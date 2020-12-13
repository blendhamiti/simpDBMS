import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Table {
    private final Path root;
    private final Collection<Column> columns;
    private Column primaryKey;
    private int rowCount;
    private final Collection<Index> indexes;

    public Table (Path root) {
        this.root = root;

        // create dir if it doesnt exist
        FileManager.getOrCreateDirectory(root);

        // create table file if it doesnt exist
        FileManager.getOrCreateFile(Paths.get(root.toString(), getFileName()));

        // fetch columns, primaryKey, rowcount if metadata file exists
        columns = new LinkedHashSet<>();
        FileManager.getOrCreateFile(Paths.get(root.toString(), "metadata.json"));
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        JSONArray jsonColumnsArray = (JSONArray) metadata.get("columns");
        for (Object jsonObject : jsonColumnsArray) {
            JSONObject jsonColumn = (JSONObject) jsonObject;
            columns.add(new Column(jsonColumn.get("name").toString(), jsonColumn.get("type").toString()));
        }
        primaryKey = getColumn(metadata.get("primaryKey").toString());
        rowCount = Integer.parseInt(metadata.get("rowCount").toString());

        // fetch indexes if any
        indexes = new LinkedHashSet<>();
        Objects.requireNonNull(FileManager.getSubDirectories(root))
                .stream()
                .filter(file -> file.getFileName().toString().startsWith("index"))
                .forEach(file -> indexes.add(new Index(file, getColumn(file.getFileName()
                        .toString().split("_")[1].split("\\.")[0]))));
    }

    public String getName() {
        return root.getFileName().toString();
    }

    public String getFileName() {
        return getName() + ".csv";
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public boolean setPrimaryKey(Column column) {
        // check that column does not have null or duplicate entries
        Set<Record> recordSet = new HashSet<>();
        for (Record record : getRows(column)) {
            if (record.isBlank()) return false;
            if (!recordSet.add(record)) return false;
        }
        primaryKey = column;
        // update metadata file
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        metadata.put("primaryKey", column.getName());
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        return true;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int setRowCount(int rowCount) {
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        metadata.put("rowCount", rowCount);
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        this.rowCount = rowCount;
        return rowCount;
    }

    public int rowCountIncrement() {
        return setRowCount(++rowCount);
    }

    public int rowCountIncrement(int increment) {
        return setRowCount(rowCount + increment);
    }

    public int rowCountDecrement() {
        return setRowCount(--rowCount);
    }

    public int rowCountDecrement(int decrement) {
        return setRowCount(rowCount - decrement);
    }

    public Collection<Column> getColumns() {
        return columns;
    }

    public Column getColumn(String name) {
        for (Column column : columns)
            if (column.getName().equals(name))
                return column;
        return null;
    }

    public boolean containsColumn(String name) {
        return getColumn(name) != null;
    }

    public boolean createColumn(String name, Type type) {
        if (containsColumn(name)) return false;
        // update metadata file
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        JSONArray jsonColumnsArray = (JSONArray) metadata.get("columns");
        JSONObject column = new JSONObject();
        column.put("name", name);
        column.put("type", type.name());
        jsonColumnsArray.add(column);
        metadata.put("columns", jsonColumnsArray);
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        // add to collection
        columns.add(new Column(name, type));
        return true;
    }

    public boolean removeColumn(String name) {
        if (!containsColumn(name)) return false;
        // update metadata file
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        JSONArray jsonColumnsArray = (JSONArray) metadata.get("columns");
        JSONObject column = null;
        for (Object jsonObject : jsonColumnsArray) {
            JSONObject jsonColumn = (JSONObject) jsonObject;
            if (jsonColumn.get("name").equals(name))
                column = jsonColumn;
        }
        jsonColumnsArray.remove(column);
        metadata.put("columns", jsonColumnsArray);
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        // remove from collection
        columns.remove(getColumn(name));
        return true;
    }

    public Collection<Index> getIndexes() {
        return indexes;
    }

    public Index getIndex(Column column) {
        for (Index i : indexes)
            if (i.getColumn().equals(column))
                return i;
        return null;
    }

    public boolean containsIndex(Column column) {
        return getIndex(column) != null;
    }

    public boolean createIndex(Column column) {
        if (containsIndex(column)) return false;
        indexes.add(new Index(Paths.get(root.toString(), "index_" + column.getName() + ".csv"), column, getRows(column)));
        return true;
    }

    public boolean removeIndex(Column column) {
        if (!containsIndex(column)) return false;
        FileManager.deleteDirectory(Paths.get(root.toString(), "index_" + column.getName() + ".csv"));
        indexes.remove(getIndex(column));
        return true;
    }

    public List<Row> getRows() {
        List<Row> rows = new ArrayList<>();
        CSVParser parser = FileManager.readCsv(
                Paths.get(root.toString(), getName() + ".csv"),
                Arrays.toString(columns.stream().map(Column::getName).toArray(String[]::new)));
        List<Record> records;
        for (CSVRecord csvRecord : parser) {
            records = new ArrayList<>();
            for (int i = 0; i < csvRecord.size(); i++)
                records.add(new Record(csvRecord.get(i), getColumn(parser.getHeaderNames().get(i)).getType()));
            rows.add(new Row(records, columns, primaryKey));
        }
        return rows;
    }

    public List<Record> getRows(Column column) {
        CSVParser parser = FileManager.readCsv(
                Paths.get(root.toString(), getName() + ".csv"),
                Arrays.toString(columns.stream().map(Column::getName).toArray(String[]::new)));
        List<Record> records = new ArrayList<>();
        for (CSVRecord csvRecord : parser)
            records.add(new Record(csvRecord.get(column.getName()), column.getType()));
        return records;
    }

    public List<Row> getRow(Column column, Record record, Filter filter) {
        // check fi record in query is blank
        if (record.getValue().isBlank()) return null;

        // prepare list for rows that match
        List<Row> rows = new ArrayList<>();

        switch (filter) {

            case EQUAL_TO:
                if (containsIndex(column)) {
                    List<Address> addresses = new ArrayList<>();
                    addresses.addAll(getIndex(column).getAddress(record));
                    List<Row> allRows = getRows();
                    for (Address address : addresses)
                        rows.add(allRows.get(address.getLine()));
                }
                else {
                    for (Row currentRow : getRows())
                        if (currentRow.getRecord(column).equals(record))
                            rows.add(currentRow);
                }

            case NOT_EQUAL_TO:
                if (containsIndex(column)) {
                    List<Address> addresses = new ArrayList<>();
                    getIndex(column).getEntries().keySet()
                            .stream()
                            .filter(rec -> !rec.equals(record))
                            .forEach(rec -> addresses.addAll(getIndex(column).getAddress(rec)));
                    List<Row> allRows = getRows();
                    for (Address address : addresses)
                        rows.add(allRows.get(address.getLine()));
                }
                else {
                    for (Row currentRow : getRows())
                        if (!currentRow.getRecord(column).equals(record))
                            rows.add(currentRow);
                }

            case LARGER_THAN:
                if (containsIndex(column)) {
                    List<Address> addresses = new ArrayList<>();
                    getIndex(column).getEntries().keySet()
                            .stream()
                            .filter(rec -> rec.compareTo(record) > 0)
                            .forEach(rec -> addresses.addAll(getIndex(column).getAddress(rec)));
                    List<Row> allRows = getRows();
                    for (Address address : addresses)
                        rows.add(allRows.get(address.getLine()));
                }
                else {
                    for (Row currentRow : getRows())
                        if (currentRow.getRecord(column).compareTo(record) > 0)
                            rows.add(currentRow);
                }

            case LARGER_THAN_OR_EQUAL_TO:
                rows.addAll(getRow(column, record, Filter.LARGER_THAN));
                rows.addAll(getRow(column, record, Filter.EQUAL_TO));

            case SMALLER_THAN:
                if (containsIndex(column)) {
                    List<Address> addresses = new ArrayList<>();
                    getIndex(column).getEntries().keySet()
                            .stream()
                            .filter(rec -> rec.compareTo(record) < 0)
                            .forEach(rec -> addresses.addAll(getIndex(column).getAddress(rec)));
                    List<Row> allRows = getRows();
                    for (Address address : addresses)
                        rows.add(allRows.get(address.getLine()));
                }
                else {
                    for (Row currentRow : getRows())
                        if (currentRow.getRecord(column).compareTo(record) < 0)
                            rows.add(currentRow);
                }

            case SMALLER_THAN_OR_EQUAL_TO:
                rows.addAll(getRow(column, record, Filter.SMALLER_THAN));
                rows.addAll(getRow(column, record, Filter.EQUAL_TO));

            default:
                rows.addAll(getRow(column, record, Filter.EQUAL_TO));
        }
        return rows;
    }

    public boolean containsRow(Row row) {
        if (primaryKey == null) return false;
        if (row.getRecord(primaryKey).isBlank()) return true;
        return getRow(primaryKey, row.getRecord(primaryKey), Filter.EQUAL_TO) != null;
    }

    public boolean addRow(List<Record> records) {
        Row row = new Row(records, columns, primaryKey);
        if (containsRow(row) || row.getRecord(primaryKey).isBlank()) return false;
        try {
            CSVPrinter printer = FileManager.appendCsv(Paths.get(root.toString(), getFileName()));
            for (Record record : records)
                printer.print(record);
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Index index : indexes)
            index.addEntry(row.getRecord(index.getColumn()), new Address(rowCountIncrement()));
        return true;
    }

    public boolean updateRow(Row row, List<Record> records) {
        return removeRow(row) && addRow(records);
    }

    public boolean removeRow(Row row) {
        if (!containsRow(row)) return false;
        List<Row> rows = getRows();
        int lineNumber = 0;
        boolean isRemoved = false;
        if (getPrimaryKey() != null && containsIndex(primaryKey)) {
            // use index to find lineNumber
            rows.removeIf(currentRow -> currentRow.equals(row));
            lineNumber = getIndex(primaryKey).getAddress(row.getRecord(primaryKey)).get(0).getLine();
            isRemoved = true;
        }
        else {
            // iterate through file to find lineNumber
            for (Row currentRow : rows) {
                lineNumber++;
                if (currentRow.equals(row)) {
                    rows.remove(currentRow);
                    isRemoved = true;
                    break;
                }
            }
        }
        if (!isRemoved) return false;
        for (Index index : indexes)
            index.removeEntry(row.getRecord(index.getColumn()), new Address(lineNumber));
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return getName().equals(table.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + getName() + '\'' +
                ", columns=" + columns +
                ", indexes=" + indexes +
                ", primaryKey=" + primaryKey +
                ", rowCount=" + rowCount +
                '}';
    }
}

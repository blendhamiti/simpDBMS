import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
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

    /*
        Use null for columns and primary key when fetching existing table
     */
    public Table (Path root, Collection<Column> columns, Column primaryKey) {
        this.root = root;

        // create dir if it doesnt exist
        FileManager.getOrCreateDirectory(root);

        // fetch or create: columns, primaryKey, rowcount
        this.columns = new LinkedHashSet<>();
        // metadata file is to be created (which means that this is a new table)
        if (FileManager.getOrCreateFile(Paths.get(root.toString(), "metadata.json"))) {
            // create metadata file
            JSONObject metadata = new JSONObject();
            JSONArray jsonColumnsArray = new JSONArray();
            metadata.put("columns", jsonColumnsArray);
            metadata.put("primaryKey", null);
            metadata.put("rowCount", 0);
            FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);

            // fetch columns and primary key
            if (columns == null) throw new IllegalArgumentException();
            columns.forEach(column -> createColumn(column.getName(), column.getType()));
            if (primaryKey != null) setPrimaryKey(primaryKey);

            // write headers in the table csv file
            try {
                CSVPrinter printer = FileManager.writeCsv(Paths.get(root.toString(), getFileName()),
                        columns.stream().map(Column::getName).toArray(String[]::new));
                if (printer == null) throw new FileNotFoundException();
                printer.close(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // metadata file exists
        else {
            JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
            JSONArray jsonColumnsArray = (JSONArray) metadata.get("columns");
            for (Object jsonObject : jsonColumnsArray) {
                JSONObject jsonColumn = (JSONObject) jsonObject;
                this.columns.add(new Column(jsonColumn.get("name").toString(), jsonColumn.get("type").toString()));
            }
            this.primaryKey = getColumn(metadata.get("primaryKey").toString());
            rowCount = Integer.parseInt(metadata.get("rowCount").toString());
        }

        // fetch indexes if any
        indexes = new LinkedHashSet<>();
        Collection<Path> indexPaths = FileManager.getSubDirectories(root);
        if (indexPaths != null) {
            indexPaths.stream()
                    .filter(file -> file.getFileName().toString().startsWith("index"))
                    .forEach(file -> indexes.add(new Index(file, getColumn(file.getFileName()
                            .toString().split("_")[1].split("\\.")[0]))));
        }
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

    /*
        primary key can be set only at time of table creation
     */
    private boolean setPrimaryKey(Column column) {
        // check that column does not have null or duplicate entries
        if (!containsColumn(column.getName())) return false;
        // check if column records are unique
        // Set<Record> recordSet = new HashSet<>();
        // for (Record record : getRows(column)) {
        //     if (record.isBlank()) return false;
        //     if (!recordSet.add(record)) return false;
        // }
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

    private int setRowCount(int rowCount) {
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        metadata.put("rowCount", rowCount);
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        this.rowCount = rowCount;
        return rowCount;
    }

    private int rowCountIncrement() {
        return setRowCount(++rowCount);
    }

    private int rowCountIncrement(int increment) {
        return setRowCount(rowCount + increment);
    }

    private int rowCountDecrement() {
        return setRowCount(--rowCount);
    }

    private int rowCountDecrement(int decrement) {
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

    private boolean createColumn(String name, Type type) {
        if (containsColumn(name)) return false;
        // if first column, create csv header
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
                Paths.get(root.toString(), getFileName()),
                columns.stream().map(Column::getName).toArray(String[]::new));
        List<Record> records;
        if (parser != null) {
            for (CSVRecord csvRecord : parser) {
                records = new ArrayList<>();
                for (int i = 0; i < csvRecord.size(); i++)
                    records.add(new Record(csvRecord.get(i), getColumn(parser.getHeaderNames().get(i)).getType()));
                rows.add(new Row(records, columns, primaryKey));
            }
        }
        return rows;
    }

    public List<Record> getRows(Column column) {
        CSVParser parser = FileManager.readCsv(
                Paths.get(root.toString(), getName() + ".csv"),
                Arrays.toString(columns.stream().map(Column::getName).toArray(String[]::new)));
        List<Record> records = new ArrayList<>();
        if (parser != null)
            for (CSVRecord csvRecord : parser)
                records.add(new Record(csvRecord.get(column.getName()), column.getType()));
        return records;
    }

    public List<Row> getRow(Column column, Record record, Filter filter) {
        // check if record in query is blank
        if (record.getValue().isBlank()) return null;

        // prepare list for rows that match
        List<Row> rows = new ArrayList<>();

        switch (filter) {

            case EQUAL_TO:
                if (containsIndex(column)) {
                    List<Address> addresses = new ArrayList<>(getIndex(column).getAddress(record));
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
        return getRow(primaryKey, row.getRecord(primaryKey), Filter.EQUAL_TO) != null;
    }

    public boolean addRow(List<Record> records) {
        Row row = new Row(records, columns, primaryKey);
        if (primaryKey != null && row.getRecord(primaryKey).isBlank()) return false;
        if (containsRow(row)) return false;
        try {
            CSVPrinter printer = FileManager.appendCsv(Paths.get(root.toString(), getFileName()));
            if (printer == null) return false;
            for (Record record : records)
                printer.print(record);
            printer.close(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rowCountIncrement();
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
        if (primaryKey != null && containsIndex(primaryKey)) {
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
        rowCountDecrement();
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
        String primaryKeyColumnName = (primaryKey == null)  ? "" : primaryKey.getName();
        return "{'" + getName() +
                "', columns=" + columns +
                ", indexes=" + indexes +
                ", primaryKey='" + primaryKeyColumnName +
                "', rowCount=" + rowCount + "}";
    }
}

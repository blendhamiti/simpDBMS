import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


public class Table {
    private Path root;
    private Collection<Column> columns;
    private Column primaryKey;
    private int rowCount;
    private Collection<Index> indexes;

    public Table (Path root) {
        this.root = root;

        // create dir if it doesnt exist
        FileManager.getOrCreateDirectory(root);

        // fetch columns, primaryKey, rowcount if metadata file exists
        columns = new LinkedHashSet<>();
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
        // update metadata file
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        metadata.put("primaryKey", column.getName());
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        return true;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        JSONObject metadata = FileManager.readJson(Paths.get(root.toString(), "metadata.json"));
        metadata.put("rowCount", rowCount);
        FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
        this.rowCount = rowCount;
    }

    public void rowCountIncrement() {
        setRowCount(++rowCount);
    }

    public void rowCountIncrement(int increment) {
        setRowCount(rowCount + increment);
    }

    public void rowCountDecrement() {
        setRowCount(--rowCount);
    }

    public void rowCountDecrement(int decrement) {
        setRowCount(rowCount - decrement);
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
            rows.add(new Row(records, columns));
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

    public Row getRow(Column column, Record record, Filter filter) {
        if (record.getValue().isBlank()) return null;
        CSVParser parser = FileManager.readCsv(
                Paths.get(root.toString(), getName() + ".csv"),
                Arrays.toString(columns.stream().map(Column::getName).toArray(String[]::new)));
        // check if index query is available
        if (containsIndex(column)) {
            //index query

        }
        // non-index query

        return null;
    }

    public void addRow(List<Record> records) {
    }

    public void updateRow(Row row, List<Record> records) {
    }

    public void removeRow(Row row) {

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Table {
    private Path root;
    private String name;
    private Collection<Column> columns;
    private Collection<Index> indexes;
    private Column primaryKey;
    private int rowCount;

    public Table (String name, Path root) {
        this.root = root;
        this.name = name;
        try {
            Files.createDirectory(Paths.get(root.toString(), name));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        columns = new HashSet<>();
        indexes = new HashSet<>();
        primaryKey = null;
        rowCount = 0;
    }

    public Table (Path table) {
        this.name = table.getFileName().toString();
        fetchMetadata(table);
        fetchIndexes(table);
    }


    private void fetchMetadata(Path table) {
        columns = new ArrayList<>();
        primaryKey = null;
        rowCount = 0;
        try {
            byte[] metadataBytes = Files.readAllBytes(
                    Files.list(table)
                        .filter(file -> file.getFileName().toString().equals("metadata.json"))
                        .findAny()
                        .get());
            String metadataStr = new String(metadataBytes);

            JSONParser parser = new JSONParser();
            try {
                JSONObject jsonMetadata = (JSONObject) parser.parse(metadataStr);
                // fetch columns
                JSONArray jsonColumnsArray = (JSONArray) jsonMetadata.get("columns");
                for (Object jsonObject : jsonColumnsArray) {
                    JSONObject jsonColumn = (JSONObject) jsonObject;
                    columns.add(new Column(jsonColumn.get("name").toString(), jsonColumn.get("type").toString()));
                }
                // fetch primary key
                primaryKey = getColumn(jsonMetadata.get("primaryKey").toString());
                // fetch row count
                rowCount = Integer.parseInt(jsonMetadata.get("rowCount").toString());
            } catch (ParseException e) {
                System.out.println(e.toString());
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private void fetchIndexes(Path table) {
        indexes = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(table)) {
            for (Path file : stream) {
                if (file.getFileName().toString().startsWith("index")) {
                    indexes.add(new Index(file, getColumn(file.getFileName().toString().split("_")[1])));
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Column primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void rowCountIncrement() {
        rowCount++;
    }

    public void rowCountIncrement(int increment) {
        rowCount += increment;
    }

    public void rowCountDecrement() {
        rowCount--;
    }

    public void rowCountDecrement(int decrement) {
        rowCount -= decrement;
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

    public void addIndex(Index index) {
        indexes.add(index);
    }

    public void removeIndex(Index index) {
        indexes.remove(index);
    }

    public Collection<Column> getColumns() {
        return columns;
    }

    public Column getColumn(String name) {
        for (Column column : columns) {
            if (column.getName().equals(name))
                return column;
        }
        return null;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void removeColumn(Column column) {
        columns.remove(column);
    }

    public List<Row> getRows() {
        return null;
    }

    public Row getRow(Column column, Record record, Filter filter) {
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
        return name.equals(table.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                ", indexes=" + indexes +
                ", primaryKey=" + primaryKey +
                ", rowCount=" + rowCount +
                '}';
    }
}

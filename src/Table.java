import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Index> indexes;
    private Column primaryKey;
    private int rowCount;

    public Table (String name) {
        this.name = name;
        columns = new ArrayList<>();
        indexes = new ArrayList<>();
        primaryKey = null;
        rowCount = 0;
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

    private void fetchColumns() {
    }

    private void fetchIndexes() {
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public Index getIndex(String name) {
        for (Index i : indexes)
            if (i.getName().equals(name))
                return i;
        return null;
    }

    public void addIndex(Index index) {
        indexes.add(index);
    }

    public void removeIndex(Index index) {
        indexes.remove(index);
    }

    public List<Column> getColumns() {
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
                '}';
    }
}

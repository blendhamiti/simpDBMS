import java.util.List;
import java.util.Objects;

public class Row {
    private List<Record> records;

    public Row(List<Record> records) {
        this.records = records;
    }

    private void fetchRow() {
    }
    
    public List<Record> getRecords() {
        return records;
    }
    
    public Record getRecord(Column column) {
        return null;
    }

    public void addRecord(Column column, Record record) {
    }

    public void updateRecord(Column column, Record record) {
    }

    public void removeRecord(Column column) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row = (Row) o;
        return records.equals(row.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(records);
    }

    @Override
    public String toString() {
        return "Row{" +
                "records=" + records +
                '}';
    }
}

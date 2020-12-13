import org.apache.commons.csv.CSVRecord;

import java.util.*;

public class Row {
    private List<Record> records;
    private Collection<Column> columns;

    public Row(List<Record> records, Collection<Column> columns) {
        this.columns = columns;
        this.records = new ArrayList<>();
        Iterator<Record> recordIterator = records.iterator();
        Iterator<Column> columnIterator = columns.iterator();
        while (recordIterator.hasNext() && columnIterator.hasNext()) {
            Record record = recordIterator.next();
            if (record.getType() != columnIterator.next().getType())
                throw new IllegalArgumentException();
            this.records.add(record);
        }
    }

    public List<Record> getRecords() {
        return records;
    }
    
    public Record getRecord(Column column) {
        Iterator<Record> recordIterator = records.iterator();
        Iterator<Column> columnIterator = columns.iterator();
        while (recordIterator.hasNext() && columnIterator.hasNext())
            if (columnIterator.next().equals(column))
                return recordIterator.next();
        return null;
    }

    public boolean updateRecord(Column column, Record record) {
        int loopCount = 0;
        for (Iterator<Column> it = columns.iterator(); it.hasNext() ; loopCount++) {
            if (it.next().equals(column)) {
                records.remove(loopCount);
                records.add(loopCount, record);
                return true;
            }
        }
        return false;
    }

    public boolean removeRecord(Column column) {
        return updateRecord(column, new Record("", column.getType()));
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

import org.apache.commons.csv.CSVRecord;

import java.util.*;

public class Row {
    private List<Record> records;

    public Row(List<Record> records) {
        this.records = records;
    }

    public Row(CSVRecord csvRecord, Collection<Column> columns) {
        records = new ArrayList<>();
        Iterator<String> csvRecordIterator = csvRecord.iterator();
        Iterator<Column> columnIterator = columns.iterator();
        while (csvRecordIterator.hasNext() && columnIterator.hasNext()) {
            records.add(new Record(csvRecordIterator.next(), columnIterator.next().getType()));
        }
        System.out.println(records);
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

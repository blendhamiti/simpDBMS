import java.nio.file.Path;
import java.util.*;

public class Index {
    private Column column;
    private Map<Record,List<Address>> entries;

    public Index(String name, Column column) {
        this.column = column;
        entries = new TreeMap<>();
    }

    public Index(Path indexFile, Column column) {
        this.column = column;
        fetchIndex(indexFile);
    }

    private void fetchIndex(Path indexFile) {
        entries = new TreeMap<>();

    }

    public Column getColumn() {
        return column;
    }

    public void addEntry(Record record) {
    }

    public void updateEntry(Address address, Record record) {
    }

    public void removeEntry(Record record) {
    }

    public List<Address> getAddress(Record record) {
        List<Address> addresses = new ArrayList<>();
        for (Record currentRecord : entries.keySet()) {
            if (currentRecord.equals(record))
                addresses.addAll(entries.get(currentRecord));
        }
        return addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return column.equals(index.column) &&
                Objects.equals(entries, index.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, entries);
    }

    @Override
    public String toString() {
        return "Index{" +
                "column=" + column +
                ", entries=" + entries +
                '}';
    }
}

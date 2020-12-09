import java.nio.file.Path;
import java.util.*;

public class Index {
    private String name;
    private Column column;
    private Map<Record,List<Address>> entries;

    public Index(String name, Column column) {
        this.name = name;
        this.column = column;
        entries = new TreeMap<>();
    }

    public Index(Path path) {
        this.name = path.getFileName().toString();
        this.column = column;
        entries = new TreeMap<>();
        fetchIndex();
    }

    private void fetchIndex() {
    }

    public String getName() {
        return name;
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
        return name.equals(index.name) &&
                column.equals(index.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, column);
    }

    @Override
    public String toString() {
        return "Index{" +
                "name='" + name + '\'' +
                ", column=" + column +
                '}';
    }
}

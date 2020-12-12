import org.apache.commons.csv.CSVParser;

import java.nio.file.Path;
import java.util.*;

public class Index {
    private Column column;

    public Index(Path filePath, Column column) {
        this.column = column;
        FileManager.getOrCreateFile(filePath);
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
        // parse entries
        Map<Record,List<Address>> entries = new HashMap<>();


        // return address
        List<Address> addresses = new ArrayList<>();
        for (Record currentRecord : entries.keySet())
            if (currentRecord.equals(record))
                addresses.addAll(entries.get(currentRecord));
        return addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return column.equals(index.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column);
    }

    @Override
    public String toString() {
        return "Index{" +
                "column=" + column +
                '}';
    }
}

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Index {
    private Path filePath;
    private Column column;
    Map<Record, List<Address>> entries;

    public Index(Path filePath, Column column) {
        this.filePath = filePath;
        this.column = column;
        entries = new TreeMap<>();
        CSVParser parser = FileManager.readCsv(filePath, column.getName(), "address");
        for (CSVRecord csvRecord : parser) {
            Record record = new Record(csvRecord.get(column.getName()), column.getType());
            Address address = new Address(Integer.parseInt(csvRecord.get("address")));
            addEntry(record, address);
        }
    }

    public Index(Path filePath, Column column, List<Record> records) {
        this.filePath = filePath;
        this.column = column;
        FileManager.getOrCreateFile(filePath);
        entries = new TreeMap<>();
        int lineCount = 0;
        for (Record record : records) {
            if (record.isBlank()) {
                lineCount++;
                continue;
            }
            addEntry(record, new Address(++lineCount));
        }
    }

    public void writeToFile() {
        try {
            CSVPrinter printer = FileManager.writeCsv(filePath, column.getName(), "address");
            if (printer != null) {
                for (Record record : entries.keySet())
                    for (Address address : entries.get(record))
                        printer.printRecord(column.getType() == Type.INTEGER ? Integer.valueOf(record.getValue()) : record.getValue(), address);
                printer.close(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Column getColumn() {
        return column;
    }

    public Map<Record, List<Address>> getEntries() {
        return entries;
    }

    public void addEntry(Record record, Address address) {
        if (record.getType() != column.getType())
            throw new IllegalArgumentException();
        if (record.isBlank())
            return;
        if (entries.containsKey(record)) {
            entries.get(record).add(address);
            return;
        }
        List<Address> addresses = new LinkedList<>();
        addresses.add(address);
        entries.put(record, addresses);
    }

    public void removeEntry(Record record, Address address) {
        // remove entry
        if (record.getType() != column.getType()) throw new IllegalArgumentException();
        if (entries.get(record).size() <= 1)
            entries.remove(record);
        else
            entries.get(record).remove(address);
        // decrement all addresses of entries below
        for (Record entry : entries.keySet()) {
            for (Address entryAddress : entries.get(entry))
                if (entryAddress.getLine() > address.getLine())
                    entryAddress.setLine(entryAddress.getLine() - 1);
        }
    }

    public List<Address> getAddress(Record record) {
        return entries.get(record);
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
        return "'" + column.getName() + "'";
    }
}

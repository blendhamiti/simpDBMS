import java.util.Objects;

public class Address implements Comparable<Address> {
    private final int line;

    public Address(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return line == address.line;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line);
    }

    @Override
    public String toString() {
        return "" + line;
    }

    @Override
    public int compareTo(Address o) {
        Integer thisLine = line;
        Integer thatLine = o.line;
        return thisLine.compareTo(thatLine);
    }
}

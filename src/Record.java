import java.rmi.UnexpectedException;
import java.util.InputMismatchException;
import java.util.Objects;

public class Record implements Comparable<Record> {
    private Object value;
    private Type type;

    public Record(Object value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return value.equals(record.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public int compareTo(Record o) {
        if      (type == Type.STRING && o.type == Type.STRING) {
            String thisValue = (String) value;
            String thatValue = (String) o.value;
            return thisValue.compareTo(thatValue);
        }
        else if (type == Type.INTEGER && o.type == Type.INTEGER) {
            Integer thisValue = (Integer) value;
            Integer thatValue = (Integer) o.value;
            return thisValue.compareTo(thatValue);
        }
        throw new InputMismatchException();
    }
}

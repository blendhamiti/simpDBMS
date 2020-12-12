import java.util.Objects;

public class Record implements Comparable<Record> {
    private String value;
    private Type type;

    public Record(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
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
        return value.equals(record.value) &&
                type == record.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public int compareTo(Record o) {
        if (type == Type.INTEGER && o.type == Type.INTEGER)
            return Integer.valueOf(value).compareTo(Integer.valueOf(o.value));
        return value.compareTo(o.value);
    }
}

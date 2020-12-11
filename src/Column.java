import java.util.Objects;

public class Column {
    private String name;
    private Type type;

    public Column(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Column(String name, String type) {
        this.name = name;
        if      (type.equals(Type.INTEGER.name()))
            this.type = Type.INTEGER;
        else if (type.equals(Type.STRING.name()))
            this.type = Type.STRING;
        else
            throw new IllegalArgumentException();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return name.equals(column.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

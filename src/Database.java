import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database {
    private String name;
    private List<Table> tables;

    public Database(String name) {
        this.name = name;
        tables = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void fetchTables() {
    }

    public List<Table> getTables() {
        return tables;
    }

    public Table getTable(String name) {
        for (Table table : tables) {
            if (table.getName().equals(name))
                return table;
        }
        return null;
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public void removeTable(Table table) {
        tables.remove(table);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return name.equals(database.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Database{" +
                "name='" + name + '\'' +
                ", tables=" + tables +
                '}';
    }
}


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Database {
    private final Path root;
    private final Collection<Table> tables;

    public Database(Path root) {
        this.root = root;
        FileManager.getOrCreateDirectory(root);
        tables = new HashSet<>();
        Collection<Path> tablePaths = FileManager.getSubDirectories(root);
        if (tablePaths != null) {
            for (Path tablePath : tablePaths) {
                if (FileManager.isEmpty(tablePath))
                    FileManager.deleteDirectory(tablePath);
                else
                    tables.add(new Table(tablePath, null, null));
            }
        }
    }

    public String getName() {
        return root.getFileName().toString();
    }

    public Collection<Table> getTables() {
        return tables;
    }

    public Table getTable(String name) {
        for (Table table : tables)
            if (table.getName().equals(name))
                return table;
        return null;
    }

    public boolean containsTable(String name) {
        return getTable(name) != null;
    }

    public Table createTable(String name, Collection<Column> columns, Column primaryKey) {
        if (containsTable(name)) return null;
        Table table = new Table(Paths.get(root.toString(), name), columns, primaryKey);
        tables.add(table);
        return table;
    }

    public boolean removeTable(String name) {
        if (!containsTable(name)) return false;
        FileManager.clearDirectory(Paths.get(root.toString(), name));
        tables.remove(getTable(name));
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Database database = (Database) o;
        return getName().equals(database.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Database='" + getName() + "'";
    }
}


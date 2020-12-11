import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Database {
    private Path root;
    private String name;
    private Collection<Table> tables;

    public Database(String name, Path root) {
        this.name = name;
        this.root = Paths.get(root.toString(), name);
        FileManager.createDirectory(root);
        tables = new HashSet<>();
    }

    public Database(Path database) {
        root = database;
        name = database.getFileName().toString();
        tables = new HashSet<>();
        Objects.requireNonNull(FileManager.getSubDirectories(root))
                .forEach(dir -> tables.add(new Table(dir)));
    }

    public String getName() {
        return name;
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

    public boolean createTable(String name) {
        if (containsTable(name)) return false;
        tables.add(new Table(name, Paths.get(root.toString(), this.name)));
        return true;
    }

    public boolean removeTable(String name) {
        if (!containsTable(name)) return false;
        return FileManager.deleteDirectory(Paths.get(root.toString(), name), true);
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


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
        this.root = root;
        this.name = name;
        try {
            Files.createDirectory(Paths.get(root.getFileName().toString(), name));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        tables = new HashSet<>();
    }

    public Database(Path database) {
        name = database.getFileName().toString();
        fetchTables(database);
    }

    private void fetchTables(Path database) {
        tables = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(database)) {
            for (Path table : stream)
                tables.add(new Table(table));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Collection<Table> getTables() {
        return tables;
    }

    public Table getTable(String name) {
        for (Table table : tables) {
            if (table.getName().equals(name))
                return table;
        }
        return null;
    }

    public boolean containsTable(String name) {
        return getTable(name) != null;
    }

    public boolean createTable(String name) {
        if (containsTable(name)) return false;
        Table table = new Table(name, Paths.get(root.toString(), this.name));
        tables.add(table);
        return true;
    }

    public boolean removeTable(Table table) {
        return tables.remove(table);
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


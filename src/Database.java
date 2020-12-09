import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public Database(Path path) {
        name = path.getFileName().toString();
        tables = new ArrayList<>();
        fetchTables(path);
    }

    private void fetchTables(Path path) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream)
                tables.add(new Table(file));
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


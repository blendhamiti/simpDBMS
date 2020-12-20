import java.nio.file.*;
import java.util.*;

public class Connection {
    private final Path root;
    private final Collection<Database> databases;

    public Connection(Path root) {
        this.root = root;
        FileManager.getOrCreateDirectory(root);
        databases = new HashSet<>();
        Objects.requireNonNull(FileManager.getSubDirectories(root))
            .forEach(dir -> databases.add(new Database(dir)));
    }

    public void close() {
        databases.forEach(database -> database.getTables()
                        .forEach(table -> table.getIndexes()
                                .forEach(index -> index.printIndexToFile())));
    }

    public Collection<Database> getDatabases() {
        return databases;
    }

    public Database getDatabase(String name) {
        for (Database database : databases)
            if (database.getName().equals(name))
                return database;
        return null;
    }

    public boolean containsDatabase(String name) {
        return getDatabase(name) != null;
    }

    public boolean createDatabase(String name) {
        if (containsDatabase(name)) return false;
        databases.add(new Database(Paths.get(root.toString(), name)));
        return true;
    }

    public boolean removeDatabase(String name) {
        if (!containsDatabase(name)) return false;
        return FileManager.deleteDirectory(Paths.get(root.toString(), name), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return root.equals(that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "path='" + root + '\'' +
                ", databases=" + databases +
                '}';
    }

    public static void main(String[] args) {

        Path path = FileSystems.getDefault().getPath("data");
        Connection connection = new Connection(path);
        System.out.println(connection.toString());

//        connection.createDatabase("als");
//        connection.getDatabase("als").createTable("ggh");

//        System.out.println(connection.removeDatabase("als"));



    }
}

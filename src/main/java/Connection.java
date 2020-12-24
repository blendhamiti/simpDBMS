import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Connection {
    private final Path root;
    private final Collection<Database> databases;

    public Connection(Path root) {
        this.root = root;
        FileManager.getOrCreateDirectory(root);
        databases = new HashSet<>();
        Collection<Path> databasePaths = FileManager.getSubDirectories(root);
        if (databasePaths != null) {
            for (Path databasePath : databasePaths) {
                if (FileManager.isEmpty(databasePath))
                    FileManager.deleteDirectory(databasePath);
                else
                    databases.add(new Database(databasePath));
            }
        }
    }

    public void close() {
        databases.forEach(database -> database.getTables()
                        .forEach(table -> table.getIndexes()
                                .forEach(Index::printIndexToFile)));
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

    public Database createDatabase(String name) {
        if (containsDatabase(name)) return null;
        Database database = new Database(Paths.get(root.toString(), name));
        databases.add(database);
        return database;
    }

    public boolean removeDatabase(String name) {
        if (!containsDatabase(name)) return false;
        FileManager.clearDirectory(Paths.get(root.toString(), name));
        databases.remove(getDatabase(name));
        return true;
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
                "path='" + root + "'}";
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

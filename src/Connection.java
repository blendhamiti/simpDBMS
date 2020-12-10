import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Connection {
    private Path root;
    private Collection<Database> databases;

    public Connection(Path root) {
        this.root = root;
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        fetchDatabases();
    }

    private void fetchDatabases() {
        databases = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path database : stream)
                databases.add(new Database(database));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public Collection<Database> getDatabases() {
        return databases;
    }

    public Database getDatabase(String name) {
        for (Database database : databases) {
            if (database.getName().equals(name))
                return database;
        }
        return null;
    }

    public boolean containsDatabase(String name) {
        return getDatabase(name) != null;
    }

    public boolean createDatabase(String name) {
        if (containsDatabase(name)) return false;
        Database database = new Database(name, root);
        databases.add(database);
        return true;
    }

    public boolean removeDatabase(String name) {
        if (!containsDatabase(name)) return false;
        try {
            Files.walk(Paths.get(root.toString(), name))
                    .map(Path::toFile)
                    .sorted(Comparator.reverseOrder())
                    .forEachOrdered(File::delete);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        if (!Files.exists(Paths.get(root.toString(), name))) {
            databases.remove(getDatabase(name));
            return true;
        }
        return false;
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
        // list directory contents
//        Path path = FileSystems.getDefault().getPath("data", "company");
//        System.out.println(Files.exists(path));
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
//            for (Path file : stream) {
//                System.out.println(file.getFileName());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Path path2 = FileSystems.getDefault().getPath("data");
//        try {
//            Files.createDirectory(path2);
//        } catch (FileAlreadyExistsException e) {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            Files.walk(Paths.get(path.getFileName().toString()))
//                .forEach(System.out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Path path = FileSystems.getDefault().getPath("data");
        Connection connection = new Connection(path);
        System.out.println(connection.toString());

//        connection.createDatabase("als");
//        connection.getDatabase("als").createTable("ggh");

//        System.out.println(connection.removeDatabase("als"));



    }
}

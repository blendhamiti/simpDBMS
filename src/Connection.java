import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Connection {
    private Path path;
    private List<Database> databases;

    public Connection(Path path) {
        this.path = path;
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        databases = new ArrayList<>();
        fetchDatabases();
    }

    private void fetchDatabases() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path dir : stream)
                databases.add(new Database(dir));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public Database getDatabase(String name) {
        for (Database database : databases) {
            if (database.getName().equals(name))
                return database;
        }
        return null;
    }

    public void addDatabase(Database database) {
        databases.add(database);
    }

    public void removeDatabase(Database database) {
        databases.remove(database);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "path='" + path + '\'' +
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

        Path path = FileSystems.getDefault().getPath("data");
        Connection connection = new Connection(path);





    }
}

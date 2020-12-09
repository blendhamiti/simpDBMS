import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Connection {
    private String path;
    private List<Database> databases;

    public Connection(String path) {
        this.path = path;
        databases = new ArrayList<>();
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
}

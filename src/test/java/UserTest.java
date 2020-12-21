import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class UserTest {
    private static final Path root = FileSystems.getDefault().getPath("src", "test", "resources", "databases-test");

    @Test
    public void createSampleDatabase() {
        FileManager.deleteDirectory(Paths.get(root.toString(), "data"), true);
        Connection connection = new Connection(Paths.get(root.toString(), "data"));
        Database database = connection.createDatabase("company");

        Collection<Column> columns = new LinkedHashSet<>();
        columns.add(new Column("name", Type.STRING));
        columns.add(new Column("age", Type.INTEGER));
        Table table = database.createTable("employees", columns, new Column("name", Type.STRING));

        List<Record> row1 = new ArrayList<>();
        row1.add(new Record("blend", Type.STRING));
        row1.add(new Record("22", Type.INTEGER));
        List<Record> row2 = new ArrayList<>();
        row2.add(new Record("eranda", Type.STRING));
        row2.add(new Record("12", Type.INTEGER));
        table.addRow(row1);
        table.addRow(row2);

        table.createIndex(new Column("name", Type.STRING));
        table.createIndex(new Column("age", Type.INTEGER));

        connection.close();

        for (Record record : table.getRows(new Column("name", Type.STRING)))
            System.out.println(record);
    }
}

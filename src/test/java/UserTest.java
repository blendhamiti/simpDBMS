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

    @Test
    public void queryRecordTest() {
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

        System.out.println(table.getRow(new Column("name", Type.STRING), new Record("era", Type.STRING), Filter.NOT_EQUAL_TO));
    }

    @Test
    /*
        Tests both removeRecord and addRecord
     */
    public void updateRecordTest() {
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

        List<Row> rows = table.getRow(new Column("name", Type.STRING), new Record("blend", Type.STRING), Filter.EQUAL_TO);
        List<Record> row3 = new ArrayList<>();
        row3.add(new Record("aferdita", Type.STRING));
        row3.add(new Record("41", Type.INTEGER));
        table.updateRow(rows.get(0), row3);

        table.getRows().forEach(System.out::println);

        connection.close();
    }
}

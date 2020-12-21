import org.junit.Assert;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

public class TableTest {
    private static final Path root = FileSystems.getDefault().getPath("src", "test", "resources", "databases-test");

    @Test
    public void createTableTest() {
        Collection<Column> columns = new LinkedHashSet<>();
        columns.add(new Column("id", Type.INTEGER));
        columns.add(new Column("brand", Type.STRING));
        columns.add(new Column("year", Type.INTEGER));
        Column primaryKey = new Column("id", Type.INTEGER);
        Table table = new Table(Paths.get(root.toString(), "phones"), columns, primaryKey);
        System.out.println(table);
    }

    @Test
    public void fetchTableTest() {
        Table table = new Table(Paths.get(root.toString(), "phones"), null, null);
        System.out.println(table);
    }

    // @Test
    // public void rowCountTest() {
    //     Table table = new Table(Paths.get(root.toString(), "phones"), null, null);
    //     table.setRowCount(3);
    // }

    @Test
    public void createIndexInEmptyTableTest() {
        
    }

    @Test
    public void createIndexInNonEmptyTableTest() {

    }

}

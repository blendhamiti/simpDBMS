import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IndexTest {
    private static final Path root = FileSystems.getDefault().getPath("src", "test", "resources", "databases-test");

    @Test
    public void getIndexFromFileTest() {
        Index index = new Index(Paths.get(root.toString(), "file.csv"), new Column("name", Type.STRING));
        System.out.println(index.getEntries().toString());
    }

    @Test
    public void createIndexAndPrintIndexToFileTest() {
        List<Record> records = new ArrayList<>();
        records.add(new Record("blend", Type.STRING));
        records.add(new Record("blends", Type.STRING));
        Index index = new Index(Paths.get(root.toString(), "file.csv"), new Column("name", Type.STRING), records);
        index.writeToFile();
        index = new Index(Paths.get(root.toString(), "file.csv"), new Column("name", Type.STRING));
        System.out.println(index.getEntries().toString());
    }


}

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static void main(String[] args) {
        Connection connection = new Connection(FileSystems.getDefault().getPath("databases"));
        // connection.createDatabase("vehicles");
        connection.getDatabase("vehicles").createTable("trucks");
        connection.getDatabase("vehicles").getTable("trucks").createColumn("brand", Type.STRING);
        connection.getDatabase("vehicles").getTable("trucks").createColumn("amount", Type.INTEGER);
        List<Record> recordList = new ArrayList<>();
        recordList.add(new Record("tesla-truck", Type.STRING));
        recordList.add(new Record("2", Type.INTEGER));
        connection.getDatabase("vehicles").getTable("trucks").addRow(recordList);



        // Path root = FileSystems.getDefault().getPath("data");
        // FileManager.getOrCreateFile(Paths.get(root.toString(), "metadata.json"));
        // JSONObject metadata = new JSONObject();
        // JSONArray jsonColumnsArray = new JSONArray();
        // metadata.put("columns", jsonColumnsArray);
        // metadata.put("primaryKey", "");
        // metadata.put("rowCount", 0);
        // FileManager.writeJson(Paths.get(root.toString(), "metadata.json"), metadata);
    }
}

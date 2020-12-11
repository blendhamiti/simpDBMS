import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("data");
        Connection conn = new Connection(path);
        System.out.println(conn);


        Paths.get("data","company", "employees", "index_id.csv");
        // FileManager.getOrCreateFile(Paths.get("data","blends", "employees", "index_ids.csv"));


    }
}

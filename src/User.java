import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.*;
import java.util.*;

public class User {

    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("data");
        // Connection conn = new Connection(path);
        // System.out.println(conn);

        Path path2 = Paths.get("data","blends", "employees", "index_id.csv");

        CSVParser parser = FileManager.readCsv(path2, "id", "address");
        int lineNumber = 0;
        for (CSVRecord csvRecord : parser) {
            lineNumber++;
            if (csvRecord.get(0).equals("ids")) {
                break;
            }
        }

        try {
            Writer out = Files.newBufferedWriter(path2, StandardOpenOption.WRITE);
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("id", "address"));

            // printer.
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}

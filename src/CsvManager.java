import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CsvManager {

    public static CSVParser parse(Path path) {
        try {
            Reader in = Files.newBufferedReader(path);
            return new CSVParser(in, CSVFormat.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CSVParser parseIndex(Path path) {
        try {
            Reader in = Files.newBufferedReader(path);
            return new CSVParser(in, CSVFormat.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        Paths.get("data","company", "employees", "index_id.csv");
        Connection conn = new Connection(Paths.get("data"));
        System.out.println(conn.toString());
        System.out.println(conn.getDatabase("company").getTable("employees").getRows());

        // CSVParser parser = CsvManager.parse(Paths.get("data","company", "employees", "employees.csv"));
        // CSVRecord header = null;
        // for (CSVRecord csvRecord : parser) {
        //     if (header == null) {
        //         header = csvRecord;
        //         continue;
        //     }
        //     System.out.println(csvRecord);
        // }

    }
}

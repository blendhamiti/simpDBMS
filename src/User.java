import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class User {

    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("data");
        // Connection conn = new Connection(path);
        // System.out.println(conn);

        Path path2 = Paths.get("data","blends", "employees");

        // getting line number
        CSVParser parser = FileManager.readCsv(Paths.get(path2.toString(), "index_id.csv"));
        for (CSVRecord csvRecord : parser) {
            System.out.println(csvRecord);
        }

        try {
            CSVPrinter printer = FileManager.appendCsv(Paths.get(path2.toString(), "index_id.csv"));
            List<Record> records = new ArrayList<>();
            records.add(new Record("qq", Type.STRING));
            records.add(new Record("qqq", Type.STRING));
            // printer.printRecord(records);
            // printer.printRecord(records.get(0), records.get(1));
            printer.print(records.get(0));
            printer.print(records.get(1));
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

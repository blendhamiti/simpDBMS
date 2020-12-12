import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class User {

    public static void main(String[] args) {
        Path path = FileSystems.getDefault().getPath("data");
        Connection conn = new Connection(path);
        System.out.println(conn);

        Path path2 = Paths.get("data","blends", "employees", "index_id.csv");

        try {
            CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(path2, StandardOpenOption.APPEND), CSVFormat.DEFAULT.withHeader("id", "address"));
            printer.printRecord("asd", "asdasd");
            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try {
        //     CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(filePath, StandardOpenOption.APPEND), CSVFormat.DEFAULT);
        //     printer.printRecord(column.getType() == Type.INTEGER ? Integer.valueOf(record.getValue()) : record.getValue(), address.getLine());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }


    }
}

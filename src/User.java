import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.*;

public class User {

    public static void main(String[] args) {
        Path root = null;
        // parse arguments
        if (args.length < 1) {
            System.out.println("Please specify the database path.");
        }
        else if (args.length == 1) {
            root = FileSystems.getDefault().getPath(args[0]);
        }
        else {
            System.out.println("Too many arguments.");
        }
        // create connection
        Connection conn = new Connection(root);
        // attach cli to connection
        CommandLineInterface.to(conn);
        // close connection
        conn.close();

    }
}

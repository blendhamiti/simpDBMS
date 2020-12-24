import java.nio.file.FileSystems;
import java.nio.file.Path;

public class User {

    public static void main(String[] args) {
        // Path root = null;
        Path root = FileSystems.getDefault().getPath("src", "main", "resources", "databases");
        // parse arguments
        // if (args.length < 1) {
        //     System.out.println("Please specify the database path.");
        //     System.exit(0);
        // }
        // else if (args.length == 1) {
        //     root = FileSystems.getDefault().getPath(args[0]);
        // }
        // else {
        //     System.out.println("Too many arguments.");
        //     System.exit(0);
        // }
        // attach cli to connection
        CommandLineInterface.to(new Connection(root));
    }
}

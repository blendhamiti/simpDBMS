import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class CommandLineInterface {

    public static void to(Connection conn) {
        if (conn == null) throw new IllegalArgumentException();
        boolean isTerminated = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("Instructions: ...");
        while (!isTerminated) {
            System.out.print("cli > ");
            // parse and format cli arguments
            String[] argumentsCaseInsensitive = sc.nextLine().split(" ");
            String[] arguments = new String[argumentsCaseInsensitive.length];
            for (int i = 0; i < arguments.length; i++) {
                if (argumentsCaseInsensitive[i].startsWith("\""))
                    arguments[i] = argumentsCaseInsensitive[i];
                else
                    arguments[i] = argumentsCaseInsensitive[i].trim().toLowerCase();
            }

            System.out.println("Command is: " + Arrays.toString(arguments));

            switch (arguments[0]) {
                case "load":
                case "show":
                case "exit":
                    if (arguments.length == 1) {
                        System.out.println("Exiting...");
                        isTerminated = true;
                    }
                    else {
                        System.out.println("Argument \"" + arguments[1] + "\" is not recognized.");
                    }
                    break;

                default:
                    System.out.println("Command \"" + arguments[0] + "\" is not found.");
                    break;
            }
        }
        sc.close();
    }
}

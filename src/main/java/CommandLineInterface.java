import java.util.Arrays;
import java.util.Scanner;

public class CommandLineInterface {
    private static String USER_INPUT_SIGN = "user > ";
    private static String TERMINATING_MESSAGE = "Exiting...";
    private static String HELP_GENERAL =
            "Command: SHOW [DATABASE] [TABLE]\n" +
            "\tDescription:\n" +
            "\t\tdisplays all databases\n" +
            "\tArguments:\n" +
            "\t\tDATABASE   => use 'database name' to display that database's tables\n" +
            "\t\tTABLE      => use 'table name' to display that table's columns, indexes, primary key and row count\n";
    private static String HELP_SHOW = "";
    private static String HELP_EXIT = "";

    private static String CONN_DB_NOT_FOUND = "Database was not found: ";
    private static String CONN_TABLE_NOT_FOUND = "Table was not found: ";

    private static String ERROR_CMD_NOT_FOUND = "Command was not found: ";
    private static String ERROR_ARG_NOT_RECOGNIZED = "Argument was not recognized: ";
    private static String ERROR_NOT_ENOUGH_ARGS = "There are not enough arguments";

    public static void to(Connection connection) {
        if (connection == null) throw new IllegalArgumentException();
        boolean isTerminated = false;
        Scanner sc = new Scanner(System.in);
        System.out.println(HELP_GENERAL);
        while (!isTerminated) {
            System.out.print(USER_INPUT_SIGN);
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
                case "show":
                    switch (arguments.length) {
                        case 1:
                            System.out.println(connection.getDatabases());
                            break;

                        case 2:
                            if (connection.containsDatabase(arguments[1])) {
                                System.out.println(connection.getDatabase(arguments[1]).getTables());
                            }
                            else {
                                System.out.println(CONN_DB_NOT_FOUND + arguments[1]);
                            }
                            break;

                        case 3:
                            if      (!connection.containsDatabase(arguments[1])) {
                                System.out.println(CONN_DB_NOT_FOUND + arguments[1]);
                            }
                            else if (connection.getDatabase(arguments[1]).containsTable(arguments[2])) {
                                System.out.println(connection.getDatabase(arguments[1]).getTable(arguments[2]));
                            }
                            else {
                                System.out.println(CONN_TABLE_NOT_FOUND + arguments[2]);
                            }
                            break;
                    }
                    break;

                case "exit":
                    if (arguments.length == 1) {
                        System.out.println(TERMINATING_MESSAGE);
                        isTerminated = true;
                    }
                    else {
                        System.out.println(ERROR_ARG_NOT_RECOGNIZED + arguments[1]);
                    }
                    break;

                default:
                    System.out.println(ERROR_CMD_NOT_FOUND + arguments[0]);
                    break;
            }
        }
        sc.close();
    }
}

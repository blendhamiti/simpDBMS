import java.util.Arrays;
import java.util.Scanner;

public class CommandLineInterface {
    private static String USER_INPUT_SIGN = "> ";
    private static String TERMINATING_MESSAGE = "Exiting...";

    public static void to(Connection connection) {
        if (connection == null) throw new IllegalArgumentException();
        String result = "";
        boolean isTerminated = false;

        QueryParser parser = new QueryParser(connection);
        System.out.println(parser.getHelpGeneral());

        Scanner sc = new Scanner(System.in);
        while (!isTerminated) {
            System.out.print(parser.getLoadedDatabaseName() + USER_INPUT_SIGN);
            result = parser.parse(sc.nextLine());

            // for debugging show command
            System.out.println(parser.getCommand());

            System.out.println(result);
            isTerminated = parser.connectionIsClosed();
        }
        System.out.println(TERMINATING_MESSAGE);
        sc.close();
    }
}

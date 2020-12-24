import java.util.Arrays;
import java.util.Scanner;

public class CommandLineInterface {
    private static String USER_INPUT_SIGN = "> ";
    private static String TERMINATING_MESSAGE = "Exiting...";

    public static void attachTo(QueryParser parser, boolean debuggingMode) {
        if (parser == null) throw new IllegalArgumentException();
        String result = "";
        boolean isTerminated = false;

        System.out.println(parser.getHelpGeneral());

        Scanner sc = new Scanner(System.in);
        while (!isTerminated) {
            System.out.print(parser.getLoadedDatabaseName() + USER_INPUT_SIGN);

            if (debuggingMode) {
                result = parser.parse(sc.nextLine());
                System.out.println(parser.getCommand());
                System.out.println(result);
            }
            else {
                System.out.println(parser.parse(sc.nextLine()));
            }

            isTerminated = parser.connectionIsClosed();
        }
        System.out.println(TERMINATING_MESSAGE);
        sc.close();
    }
}

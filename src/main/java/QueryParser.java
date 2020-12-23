import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QueryParser {
    private Connection connection;
    private String result;
    private String command;
    private static final String[] commands = {"show", "load", "create", "remove", "get", "add", "update", "delete"};
    private Database loadedDatabase;

    private static String DB_NOT_FOUND = "Database was not found: ";
    private static String TABLE_NOT_FOUND = "Table was not found: ";
    private static String INDEX_NOT_FOUND = "Index was not found: ";
    private static String COLUMN_NOT_FOUND = "Column was not found: ";
    private static String DB_EXISTS = "Database exists: ";
    private static String TABLE_EXISTS = "Table exists: ";
    private static String NO_LOADED_DB_OR_TABLE_EXISTS = "There is no loaded database, or table exists: ";

    private static String HELP_GENERAL =
            "Available commands are: " + Arrays.asList(commands).toString() + "\n" +
            "Use \"help [COMMAND]\" for the detailed description of the command.";
    private static String HELP_SHOW =
            "Command: show [DATABASE] [TABLE]\n" +
            "\tDescription:\n" +
            "\t\tdisplays all databases\n" +
            "\tArguments:\n" +
            "\t\tDATABASE   => use 'database name' to display that database's tables\n" +
            "\t\tTABLE      => use 'table name' to display that table's columns, indexes, primary key and row count\n";
    private static String HELP_LOAD =
            "Command: load [DATABASE]\n" +
            "\tDescription:\n" +
            "\t\tloads the specified database,if [DATABASE] is used, otherwise unloads the currently loaded database\n" +
            "\tArguments:\n" +
            "\t\tDATABASE   => specify the database name\n";
    private static String HELP_CREATE =
            "[1] Command: create database DATABASE\n" +
            "\tDescription:\n" +
            "\t\tcreates database with specified name\n" +
            "\tArguments:\n" +
            "\t\tDATABASE   => specify the database name\n" +
            "[2] Command: create table TABLE columns COLUMNS [PK]\n" +
            "\tDescription:\n" +
            "\t\tcreates table with specified columns (see below for COLUMNS format), and optionally the primary key column \n" +
            "\tArguments:\n" +
            "\t\tTABLE      => specify the table name\n" +
            "\t\tCOLUMNS    => specify the columns as \"(column_name,column_type;column_name,column_type;...)\" with type: {'STRING', 'INTEGER'}\n" +
            "\t\tPK     => specify the primary key column name\n" +
            "[3] Command: create table TABLE index COLUMN\n" +
            "\tDescription:\n" +
            "\t\tcreates index of specified column\n" +
            "\tArguments:\n" +
            "\t\tTABLE      => specify the table name\n" +
            "\t\tCOLUMN     => specify the column name\n";
    private static String HELP_REMOVE =
            "[1] Command: remove database DATABASE\n" +
            "\tDescription:\n" +
            "\t\tcreates database with specified name\n" +
            "\tArguments:\n" +
            "\t\tDATABASE   => specify the database name\n" +
            "[2] Command: remove table TABLE\n" +
            "\tDescription:\n" +
            "\t\tremoves table with specified name\n" +
            "\tArguments:\n" +
            "\t\tTABLE      => specify the table name\n" +
            "[3] Command: remove table TABLE index COLUMN\n" +
            "\tDescription:\n" +
            "\t\tremoves index of specified column\n" +
            "\tArguments:\n" +
            "\t\tTABLE      => specify the table name\n" +
            "\t\tCOLUMN     => specify the column name\n";
    private static String HELP_GET = "";
    private static String HELP_ADD = "";
    private static String HELP_UPDATE = "";
    private static String HELP_DELETE = "";

    private static String HELP_EXIT =
            "Command: EXIT\n" +
            "\tDescription:\n" +
            "\t\tcloses connection and exits the program\n";

    private static String ERROR_CMD_NOT_FOUND = "Command was not found: ";
    private static String ERROR_ARG_NOT_RECOGNIZED = "Argument was not recognized: ";
    private static String ERROR_NOT_ENOUGH_ARGS = "There are not enough arguments";

    public QueryParser(Connection connection) {
        this.connection = connection;
        this.result = "";
        this.command = "";
    }

    public String parse(String line) {
        String[] argumentsCaseInsensitive = line.split(" ");
        String[] arguments = new String[argumentsCaseInsensitive.length];
        for (int i = 0; i < arguments.length; i++) {
            if (argumentsCaseInsensitive[i].startsWith("\""))
                arguments[i] = argumentsCaseInsensitive[i];
            else
                arguments[i] = argumentsCaseInsensitive[i].trim().toLowerCase();
        }

        command = "Command is: " + Arrays.toString(arguments);

        switch (arguments[0]) {
            case "show":
                switch (arguments.length) {
                    case 1:
                        result = connection.getDatabases().toString();
                        break;

                    case 2:
                        if (connection.containsDatabase(arguments[1])) {
                            result = connection.getDatabase(arguments[1]).getTables().toString();
                        }
                        else {
                            result = DB_NOT_FOUND + arguments[1];
                        }
                        break;

                    case 3:
                        if      (!connection.containsDatabase(arguments[1])) {
                            result = DB_NOT_FOUND + arguments[1];
                        }
                        else if (connection.getDatabase(arguments[1]).containsTable(arguments[2])) {
                            result = connection.getDatabase(arguments[1]).getTable(arguments[2]).toString();
                        }
                        else {
                            result = TABLE_NOT_FOUND + arguments[2];
                        }
                        break;

                    default:
                        result = HELP_SHOW;
                        break;
                }
                break;

            case "load":
                if (arguments.length == 1) {
                    loadedDatabase = null;
                    result = "";
                }
                else if (arguments.length == 2) {
                    if (connection.containsDatabase(arguments[1])) {
                        loadedDatabase = connection.getDatabase(arguments[1]);
                        result = loadedDatabase.toString();
                    }
                    else {
                        result = DB_NOT_FOUND + arguments[1];
                    }
                }
                else {
                    result = HELP_LOAD;
                }
                break;

            case "create":
                if (arguments.length >= 3) {
                    switch (arguments[1]) {
                        case "database":
                            if (arguments.length == 3) {
                                if (!connection.containsDatabase(arguments[2]))
                                    connection.createDatabase(arguments[2]);
                                else
                                    result = DB_EXISTS + arguments[2];
                            }
                            else {
                                result = HELP_CREATE;
                            }
                            break;

                        case "table":
                            if (arguments.length >= 5) {
                                switch (arguments[3]) {
                                    case "columns":
                                        if (loadedDatabase != null && !loadedDatabase.containsTable(arguments[2])) {
                                            String columnsString = arguments[4].substring(1, arguments[4].length() - 1);
                                            List<Column> columns = new ArrayList<>();
                                            for (String columnString : columnsString.split(";")) {
                                                String[] columnDataString = columnString.split(",");
                                                columns.add(new Column(columnDataString[0], columnDataString[1].toUpperCase()));
                                            }
                                            if (!columns.isEmpty()) {
                                                if (arguments.length == 5) {
                                                    loadedDatabase.createTable(arguments[2], columns, null);
                                                    result = parse("show" + " " + loadedDatabase.getName());
                                                }
                                                else if (arguments.length == 6) {
                                                    Column primaryKey = null;
                                                    for (Column column : columns)
                                                        if (column.getName().equals(arguments[5]))
                                                            primaryKey = column;
                                                    loadedDatabase.createTable(arguments[2], columns, primaryKey);
                                                    result = parse("show" + " " + loadedDatabase.getName());
                                                }
                                                else {
                                                    result = HELP_CREATE;
                                                }
                                            }
                                            else {
                                                result = HELP_CREATE;
                                            }
                                        }
                                        else {
                                            result = NO_LOADED_DB_OR_TABLE_EXISTS + arguments[2];
                                        }
                                        break;

                                    case "index":
                                        if (arguments.length == 5) {
                                            if (loadedDatabase != null && loadedDatabase.containsTable(arguments[2])) {
                                                if (loadedDatabase.getTable(arguments[2]).createIndex(loadedDatabase.getTable(arguments[2]).getColumn(arguments[4])))
                                                    result = parse("show" + " " + loadedDatabase.getName() + " " + loadedDatabase.getTable(arguments[2]));
                                                else
                                                    result = COLUMN_NOT_FOUND + arguments[4];
                                            }
                                            else {
                                                result = TABLE_NOT_FOUND + arguments[2];
                                            }
                                        }
                                        else {
                                            result = HELP_CREATE;
                                        }
                                        break;

                                    default:
                                        result = HELP_CREATE;
                                        break;
                                }
                            }
                            break;

                        default:
                            result = HELP_CREATE;
                            break;
                    }
                }
                else {
                    result = HELP_CREATE;
                }
                break;

            case "remove":
                if (arguments.length >= 3) {
                    switch (arguments[1]) {
                        case "database":
                            if (arguments.length == 3) {
                                if (connection.removeDatabase(arguments[2]))
                                    result = parse("show");
                                else
                                    result = DB_NOT_FOUND + arguments[2];
                            }
                            else {
                                result = HELP_REMOVE;
                            }
                            break;

                        case "table":
                            if (arguments.length == 3) {
                                if (loadedDatabase != null && loadedDatabase.removeTable(arguments[2]))
                                    result = parse("show" + " " + loadedDatabase.getName());
                                else
                                    result = TABLE_NOT_FOUND + arguments[2];
                            }
                            else if (arguments.length == 5) {
                                if (loadedDatabase != null && loadedDatabase.containsTable(arguments[2])) {
                                    if (loadedDatabase.getTable(arguments[2]).removeIndex(loadedDatabase.getTable(arguments[2]).getColumn(arguments[4])))
                                        result = parse("show" + " " + loadedDatabase.getName() + " " + loadedDatabase.getTable(arguments[2]));
                                    else
                                        result = INDEX_NOT_FOUND + arguments[4];
                                }
                                else {
                                    result = TABLE_NOT_FOUND + arguments[2];
                                }
                            }
                            else {
                                result = HELP_REMOVE;
                            }
                            break;

                        default:
                            result = HELP_REMOVE;
                            break;
                    }
                }
                else {
                    result = HELP_REMOVE;
                }
                break;

            case "get":
                break;

            case "add":
                break;

            case "update":
                break;

            case "delete":
                break;

            case "help":
                if (arguments.length == 2) {
                    switch (arguments[1]) {
                        case "show":
                            result = HELP_SHOW;
                            break;

                        case "load":
                            result = HELP_LOAD;
                            break;

                        case "create":
                            result = HELP_CREATE;
                            break;

                        case "remove":
                            result = HELP_REMOVE;
                            break;

                        case "get":
                            result = HELP_GET;
                            break;

                        case "add":
                            result = HELP_ADD;
                            break;

                        case "update":
                            result = HELP_UPDATE;
                            break;

                        case "delete":
                            result = HELP_DELETE;
                            break;

                        default:
                            result = HELP_GENERAL;
                            break;
                    }
                }
                else {
                    result = HELP_GENERAL;
                }
                break;

            case "exit":
                if (arguments.length == 1) {
                    connection.close();
                    connection = null;
                }
                else {
                    result = ERROR_ARG_NOT_RECOGNIZED + arguments[1];
                }
                break;

            default:
                result = ERROR_CMD_NOT_FOUND + arguments[0];
                break;
        }
        return result;
    }

    public boolean connectionIsClosed() {
        return connection == null;
    }

    public String getHelpGeneral() {
        return HELP_GENERAL;
    }

    public String getCommand() {
        return command;
    }

    public String getLoadedDatabaseName() {
        return (loadedDatabase == null) ? "" : loadedDatabase.getName();
    }
}

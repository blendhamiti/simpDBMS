import java.util.*;

public class QueryParser {
    private Connection connection;
    private String result;
    private String command;
    private static final String[] commands = {"help", "exit", "show", "load", "create", "remove", "get", "add", "update", "delete"};
    private Database loadedDatabase;

    private static final String DB_NOT_FOUND = "Database was not found: ";
    private static final String TABLE_NOT_FOUND = "Table was not found: ";
    private static final String INDEX_NOT_FOUND = "Index was not found: ";
    private static final String COLUMN_NOT_FOUND = "Column was not found: ";
    private static final String DB_EXISTS = "Database exists: ";
    private static final String INDEX_EXISTS = "Index exists: ";
    private static final String NO_DB_LOADED_OR_TABLE_EXISTS = "There is no loaded database, or table exists: ";
    private static final String NO_DB_LOADED = "There is not a loaded database, see 'help load'";

    private static final String HELP_GENERAL =
            "Available commands are: " + Arrays.asList(commands).toString() + "\n" +
            "Use \"help [COMMAND]\" for the detailed description of the command.";
    private static final String HELP_SHOW =
            """
                    Command: show [DATABASE] [TABLE]
                    \tDescription:
                    \t\tdisplays all databases
                    \tArguments:
                    \t\tDATABASE   => use 'database name' to display that database's tables
                    \t\tTABLE      => use 'table name' to display that table's columns, indexes, primary key and row count
                    """;
    private static final String HELP_LOAD =
            """
                    Command: load [DATABASE]
                    \tDescription:
                    \t\tloads the specified database to allow the user to perform queries on it
                    \t\tif [DATABASE] is not specified, it unloads the currently loaded database
                    \tArguments:
                    \t\tDATABASE   => specify the database name
                    """;
    private static final String HELP_CREATE =
            """
                    [1] Command: create database DATABASE
                    \tDescription:
                    \t\tcreates database with specified name
                    \tArguments:
                    \t\tDATABASE   => specify the database name
                    [2] Command: create table TABLE columns COLUMNS [PK]
                    \tDescription:
                    \t\tcreates table with specified columns (see below for COLUMNS format), and optionally the primary key column\s
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMNS    => specify the columns as "(column_name,column_type;column_name,column_type;...)" with type: {'STRING', 'INTEGER'}
                    \t\tPK         => specify the primary key column name
                    [3] Command: create table TABLE index COLUMN
                    \tDescription:
                    \t\tcreates index of specified column
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    """;
    private static final String HELP_REMOVE =
            """
                    [1] Command: remove database DATABASE
                    \tDescription:
                    \t\tcreates database with specified name
                    \tArguments:
                    \t\tDATABASE   => specify the database name
                    [2] Command: remove table TABLE
                    \tDescription:
                    \t\tremoves table with specified name
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    [3] Command: remove table TABLE index COLUMN
                    \tDescription:
                    \t\tremoves index of specified column
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    """;
    private static final String HELP_GET =
            """
                    [1] Command: get table TABLE
                    \tDescription:
                    \t\treturns all rows of the specified table
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    [2] Command: get table TABLE column COLUMN
                    \tDescription:
                    \t\treturns all records that belong to the certain column of the table
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    [3] Command: get table TABLE column COLUMN SYMBOL RECORD
                    \tDescription:
                    \t\treturns all rows of the table that match the criteria (see 'arguments' for instructions on the symbol specification)
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    \t\tSYMBOL     => specify the comparison symbol as one of the following: {'==','!=','>','>=','<','<='}
                    \t\tRECORD     => specify the record for comparison (use quotation marks for case sensitive words)
                    """;
    private static final String HELP_ADD =
            """
                    Command: add table TABLE records RECORDS
                    \tDescription:
                    \t\tadds a row containing the specified records to the table
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tRECORDS    => specify the records as "(record1,record2,...)"
                    \t\t              number of records specified has to match the number of columns in the table
                    \t\t              use quotation marks for case sensitive words
                    """;
    private static final String HELP_UPDATE =
            """
                    Command: update table TABLE column COLUMN SYMBOL RECORD with RECORDS
                    \tDescription:
                    \t\tupdates the row of the table that match the criteria (see 'arguments' for instructions on the symbol specification)
                    \t\tif more than one row matches the criteria, no changes are performed
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    \t\tSYMBOL     => specify the comparison symbol as one of the following: {'==','!=','>','>=','<','<='}
                    \t\tRECORD     => specify the record for comparison (use quotation marks for case sensitive words)
                    \t\tRECORDS    => specify the records as "(record1,record2,...)"
                    \t\t              number of records specified has to match the number of columns in the table
                    \t\t              use quotation marks for case sensitive words
                    """;
    private static final String HELP_DELETE =
            """
                    Command: delete table TABLE column COLUMN SYMBOL RECORD
                    \tDescription:
                    \t\tdeletes all rows of the table that match the criteria (see 'arguments' for instructions on the symbol specification)
                    \tArguments:
                    \t\tTABLE      => specify the table name
                    \t\tCOLUMN     => specify the column name
                    \t\tSYMBOL     => specify the comparison symbol as one of the following: {'==','!=','>','>=','<','<='}
                    \t\tRECORD     => specify the record for comparison (use quotation marks for case sensitive words)
                    """;
    private static final String HELP_EXIT =
            """
                    Command: EXIT
                    \tDescription:
                    \t\tcloses connection and exits the program
                    """;

    public QueryParser(Connection connection) {
        this.connection = connection;
        this.result = "";
        this.command = "";
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public String parse(String line) {
        String[] argumentsCaseInsensitive = line.split(" ");
        String[] arguments = new String[argumentsCaseInsensitive.length];
        for (int i = 0; i < arguments.length; i++) {
            if (argumentsCaseInsensitive[i].startsWith("\"") || argumentsCaseInsensitive[i].startsWith("("))
                arguments[i] = argumentsCaseInsensitive[i];
            else
                arguments[i] = argumentsCaseInsensitive[i].trim().toLowerCase();
        }

        command = "Command is: " + Arrays.toString(arguments);

        switch (arguments[0]) {
            case "show":
                switch (arguments.length) {
                    case 1:
                        Collection<Database> databases = connection.getDatabases();
                        result = "";
                        for (Database database : databases)
                            result += database.toString() + "\n";
                        break;

                    case 2:
                        if (connection.containsDatabase(arguments[1])) {
                            Collection<Table> tables = connection.getDatabase(arguments[1]).getTables();
                            result = "";
                            for (Table table : tables)
                                result += table.toString() + "\n";
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
                        result = loadedDatabase.toString() + "\n";
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
                                if (!connection.containsDatabase(arguments[2])) {
                                    connection.createDatabase(arguments[2]);
                                    result = parse("show");
                                }
                                else {
                                    result = DB_EXISTS + arguments[2];
                                }
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
                                                columns.add(new Column(columnDataString[0].toLowerCase(), columnDataString[1].toUpperCase()));
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
                                            result = NO_DB_LOADED_OR_TABLE_EXISTS + arguments[2];
                                        }
                                        break;

                                    case "index":
                                        if (arguments.length == 5) {
                                            if (loadedDatabase != null && loadedDatabase.containsTable(arguments[2])) {
                                                if (loadedDatabase.getTable(arguments[2]).createIndex(loadedDatabase.getTable(arguments[2]).getColumn(arguments[4]))) {
                                                    result = parse("show" + " " + loadedDatabase.getName() + " " + loadedDatabase.getTable(arguments[2]).getName());
                                                }
                                                else {
                                                    if (loadedDatabase.getTable(arguments[2]).containsIndex(loadedDatabase.getTable(arguments[2]).getColumn(arguments[4])))
                                                        result = INDEX_EXISTS + arguments[4];
                                                    else
                                                        result = COLUMN_NOT_FOUND + arguments[4];
                                                }
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
                                        result = parse("show" + " " + loadedDatabase.getName() + " " + loadedDatabase.getTable(arguments[2]).getName());
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
                if (loadedDatabase == null) {
                    result = NO_DB_LOADED;
                    break;
                }
                switch (arguments.length) {
                    case 3:
                        if (loadedDatabase.containsTable(arguments[2])) {
                            List<Row> rows = loadedDatabase.getTable(arguments[2]).getRows();
                            result = "";
                            for (Row row : rows)
                                result += row.toString() + "\n";
                        }
                        else {
                            result = TABLE_NOT_FOUND + arguments[2];
                        }
                        break;

                    case 5:
                        if (loadedDatabase.containsTable(arguments[2])) {
                            Table table = loadedDatabase.getTable(arguments[2]);
                            if (table.containsColumn(arguments[4])) {
                                List<Record> records = table.getRows(table.getColumn(arguments[4]));
                                result = "";
                                for (Record record : records)
                                    result += "[" + record.getValue() + "]\n";
                            }
                            else {
                                result = COLUMN_NOT_FOUND + arguments[4];
                            }
                        }
                        else {
                            result = TABLE_NOT_FOUND + arguments[2];
                        }
                        break;

                    case 7:
                        if (loadedDatabase.containsTable(arguments[2])) {
                            Table table = loadedDatabase.getTable(arguments[2]);
                            if (table.containsColumn(arguments[4])) {
                                Column column = table.getColumn(arguments[4]);
                                if (Filter.getFilter(arguments[5]) != null) {
                                    Record record = new Record(arguments[6].replace("\"", ""), column.getType());
                                    List<Row> rows = table.getRow(column, record, Filter.getFilter(arguments[5]));
                                    result = "";
                                    for (Row row : rows)
                                        result += row.toString() + "\n";
                                }
                                else {
                                    result = HELP_GET;
                                }
                            }
                            else {
                                result = COLUMN_NOT_FOUND + arguments[4];
                            }
                        }
                        else {
                            result = TABLE_NOT_FOUND + arguments[2];
                        }
                        break;

                    default:
                        result = HELP_GET;
                        break;
                }
                break;

            case "add":
                if (loadedDatabase == null) {
                    result = NO_DB_LOADED;
                    break;
                }
                if (arguments.length == 5) {
                    if (loadedDatabase.containsTable(arguments[2])) {
                        Table table = loadedDatabase.getTable(arguments[2]);
                        String[] recordsArray = arguments[4].substring(1, arguments[4].length() - 1).split(",");
                        if (recordsArray.length == table.getColumns().size()) {
                            List<Record> records = new ArrayList<>();
                            int loopCount = 0;
                            for (Iterator<Column> it = table.getColumns().iterator(); it.hasNext() ; loopCount++)
                                records.add(new Record(recordsArray[loopCount].replace("\"", ""), it.next().getType()));
                            if (table.addRow(records))
                                result = new Row(records, table.getColumns(), table.getPrimaryKey()).toString() + "\n";
                            else
                                result = HELP_ADD;
                        }
                        else {
                            result = HELP_ADD;
                        }
                    }
                    else {
                        result = TABLE_NOT_FOUND + arguments[2];
                    }
                }
                else {
                    result = HELP_ADD;
                }
                break;

            case "update":
                if (loadedDatabase == null) {
                    result = NO_DB_LOADED;
                    break;
                }
                if (arguments.length == 9) {
                    if (loadedDatabase.containsTable(arguments[2])) {
                        Table table = loadedDatabase.getTable(arguments[2]);
                        if (table.containsColumn(arguments[4])) {
                            Column column = table.getColumn(arguments[4]);
                            if (Filter.getFilter(arguments[5]) != null) {
                                Record record = new Record(arguments[6].replace("\"", ""), column.getType());
                                List<Row> rows = table.getRow(column, record, Filter.getFilter(arguments[5]));
                                if (rows.isEmpty()) {
                                    result = "No rows match the stated criteria";
                                }
                                else if (rows.size() > 1) {
                                    result = "More than one row matches the stated criteria";
                                }
                                else {
                                    Row row = rows.get(0);
                                    if (!table.removeRow(row)) {
                                        result = HELP_UPDATE;
                                    }
                                    else {
                                        String[] recordsArray = arguments[8].substring(1, arguments[4].length() - 1).split(",");
                                        if (recordsArray.length == table.getColumns().size()) {
                                            List<Record> records = new ArrayList<>();
                                            int loopCount = 0;
                                            for (Iterator<Column> it = table.getColumns().iterator(); it.hasNext() ; loopCount++)
                                                records.add(new Record(recordsArray[loopCount].replace("\"", ""), it.next().getType()));
                                            if (table.addRow(records))
                                                result = new Row(records, table.getColumns(), table.getPrimaryKey()).toString() + "\n";
                                            else
                                                result = HELP_UPDATE;
                                        }
                                    }
                                }
                            }
                            else {
                                result = HELP_UPDATE;
                            }
                        }
                        else {
                            result = COLUMN_NOT_FOUND + arguments[4];
                        }
                    }
                    else {
                        result = TABLE_NOT_FOUND + arguments[2];
                    }
                }
                else {
                    result = HELP_UPDATE;
                }
                break;

            case "delete":
                if (loadedDatabase == null) {
                    result = NO_DB_LOADED;
                    break;
                }
                if (arguments.length == 7) {
                    if (loadedDatabase.containsTable(arguments[2])) {
                        Table table = loadedDatabase.getTable(arguments[2]);
                        if (table.containsColumn(arguments[4])) {
                            Column column = table.getColumn(arguments[4]);
                            if (Filter.getFilter(arguments[5]) != null) {
                                Record record = new Record(arguments[6].replace("\"", ""), column.getType());
                                List<Row> rows = table.getRow(column, record, Filter.getFilter(arguments[5]));
                                int rowCount = 0;
                                for (Row row : rows) {
                                    if (table.removeRow(row))
                                        rowCount++;
                                }
                                result = "Deleted " + rowCount + " row(s).";
                            }
                            else {
                                result = HELP_DELETE;
                            }
                        }
                        else {
                            result = COLUMN_NOT_FOUND + arguments[4];
                        }
                    }
                    else {
                        result = TABLE_NOT_FOUND + arguments[2];
                    }
                }
                else {
                    result = HELP_DELETE;
                }
                break;

            case "help":
                if (arguments.length == 2) {
                    switch (arguments[1]) {
                        case "show" -> result = HELP_SHOW;
                        case "load" -> result = HELP_LOAD;
                        case "create" -> result = HELP_CREATE;
                        case "remove" -> result = HELP_REMOVE;
                        case "get" -> result = HELP_GET;
                        case "add" -> result = HELP_ADD;
                        case "update" -> result = HELP_UPDATE;
                        case "delete" -> result = HELP_DELETE;
                        default -> result = HELP_GENERAL;
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
                    result = "Connection closed.";
                }
                else {
                    result = HELP_EXIT;
                }
                break;

            default:
                result = HELP_GENERAL;
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

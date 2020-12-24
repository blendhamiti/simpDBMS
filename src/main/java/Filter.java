public enum Filter {
    EQUAL_TO,
    LARGER_THAN,
    LARGER_THAN_OR_EQUAL_TO,
    NOT_EQUAL_TO,
    SMALLER_THAN,
    SMALLER_THAN_OR_EQUAL_TO;

    public static String getSymbol(Filter filter) {
        return switch (filter) {
            case EQUAL_TO -> "==";
            case LARGER_THAN -> ">";
            case LARGER_THAN_OR_EQUAL_TO -> ">=";
            case NOT_EQUAL_TO -> "!=";
            case SMALLER_THAN -> "<";
            case SMALLER_THAN_OR_EQUAL_TO -> "<=";
        };
    }

    public static Filter getFilter(String symbol) {
        for (Filter filter : Filter.values())
            if (symbol.equals(getSymbol(filter)))
                return filter;
        return null;
    }
}

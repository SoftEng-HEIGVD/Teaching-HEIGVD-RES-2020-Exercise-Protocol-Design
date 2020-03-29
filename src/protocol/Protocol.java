package protocol;

public class Protocol {
    public static final String[] OPERATORS = {"+", "-", "*", "/"};
    public static final char DECIMAL_SEPARATOR = '.';
    public static final String CLIENT_OPENING = "Hello\n";
    public static final String SERVER_OPENING = "Welcome to my super calculator! Valid operators are '+', '-', '*', '/'.\n";
    public static final String SERVER_ERROR = "Invalid operation\n";
    public static final String CLIENT_CLOSE_REQUEST = "Close\n";
    public static final int PORT_NO = 2020;
}

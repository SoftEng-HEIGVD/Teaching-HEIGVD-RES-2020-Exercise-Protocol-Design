package egremyb.common;

/**
 *
 * @author Bruno Egremy
 */
public class Protocol {

    public static final int DEFAULT_PORT = 2020;

    // Protocol messages
    public static final String CMD_HELLO = "hello";
    public static final String CMD_WELCOME = "welcome";
    public static final String CMD_WRONG = "wrong";
    public static final String CMD_BYE = "bye";


    // Allowed operators
    public static final char ADD_OPERATOR = '+';
    public static final char SUB_OPERATOR = '-';
    public static final char MUL_OPERATOR = '*';
    public static final char DIV_OPERATOR = '/';
    public static final char POW_OPERATOR = '^';
}
package egremyb.common;

/**
 *
 * @author Bruno Egremy
 */
public class Protocol {

    public static final int DEFAULT_PORT = 2020;

    // Protocol messages
    public static final String CMD_HELLO   = "hello";
    public static final String CMD_WELCOME = "welcome";
    public static final String CMD_WRONG   = "wrong";
    public static final String CMD_BYE     = "bye";


    // Allowed operators
    public static final String ADD_OPERATOR = "+";
    public static final String SUB_OPERATOR = "-";
    public static final String MUL_OPERATOR = "*";
    public static final String DIV_OPERATOR = "/";
    public static final String POW_OPERATOR = "^";

    // Separator
    public static final String SEPARATOR = " ";
}
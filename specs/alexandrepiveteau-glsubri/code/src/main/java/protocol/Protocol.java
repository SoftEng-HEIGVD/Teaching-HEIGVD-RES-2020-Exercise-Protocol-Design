package protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Declarations and constants that are relevant to both ends of the protocol.
 *
 * @author Alexandre Piveteau
 * @author Guy-Laurent Subri
 */
public class Protocol {
  /** The port that is defined in the protocol spec. */
  public static final int HOST_PORT = 8080;
  /** The {@link Charset} encoding for communicating text content over the protocol. */
  public static Charset CHARSET = StandardCharsets.UTF_8;

  private Protocol() {}

  /** Declarations of constant messages, and simple message builders. */
  public static class Messages {

    public static final String MSG_START = "START";
    public static final String MSG_GO_AHEAD = "GO_AHEAD";
    public static final String MSG_OK_N = "OK N";
    public static final String MSG_OK_O = "OK O";
    public static final String MSG_PERFORM = "PERFORM";
    public static final String MSG_RES = "RES";

    private Messages() {}

    /**
     * Appends a new line to a message, as defined in the protocol. The newline separator is defined
     * to be UNIX-compliant in the spec.
     *
     * @param message The message to append to.
     * @return A new {@link String}, with the new line terminator.
     */
    public static String withNewLine(String message) {
      return message + "\n";
    }
  }
}

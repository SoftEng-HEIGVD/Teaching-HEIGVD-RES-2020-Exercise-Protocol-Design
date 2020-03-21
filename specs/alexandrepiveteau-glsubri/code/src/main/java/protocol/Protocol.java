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
  private Protocol() {}

  /**
   * The port that is defined in the protocol spec.
   */
  public static final int HOST_PORT = 8080;

  /**
   * The {@link Charset} encoding for communicating text content over the protocol.
   */
  public static Charset CHARSET = StandardCharsets.UTF_8;
}

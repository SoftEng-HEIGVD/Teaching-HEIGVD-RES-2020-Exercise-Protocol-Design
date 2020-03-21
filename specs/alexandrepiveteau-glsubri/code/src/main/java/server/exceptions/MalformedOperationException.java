package server.exceptions;

import server.CalculatorOperation;

/**
 * An exception that will be thrown when some arguments provided to a {@link
 * server.CalculatorOperation} are not valid.
 *
 * @author Alexandre Piveteau
 * @author Guy-Laurent Subri
 */
public class MalformedOperationException extends MalformedMessageException {

  public MalformedOperationException(CalculatorOperation operation, int a, int b) {
    super(String.format("%d %s %d", a, operation, b));
  }
}

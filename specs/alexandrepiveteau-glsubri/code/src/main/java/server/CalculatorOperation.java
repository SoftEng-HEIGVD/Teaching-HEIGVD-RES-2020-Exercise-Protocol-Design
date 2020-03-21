package server;

import java.util.function.BiFunction;
import server.exceptions.MalformedOperationException;
import server.exceptions.MalformedMessageException;

public enum CalculatorOperation {
  ADD(Integer::sum),
  SUB((a, b) -> a - b),
  MUL((a, b) -> a * b),
  DIV((a, b) -> a / b);

  private BiFunction<Integer, Integer, Integer> behavior;

  /* private */ CalculatorOperation(BiFunction<Integer, Integer, Integer> function) {
    this.behavior = function;
  }

  /**
   * Returns the {@link CalculatorOperation} corresponding to a certain String message. Messages
   * should be encoded to UTF-8. The messages are considered from the server perspective.
   *
   * @param message The message that was received.
   * @return The corresponding {@link CalculatorOperation} for the message.
   * @throws MalformedMessageException If the message is not valid.
   */
  public static CalculatorOperation fromMessage(String message) throws MalformedMessageException {
    switch (message) {
      case "O ADD":
        return CalculatorOperation.ADD;
      case "O SUB":
        return CalculatorOperation.SUB;
      case "O MUL":
        return CalculatorOperation.MUL;
      case "O DIV":
        return CalculatorOperation.DIV;
      default:
        throw new MalformedMessageException(message);
    }
  }

  public int perform(int a, int b) throws MalformedOperationException {
    try {
      return this.behavior.apply(a, b);
    } catch (Throwable ignored) {
      throw new MalformedOperationException(this, a, b);
    }
  }
}

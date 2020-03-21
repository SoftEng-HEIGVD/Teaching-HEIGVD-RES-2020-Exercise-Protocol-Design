package server;

import java.util.function.BiFunction;

public enum Operation {
  ADD(Integer::sum),
  SUB((a, b) -> a - b),
  MUL((a, b) -> a * b),
  DIV((a, b) -> a / b);

  private BiFunction<Integer, Integer, Integer> behavior;

  /* private */ Operation(BiFunction<Integer, Integer, Integer> function) {
    this.behavior = function;
  }

  /**
   * Returns the {@link Operation} corresponding to a certain String message. Messages should be
   * encoded to UTF-8. The messages are considered from the server perspective.
   *
   * @param message The message that was received.
   * @return The corresponding {@link Operation} for the message.
   * @throws MalformedMessageException If the message is not valid.
   */
  public static Operation fromMessage(String message) throws MalformedMessageException {
    switch (message) {
      case "O ADD":
        return Operation.ADD;
      case "O SUB":
        return Operation.SUB;
      case "O MUL":
        return Operation.MUL;
      case "O DIV":
        return Operation.DIV;
      default:
        throw new MalformedMessageException(message);
    }
  }

  public int perform(int a, int b) throws IllegalArgumentException {
    try {
      return this.behavior.apply(a, b);
    } catch (Throwable any) {
      throw new IllegalArgumentException(any);
    }
  }
}

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

  public int perform(int a, int b) throws IllegalArgumentException {
    try {
      return this.behavior.apply(a, b);
    } catch (Throwable any) {
      throw new IllegalArgumentException(any);
    }
  }
}
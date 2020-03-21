package server;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

  private static final Logger LOG = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) throws IOException {
    CalculatorServer server = new CalculatorServer();
    server.start();
  }
}

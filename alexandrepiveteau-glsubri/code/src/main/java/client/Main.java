package client;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
  public static void main(String[] args) throws IOException {
    Client c = new Client(InetAddress.getLocalHost());
    while (true) {
      c.start();
    }
  }
}
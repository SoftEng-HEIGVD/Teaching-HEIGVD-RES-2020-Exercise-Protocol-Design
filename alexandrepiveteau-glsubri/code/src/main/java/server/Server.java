package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

  private static final Logger LOG = Logger.getLogger(Server.class.getName());

  private static final int HOST_PORT = 8080;
  private static final int HOST_BACKLOG = 50;

  private static Charset CHARSET = StandardCharsets.UTF_8;

  public void start() throws IOException {
    ServerSocket serverSocket;
    BufferedReader reader;
    BufferedWriter writer;
    int a = 0;
    int b = 0;
    Operation op;

    serverSocket = new ServerSocket(HOST_PORT, HOST_BACKLOG, InetAddress.getLocalHost());

    while (true) {

      try (Socket socket = serverSocket.accept()) {

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET));

        String greeting = reader.readLine();

        // Message 1 <- Receive a greeting message.
        if (!greeting.equals("START")) {
          wrongMessage(socket, writer, greeting);
          continue;
        }

        // Message 2 -> Tell that we are ready.
        writer.write("GO AHEAD\n");
        writer.flush();

        // Message 3 <- Read the number (if it exists).
        try {
          String line = reader.readLine();
          a = readAndParseInteger(line);
        } catch (MalformedMessageException mme) {
          wrongMessage(socket, writer, mme.getMalformedMessage());
          continue;
        }

        // Message 4 -> Notify we read the number.
        writer.write("OK N\n");
        writer.flush();

        // Message 5 <- Read the operator.
        try {
          String line = reader.readLine();
          op = readAndParseOperation(line);
        } catch (MalformedMessageException mme) {
          wrongMessage(socket, writer, mme.getMalformedMessage());
          continue;
        }

        // Message 6 -> Notify we read the operator.
        writer.write(String.format("OK O %s\n", op));
        writer.flush();

        // Message 7 <- Read the number (if it exists).
        try {
          String line = reader.readLine();
          b = readAndParseInteger(line);
        } catch (MalformedMessageException mme) {
          wrongMessage(socket, writer, mme.getMalformedMessage());
          continue;
        }

        // Message 8 -> Notify we read the number.
        writer.write("OK N\n");
        writer.flush();

        // Message 9 <- Are we asked to perform the operation ?
        String perform = reader.readLine();
        if (!perform.equals("PERFORM")) {
          wrongMessage(socket, writer, perform);
          continue;
        }

        // Message 10 -> compute and send the result.
        try {
          writer.write(String.format("RES %d\n", op.perform(a, b)));
          writer.flush();
        } catch (IllegalArgumentException iae) {
          wrongMessage(socket, writer, String.format("%d %s %d", a, op, b));
        }
      }
    }
  }

  private void wrongMessage(
      Socket socket,
      BufferedWriter socketWriter,
      String message
  ) throws IOException {
    LOG.log(Level.WARNING, "Received incorrect message : \"{0}\".", message);
    socketWriter.write("ERR\n");
    socketWriter.flush();
    socket.close();
  }

  private int readAndParseInteger(String message) throws MalformedMessageException {

    // Preconditions.
    if (message.length() <= 2) {
      throw new MalformedMessageException(message);
    }
    if (message.charAt(0) != 'N' || message.charAt(1) != ' ') {
      throw new MalformedMessageException(message);
    }

    // Drop the first two chars, and try to parse an integer.
    String dropped = message.substring(2);
    int value;
    try {
      value = Integer.parseInt(dropped, 10);
    } catch (NumberFormatException nfe) {
      throw new MalformedMessageException(message);
    }

    return value;
  }

  private Operation readAndParseOperation(String message) throws MalformedMessageException {
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
}

package server;

import static protocol.Protocol.Messages.MSG_OK_O;
import static protocol.Protocol.Messages.MSG_PERFORM;
import static protocol.Protocol.Messages.withNewLine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.Protocol;
import protocol.Protocol.Messages;

/**
 * An implementation of a {@link Runnable} that will implement the notion of a session of calculator
 * requests and responses on the server side.
 *
 * <p>Each {@link Runnable} has a contained state, meaning that it is possible for a server to
 * handle concurrent requests.
 *
 * @author Alexandre Piveteau
 * @author Guy-Laurent Subri
 */
public class CalculatorRequest implements Runnable {

  private static Logger LOG = Logger.getLogger(CalculatorRequest.class.getName());

  // I/O streams used in the app.
  private BufferedReader reader;
  private BufferedWriter writer;

  // Socket for one connection.
  private Socket socket;

  public CalculatorRequest(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      int a, b;
      CalculatorOperation op;

      Reader streamReader = new InputStreamReader(socket.getInputStream(), Protocol.CHARSET);
      Writer streamWriter = new OutputStreamWriter(socket.getOutputStream(), Protocol.CHARSET);

      reader = new BufferedReader(streamReader);
      writer = new BufferedWriter(streamWriter);

      // Message 1 <- Receive a greeting message.
      requireMessage(Messages.MSG_START);

      // Message 2 -> Tell that we are ready.
      writeMessageNow(withNewLine(Messages.MSG_GO_AHEAD));

      // Message 3 <- Read the number (if it exists).
      try {
        String line = reader.readLine();
        a = readAndParseInteger(line);
      } catch (MalformedMessageException mme) {
        wrongMessage(socket, writer, mme.getMalformedMessage());
        return;
      }

      // Message 4 -> Notify we read the number.
      writeMessageNow(withNewLine(Messages.MSG_OK_N));

      // Message 5 <- Read the operator.
      try {
        String line = reader.readLine();
        op = CalculatorOperation.fromMessage(line);
      } catch (MalformedMessageException mme) {
        wrongMessage(socket, writer, mme.getMalformedMessage());
        return;
      }

      // Message 6 -> Notify we read the operator.
      writeMessageNow(withNewLine(MSG_OK_O + " " + op));

      // Message 7 <- Read the number (if it exists).
      try {
        String line = reader.readLine();
        b = readAndParseInteger(line);
      } catch (MalformedMessageException mme) {
        wrongMessage(socket, writer, mme.getMalformedMessage());
        return;
      }

      // Message 8 -> Notify we read the number.
      writeMessageNow(withNewLine(Messages.MSG_OK_N));

      // Message 9 <- Are we asked to perform the operation ?
      requireMessage(MSG_PERFORM);

      // Message 10 -> compute and send the result.
      try {
        writer.write(withNewLine(Messages.MSG_RES + " " + op.perform(a, b)));
        writer.flush();
      } catch (IllegalArgumentException iae) {
        wrongMessage(socket, writer, String.format("%d %s %d", a, op, b));
      }

    } catch (MalformedMessageException mex) {
      LOG.log(Level.WARNING, "Received incorrect message : \"{0}\".", mex.getMalformedMessage());
    } catch (IOException ioex) {
      LOG.log(Level.SEVERE, ioex.getMessage());
    } finally {

      // Close the socket after the connection is over.
      if (!socket.isClosed()) {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Requires that a certain message is now received, or throws an exception.
   *
   * @throws IOException Thrown if the expected message is not received now.
   */
  private void requireMessage(String expected) throws IOException {
    String message = reader.readLine();
    if (!expected.equals(message)) {
      wrongMessage(socket, writer, message);
      throw new MalformedMessageException(message);
    }
  }

  /**
   * Writes a certain protocol message now, and ensures it is sent fully now.
   *
   * @param message The message to send.
   * @throws IOException Thrown if the message could not be sent.
   */
  private void writeMessageNow(String message) throws IOException {
    writer.write(message);
    writer.flush();
  }

  private void wrongMessage(Socket socket, BufferedWriter socketWriter, String message)
      throws IOException {
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
}

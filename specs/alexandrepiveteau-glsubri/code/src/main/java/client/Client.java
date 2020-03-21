package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import protocol.Protocol;

import static protocol.Protocol.Messages.*;

public class Client {
  private static final String ERROR_FORMAT =
      "Error in format. Must be 'a <op> b'\n"
          + "a and b must be integers\n"
          + "<op> must be +, -, * or /\n";

  private static final String ERROR_COMM = "Error while talking with server.\n";
  private static final String INFO_USAGE = "Enter a simple calculation to be made:";
  private static final String INFO_RES = "Result is: ";
  private final InetAddress host;
  private BufferedWriter writer;
  private BufferedReader reader;

  public Client(InetAddress host) {
    this.host = host;
  }

  /**
   * Helper method to send a request to the server. Makes sure that there is a '\n' at the end
   * of the request string. Also forces immediate write with flush
   * @param req The string representing the request to the server
   * @throws IOException If an exception is thrown when using the socket
   */
  private void sendReq(String req) throws IOException {
    writer.write(withNewLine(req));
    writer.flush();
  }

  /**
   * Helper method to check if the server gave us the expected answer.
   * @param ans The expected answer from the server
   * @throws IOException If the server didn't answer us what we expected or if there is an error
   * using the reader
   */
  private void checkAnswer(String ans) throws IOException {
    if (!reader.readLine().equals(ans)) {
      sendReq(MSG_ERR);
      reset();
    }
  }

  /**
   * Helper method to simply throw an error when communicating with the server
   * @throws IOException Automatically thrown when this method is called
   */
  private void reset() throws IOException {
    throw new IOException(ERROR_COMM);
  }

  /**
   * Starts the sequence with the server. Returns the result of the execution.
   * @param f The First integer
   * @param op The operation
   * @param s The Second integer
   * @return The result of f `op` s
   * @throws IOException If there is a problem communicating with the server
   */
  private int askServer(int f, Op op, int s) throws IOException {
    sendReq(MSG_START);
    checkAnswer(MSG_GO_AHEAD);

    sendReq(MSG_N + " " + f);
    checkAnswer(MSG_OK_N);

    sendReq(MSG_O + " " + op);
    checkAnswer(MSG_OK_O + " " + op);

    sendReq(MSG_N + " " + s);
    checkAnswer(MSG_OK_N);

    sendReq(MSG_PERFORM);
    String line = reader.readLine();

    if (line.length() < 3 || ! line.substring(0, 3).equals(MSG_RES))
      reset();

    return Integer.parseInt(line.substring(4));
  }

  /**
   * Main entry point for this class. Starts by asking the user for input and sends it to the server
   */
  public void start() {
    int f = 0;
    int s = 0;
    Op op = Op.ADD;

    boolean incorrectInput = true;
    while (incorrectInput) {
      System.out.println(INFO_USAGE);
      Scanner in = new Scanner(System.in);
      try {
        f = in.nextInt();
        op = Op.fromToken(in.next());
        s = in.nextInt();
        incorrectInput = false;
      } catch (Throwable e) {
        System.out.println(ERROR_FORMAT);
      }
    }

    try (Socket server = new Socket(host, Protocol.HOST_PORT)) {
      OutputStream os = server.getOutputStream();
      InputStream is = server.getInputStream();

      writer = new BufferedWriter(new OutputStreamWriter(os, Protocol.CHARSET));
      reader = new BufferedReader(new InputStreamReader(is, Protocol.CHARSET));

      int res = askServer(f, op, s);
      System.out.println(INFO_RES + res);
    } catch (Throwable e) {
      System.out.println(ERROR_COMM);
    }
  }
}

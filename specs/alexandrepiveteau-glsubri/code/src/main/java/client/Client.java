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

  private void sendReq(String req) throws IOException {
    writer.write(withNewLine(req));
    writer.flush();
  }

  private void checkAnswer(String ans) throws IOException {
    if (!reader.readLine().equals(ans)) {
      sendReq(MSG_ERR);
      reset();
    }
  }

  private void reset() throws IOException {
    throw new IOException(ERROR_COMM);
  }

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

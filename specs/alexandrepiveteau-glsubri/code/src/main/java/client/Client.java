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
import java.util.InputMismatchException;
import java.util.Scanner;
import protocol.Protocol;

public class Client {
  private static final String ERROR_FORMAT =
      "Error in format. Must be 'a <op> b'\n"
          + "a and b must be integers\n"
          + "<op> must be +, -, * or /\n";
  private static final String ERROR_COMM = "Error while talking with server.\n";
  private final InetAddress host;
  private BufferedWriter writer;
  private BufferedReader reader;
  public Client(InetAddress host) {
    this.host = host;
  }

  private void sendReq(String req) throws IOException {
    writer.write(req + "\n");
    writer.flush();
  }

  private void checkAnswer(String ans) throws IOException {
    if (!reader.readLine().equals(ans)) {
      sendReq("ERR");
      reset();
    }
  }

  private void reset() throws IOException {
    throw new IOException(ERROR_COMM);
  }

  private int askServer(int f, Op op, int s) throws IOException {
    sendReq("START");
    checkAnswer("GO AHEAD");

    sendReq("N " + f);
    checkAnswer("OK N");

    sendReq("O " + op);
    checkAnswer("OK O " + op);

    sendReq("N " + s);
    checkAnswer("OK N");

    sendReq("PERFORM");
    String line = reader.readLine();

    if (line.length() < 3 || line.substring(0, 3).equals("ERR")) reset();

    return Integer.parseInt(line.substring(4));
  }

  public void start() throws IOException {
    Socket server = new Socket(host, Protocol.HOST_PORT);
    int f = 0;
    int s = 0;
    Op op = Op.ADD;

    boolean incorrectInput = true;
    while (incorrectInput) {
      System.out.println("Enter a simple calculation to be made:");
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

    try {
      OutputStream os = server.getOutputStream();
      InputStream is = server.getInputStream();

      writer = new BufferedWriter(new OutputStreamWriter(os, Protocol.CHARSET));
      reader = new BufferedReader(new InputStreamReader(is, Protocol.CHARSET));

      int res = askServer(f, op, s);
      System.out.println("Result is: " + res);
    } catch (Throwable e) {
      System.out.println(ERROR_COMM);
    } finally {
      server.close();
    }
  }

  private enum Op {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private String op;

    Op(String op) {
      this.op = op;
    }

    public static Op fromToken(String token) throws InputMismatchException {
      for (Op operator : values()) {
        if (operator.op.equals(token))
          return operator;
      }
      throw new InputMismatchException();
    }
  }
}

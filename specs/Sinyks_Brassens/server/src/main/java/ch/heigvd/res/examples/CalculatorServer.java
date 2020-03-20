package ch.heigvd.res.examples;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * inpired by O.Liechti
 * here we implement a very simple Protocol
 * Here is the server for
 * @author Sacha Perdrizat & Pablo Mercado
 */
public class CalculatorServer {

  static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

  private final int LISTEN_PORT = 2020;

  /**
   *
   * @param operator
   * @param leftOperand
   * @param rightOperand
   * @return the result of the coper
   */
  private int executor(String operator, int leftOperand, int rightOperand) throws InterruptedException {
    int result = 0;

    switch (operator){
      case "+":
        result = leftOperand + rightOperand;
        break;
      case "-":
        result = leftOperand - rightOperand;
        break;
      case "*":
        result = leftOperand * rightOperand;
        break;
      default:
        throw new InterruptedException("not an valid operand");
    }

    return result;
  }

  /**
   * This method does the entire processing.
   */
  public void start() {
    LOG.info("Starting server...");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    BufferedReader reader = null;
    PrintWriter writer = null;

    try {


      String operand;
      int leftOperand;
      int rightOperand;
      int result;

      LOG.log(Level.INFO,"starting the state communication");

      while (true) {
        writer.println("Hi : give me one of those three: + - *");
        writer.flush();

        operand = reader.readLine();

        LOG.log(Level.INFO, "Here we need the first operator");
        writer.println("OK : give me the left operand");
        writer.flush();

        leftOperand = Integer.parseInt(reader.readLine());

        LOG.log(Level.INFO, "Here we need the second operator");
        writer.println("OK : give me the right operand");
        writer.flush();

        rightOperand = Integer.parseInt(reader.readLine());

        result = this.executor(operand,leftOperand,rightOperand);

        writer.println("Your result is : " + result);
        writer.flush();

      }


    } catch (IOException | InterruptedException ex) {
      LOG.log(Level.SEVERE, ex.getMessage());
    } finally {
      LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");

      try {
        reader.close();
      } catch (IOException ex) {
        Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
      }

      writer.close();

      try {
        clientSocket.close();
      } catch (IOException ex) {
        Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
      }

      try {
        serverSocket.close();
      } catch (IOException ex) {
        Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  }

  /**
   * A utility method to print server socket information
   *
   * @param serverSocket the socket that we want to log
   */
  private void logServerSocketAddress(ServerSocket serverSocket) {
    LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
    LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
    LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
  }

  /**
   * A utility method to print socket information
   *
   * @param clientSocket the socket that we want to log
   */
  private void logSocketAddress(Socket clientSocket) {
    LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
    LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
    LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
    LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

    CalculatorServer server = new CalculatorServer();
    server.start();
  }

}

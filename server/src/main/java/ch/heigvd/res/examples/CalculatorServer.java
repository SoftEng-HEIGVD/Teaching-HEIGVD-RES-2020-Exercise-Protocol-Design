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
 *
 * @author Sacha Perdrizat & Pablo Mercado
 */
public class CalculatorServer {

  static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

  private final int LISTEN_PORT = 2020;

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
      LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
      serverSocket = new ServerSocket(LISTEN_PORT);
      logServerSocketAddress(serverSocket);

      LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
      clientSocket = serverSocket.accept();

      LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
      logSocketAddress(clientSocket);

      LOG.log(Level.INFO, "Getting a Reader and a Writer connected to the client socket...");
      reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      writer = new PrintWriter(clientSocket.getOutputStream());

      String operand;
      int leftOperator;
      int rightOperator;
      int result;

      LOG.log(Level.INFO,"starting the state communication");

      while (true) {

        operand = reader.readLine();

        LOG.log(Level.INFO, "Here we need the first operator");
        writer.println("OK : give me the left operand");
        writer.flush();

        leftOperator = Integer.parseInt(reader.readLine());

        LOG.log(Level.INFO, "Here we need the second operator");
        writer.println("OK : give me the right operand");
        writer.flush();

        rightOperator = Integer.parseInt(reader.readLine());

        switch (operand){
          case "+":
            result = leftOperator + rightOperator;
          break;
          case "-":
            result = leftOperator - rightOperator;
            break;
          case "*":
            result = leftOperator * rightOperator;
            break;
          default:
            throw new InterruptedException("not an valid operand");
        }

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

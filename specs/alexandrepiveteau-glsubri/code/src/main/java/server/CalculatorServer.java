package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.Protocol;

public class CalculatorServer {

  private static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

  private static final int HOST_BACKLOG = 50;

  public void start() throws IOException {
    ServerSocket serverSocket;

    ExecutorService service = Executors.newCachedThreadPool();

    serverSocket = new ServerSocket(Protocol.HOST_PORT, HOST_BACKLOG, InetAddress.getLocalHost());

    LOG.log(Level.INFO, "Running at " + serverSocket.getLocalSocketAddress());
    LOG.log(Level.INFO, "Port is " + serverSocket.getLocalPort());

    while (true) {
      Socket socket = serverSocket.accept();
      service.submit(new CalculatorRequest(socket));
    }
  }
}

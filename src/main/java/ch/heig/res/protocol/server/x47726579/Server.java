package main.java.ch.heig.res.protocol.server.x47726579;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a multi-threaded server of the custom presence protocol. The
 * server binds a socket on the specified port and waits for incoming connection
 * requests. It keeps track of connected clients in a list. When new clients
 * arrive, leave or send messages, the server notifies all connected clients.
 *
 * @author Olivier Liechti
 * @modified_by Laurent Scherer
 */
public class Server implements Runnable
{

	private static final int    DEFAULT_PORT = 3300;
	private static final String CMD_GREET    = "Greetings";
	private static final String CMD_ADD      = "ADD";
	private static final String CMD_SUB      = "SUB";
	private static final String CMD_MUL      = "MUL";
	private static final String CMD_EXT      = "EXT";

	final static Logger LOG = Logger.getLogger(Server.class.getName());


	boolean      shouldRun;
	ServerSocket serverSocket;
	final List<Worker> connectedWorkers;

	public Server()
	{
		this.shouldRun = true;
		this.connectedWorkers = Collections.synchronizedList(new LinkedList<Worker>());
	}

	private void registerWorker(Worker worker)
	{
		LOG.log(Level.INFO, ">> Waiting for lock before registring worker {0}",
		        worker.userName);
		connectedWorkers.add(worker);
		LOG.log(Level.INFO, "<< Worker {0} registered.", worker.userName);
	}

	private void unregisterWorker(Worker worker)
	{
		LOG.log(Level.INFO, ">> Waiting for lock before unregistring worker {0}",
		        worker.userName);
		connectedWorkers.remove(worker);
		LOG.log(Level.INFO, "<< Worker {0} unregistered.", worker.userName);
	}

	private void notifyConnectedWorkers(String message)
	{
		LOG.info(">> Waiting for lock before notifying workers");
		synchronized (connectedWorkers) {
			LOG.info("Notifying workers");
			for (Worker worker : connectedWorkers) {
				worker.sendNotification(message);
			}
		}
		LOG.info("<< Workers notified");
	}

	private void disconnectConnectedWorkers()
	{
		LOG.info(">> Waiting for lock before disconnecting workers");
		synchronized (connectedWorkers) {
			LOG.info("Disconnecting workers");
			for (Worker worker : connectedWorkers) {
				worker.disconnect();
			}
		}
		LOG.info("<< Workers disconnected");
	}

	@Override
	public void run()
	{
		try {
			LOG.log(Level.INFO, "Starting Presence Server on port {0}", DEFAULT_PORT);
			serverSocket = new ServerSocket(DEFAULT_PORT);
			while (shouldRun) {
				Socket clientSocket = serverSocket.accept();
				Server.this.notifyConnectedWorkers("Someone has arrived...");
				Worker newWorker = new Worker(clientSocket);
				registerWorker(newWorker);
				new Thread(newWorker).start();
			}
			serverSocket.close();
			LOG.info("shouldRun is false... server going down");
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
			System.exit(-1);
		}
	}

	private void shutdown()
	{
		LOG.info("Shutting down server...");
		shouldRun = false;
		try {
			serverSocket.close();
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
		disconnectConnectedWorkers();
	}

	class Worker implements Runnable
	{

		Socket         clientSocket;
		BufferedReader in;
		PrintWriter    out;
		boolean        connected;
		String         userName = "An anonymous user";

		public Worker(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
			try {
				in =
					new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream());
				connected = true;
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}

		@Override
		public void run()
		{
			String commandLine;
			Server.this.notifyConnectedWorkers("Welcome to the Presence Server");
			Server.this.notifyConnectedWorkers("  Tell me who you are with 'HELLO name'");
			Server.this.notifyConnectedWorkers(
				"  Say something to other users with 'SAY message'");
			Server.this.notifyConnectedWorkers("  Ask me who is connected with 'WHO'");
			Server.this.notifyConnectedWorkers("  Leave with 'BYE'");
			Server.this.notifyConnectedWorkers("  Shutdown server with 'KILL'");
			try {
				while (connected && ((commandLine = in.readLine()) != null)) {
					String[] tokens = commandLine.split(" ");
					switch (tokens[0].toUpperCase()) {
						case (CMD_GREET):
							userName = tokens.length >= 2 ? tokens[1] : "An anonymous user";
							Server.this.notifyConnectedWorkers(userName + " is in the room.");
							break;
						case (CMD_MUL):
							String message =
								tokens.length >= 2 ? commandLine.substring(4) : "nothing...";
							Server.this.notifyConnectedWorkers(userName + " says: " + message);
							break;
						case (CMD_ADD):
							StringBuilder sb = new StringBuilder(
								"Currently connected users:\r\n");
							for (Worker w : connectedWorkers) {
								sb.append(" - ");
								sb.append(w.userName);
								sb.append("\n");
							}
							sendNotification(sb.toString());
							break;
						case (CMD_SUB):
							Server.this.notifyConnectedWorkers(
								userName + " is about to leave the room.");
							connected = false;
							break;
						case (CMD_EXT):
							sendNotification("KILL command received. Bringing server down...");
							shutdown();
							break;
						default:
							sendNotification(
								"What? I only understand HELLO, SAY, WHO, BYE and KILL " +
								"commands");
					}
				}
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			} finally {
				unregisterWorker(this);
				Server.this.notifyConnectedWorkers(userName + " has left the room.");
				cleanup();
			}
		}

		private void cleanup()
		{
			LOG.log(Level.INFO, "Cleaning up worker used by {0}", userName);

			LOG.log(Level.INFO, "Closing clientSocket used by {0}", userName);
			try {
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}

			LOG.log(Level.INFO, "Closing in used by {0}", userName);
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}

			LOG.log(Level.INFO, "Closing out used by {0}", userName);
			if (out != null) {
				out.close();
			}

			LOG.log(Level.INFO, "Clean up done for worker used by {0}", userName);
		}

		public void sendNotification(String message)
		{
			out.println(message);
			out.flush();
		}

		private void disconnect()
		{
			LOG.log(Level.INFO, "Disconnecting worker used by {0}", userName);
			connected = false;
			cleanup();
		}

	}

}
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

import static main.java.ch.heig.res.protocol.protocol.x47726579.Protocol.*;


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

	final static Logger LOG = Logger.getLogger(Server.class.getName());

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	}

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
			LOG.log(Level.FINE, "Starting Presence Server on port {0}", DEFAULT_PORT);
			serverSocket = new ServerSocket(DEFAULT_PORT);
			while (shouldRun) {
				Socket clientSocket = serverSocket.accept();
				Worker newWorker    = new Worker(clientSocket);
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
		String         userName;
		int            result = 0;


		public Worker(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream());
				connected = true;
				if (!in.readLine().equals(CMD_GREET)) {disconnect();}
				userName = "user" + connectedWorkers.size();
				sendNotification("\t================= Welcome =================");
				stdMessage();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}

		private void stdMessage()
		{
			sendNotification("I am a Math server, you can query me with the following keywords :");
			sendNotification("- To add two numbers                       : ADD number1 number2 ");
			sendNotification("- Add a number to the previous result      : ADD number");
			sendNotification("- To subtract two numbers                  : SUB number1 number2 ");
			sendNotification("- Subtract a number to the previous result : SUB number");
			sendNotification("- To multiply two numbers                  : MUL number1 number2 ");
			sendNotification("- Multiply a number to the previous result : MUL number");
			sendNotification("- Exit the server with EXT");
			sendNotification("- Kill me with KILL");
		}

		@Override
		public void run()
		{
			String commandLine;

			try {
				while (connected && ((commandLine = in.readLine()) != null)) {
					String[] tokens = commandLine.split(" ");
					switch (tokens[0].toUpperCase()) {
						case (CMD_MUL):
							if (tokens.length == 2) {
								result *= Integer.parseInt(tokens[1]);
							} else if (tokens.length == 3) {
								result = Integer.parseInt(tokens[1]) * Integer.parseInt(tokens[2]);
							} else {
								sendNotification("Not a valid MUL command.");
								break;
							}
							sendNotification(String.valueOf(result));
							break;
						case (CMD_ADD):
							if (tokens.length == 2) {
								result += Integer.parseInt(tokens[1]);
							} else if (tokens.length == 3) {
								result = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[2]);
							} else {
								sendNotification("Not a valid ADD command.");
								break;
							}
							sendNotification(String.valueOf(result));
							break;
						case (CMD_SUB):
							if (tokens.length == 2) {
								result -= Integer.parseInt(tokens[1]);
							} else if (tokens.length == 3) {
								result = Integer.parseInt(tokens[1]) - Integer.parseInt(tokens[2]);
							} else {
								sendNotification("Not a valid ADD command.");
								break;
							}
							sendNotification(String.valueOf(result));
							break;
						case (CMD_EXT):
							Server.this.notifyConnectedWorkers(userName + " is about to leave the room.");
							connected = false;
							break;
						case (CMD_KILL):
							sendNotification("KILL command received. Bringing server down...");
							shutdown();
							break;
						default:
							sendNotification("What? I only understand ADD, SUB, MUL, EXT and KILL commands");
					}

				}
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			} finally {
				unregisterWorker(this);
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
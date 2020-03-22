package main.java.ch.heig.res.protocol.test.x47726579;

import main.java.ch.heig.res.protocol.client.x47726579.Client;
import main.java.ch.heig.res.protocol.server.x47726579.Server;

import java.io.IOException;

public class Application
{
	public static void main(String[] args)
	{
		System.out.println("Start the server with [0], client with 1 :");
		char choice = '0';

		try {
			choice = (char) System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}


		if (choice == '1') {

			System.out.println("Starting client...\nConnecting to server...");
			Client cli = new Client();
			cli.connect("192.168.1.126", 3300);

		} else {

			System.out.println("Starting server...\nWaiting for clients...");

			Server srv     = new Server();
			long   start   = System.nanoTime();
			long   minute  = 60_000_000_000L;
			int    counter = 1;
			srv.run();
			while (srv.isRunning()) {
				if (((System.nanoTime() - start) > minute * counter)) {
					System.out.println("Every sixty seconds a minute passes...");
					counter++;
				}
			}
		}
		System.out.println("Shutting down the " + (choice == '0' ? "server..." : "client..."));
	}


}



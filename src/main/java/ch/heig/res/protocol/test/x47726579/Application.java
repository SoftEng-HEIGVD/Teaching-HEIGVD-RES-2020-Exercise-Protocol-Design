package main.java.ch.heig.res.protocol.test.x47726579;

import main.java.ch.heig.res.protocol.client.x47726579.Client;
import main.java.ch.heig.res.protocol.server.x47726579.Server;

import java.io.IOException;
import java.util.Scanner;

import static main.java.ch.heig.res.protocol.protocol.x47726579.Protocol.DEFAULT_PORT;

public class Application
{
	public static void main(String[] args)
	{

		char   choice     = '0';
		String DEFAULT_IP = "192.168.1.126";


		//==== Which part of the app do we want to use?
		System.out.println("Start the server with [0], client with 1 :");
		try {
			choice = (char) System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//==== We want to use the client.
		if (choice == '1') {
			System.out.println("Starting client...");
			System.out.print("Do you wish to specify an IP? [Y/N]");

			try {
				do {
					choice = (char) System.in.read();
				} while (Character.toUpperCase(choice) != 'N' && Character.toUpperCase(choice) != 'Y');
			} catch (IOException e) {
				e.printStackTrace();
			}

			// this option will be left without any checks,
			// as we would need to go quite in-depth to validate a proper IP, we will
			// it up to the user.
			if (Character.toUpperCase(choice) == 'Y') {
				String  IP;
				Scanner newIP = new Scanner(System.in);
				while ((IP = newIP.nextLine()).isBlank()) {
					System.out.println("Current IP [" + DEFAULT_IP + "], enter new IP :");
					DEFAULT_IP = IP;
				}
			}

			System.out.println("Connecting to server...");
			Client cli = new Client();
			cli.connect(DEFAULT_IP, DEFAULT_PORT);

		}
		//==== We want to use the server.
		else {

			System.out.println("Starting server...\nWaiting for clients...");

			Server srv = new Server();

			// To keep track of time, serves no purpose outside of counting minutes
			long start   = System.nanoTime();
			long minute  = 60_000_000_000L;
			int  counter = 1;

			srv.run(); // server is started

			// not the best but it should do for this project. we could probably
			// use synchronized methods and wait() calls, but I assumed this went
			// outside the scope of this project, even if it seems like a better
			// way of doing it
			while (srv.isRunning()) {
				// again, just having some fun counting the minutes
				if (((System.nanoTime() - start) > minute * counter)) {
					System.out.println("Every sixty seconds a minute passes...");
					System.out.flush();
					counter++;
				}
			}
		}

		// cosmetic
		System.out.println("Shutting down the " + (choice == '0' ? "server..." : "client..."));
		System.out.flush();
	}


}



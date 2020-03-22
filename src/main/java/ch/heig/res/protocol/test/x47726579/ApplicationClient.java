package main.java.ch.heig.res.protocol.test.x47726579;

import main.java.ch.heig.res.protocol.client.x47726579.Client;

public class ApplicationClient
{
	public static void main(String[] args)
	{

		Client cli = new Client();

		cli.connect("192.168.1.126", 3300);


	}

}

package main.java.ch.heig.res.protocol.protocol.x47726579;

/**
 * This class is a multi-threaded server of the custom presence protocol. The
 * server binds a socket on the specified port and waits for incoming connection
 * requests. It keeps track of connected clients in a list. When new clients
 * arrive, leave or send messages, the server notifies all connected clients.
 *
 * @author Olivier Liechti
 * @modified_by Laurent Scherer
 */
public class Protocol
{
	public static final int    DEFAULT_PORT = 3300;
	public static final String CMD_GREET    = "Greetings";
	public static final String CMD_ADD      = "ADD";
	public static final String CMD_SUB      = "SUB";
	public static final String CMD_MUL      = "MUL";
	public static final String CMD_EXT      = "EXT";
}
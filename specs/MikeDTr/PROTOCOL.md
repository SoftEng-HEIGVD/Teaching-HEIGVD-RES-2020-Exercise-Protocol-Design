#Specs

* What transport protocol do we use?
		TCP
* How does the client find the server (addresses and ports)?
		Address : 	Given (localhost)
		Port :		8080
	
* Who speaks first?
		the client speaks first
	
* What is the sequence of messages exchanged by the client and the server? (flow)
		Client => Server : "Hello"
		Server => Client : "Hello, type help for the help menu"
		Client => Server : "help"
		Server => Client : "Help menu"
		Client => Server : "add 2 2"
		Server => Client : "4"
		Client => Server : "sub 18 5"
		Server => Client : "3"
		....
		
* What happens when a message is received from the other party? (semantics)
		client : affiche le message
		serveur : interprete la commande et l'execute (parse and use variable)
	
* What is the syntax of the messages? How we generate and parse them? (syntax)
	    message = <op> <var1> <var2>
		<op>    = add | sub | div | mul 
		<var2>  = <number>
		<var1>  = <number>
	
* Who closes the connection and when?

		* If no error detected 	: Client
		* An error occured		: Server
		* In case of timeout 	: Server

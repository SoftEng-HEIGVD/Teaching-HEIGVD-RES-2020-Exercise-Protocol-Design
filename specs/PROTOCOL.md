# What transport protocol do we use ?

+ We are going to use TCP transport protocol in order to make sure that the informations are exchanged
successfully between the server and the client. Indeed, TCP is a reliable data transfer service.

# How does the client find the server ?

+ IP address of the server is given by default: 192.196.200.1 

+ The server listens to port 1000 (port also chosen by default)

# Who speaks first ?

+ The client is going to speak first asking the server to connect.

# Flow : What is the sequence of messages exchanged by the client and the server ?

+ Three-Way Handshake (done only once to establish the connection between client and server)
Client: SYN     -> The client asks the server for connection.
Server: SYN-ACK -> The server accepts the connection request.
Client: ACK     -> The client tells the server it received its answer.

+ Calculator (repeated)
Client : The client asks the server to compute a calculation.
Server : The server answers the result to the client

# Semantics : What happens when a message is received from the other party ?
The other party sends an ACK (it acknoledges the message received)

# Syntax : What is the syntax of the messages ? How do we generate and parse them ?

+ We can choose XML for the syntax of the messages
Example:
<message id= "501">
	<operandeA> 1 </operandeA>
	<operateur> + </operateur>
	<operandeB> 2 </operandeB>
</message>

+ We can generate and parse messages using an XML parser like DOM or JDOM.

# Who closes the connection and when ?

+ The client closes the connection when it wishes to stop asking the server to perform calculations.


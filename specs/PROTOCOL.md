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

+ The syntax operations will be:
[OPERANDE] [OPERATEUR1] [OPERATEUR2]

Exemple: ADD 1 2 -> 1 + 2
ou SUB 2 1 -> 2 - 1
ou MULT 2 2 -> 2 * 2

+ We parse messages with space.

+ An example of communication would be :
CONNECTION
Client: SYN
Server: SYN-ACK
Client: ACK
CALCULATION REQUESTS
Client: ADD 1 2
Server: 3
Client: ACK
Client: SUB 2 1
Server: 1
Client: ACK
DISCONNECTION
Client: FIN
Server: ACK

# Who closes the connection and when ?

+ The client closes the connection when it wishes to stop asking the server to perform calculations.


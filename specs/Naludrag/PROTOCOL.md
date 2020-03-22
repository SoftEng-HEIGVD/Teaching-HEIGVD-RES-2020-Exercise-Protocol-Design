# Specification Protocol Design Exercise

In this file we wrote the different specifications of the application that implements a calculator in a server.

## What transport protocol do we use?

We use the TCP protocol.

## How does the client find the server (addresses and ports)?

The client must know the IP adress and the port of the server. For the port we decided to use the port 12302 and for the IP it is difficult to be able to decide that due to the fact that IP adresses can change.  
For the exercise we decided to use localhost has the adresse of connection for the client.

## Who speaks first?

The client speaks first by sending a "HELLO" message.
The server will then register the client and send a response message.

## What is the sequence of messages exchanged by the client and the server? (flow)

First, the client say    : "HELLO"  
Then, the server respond : "HELLO, GIVE CALCULATIONS(supported operators : + - * /)"  
The client send          : "1 + 3"  
The server answer        : "1 + 3 = 4"  
Client : ...  
Server : ...  
To finish the communication the client send : "BYE"

## What happens when a message is received from the other party? (semantics)

It is decoded in UTF-8. The encoding used by the server and clients has to be UTF-8.

Client->Server : Read the message check its validity and send the result.  
Server->Client : Read and print the message.

## What is the syntax of the messages? How we generate and parse them? (syntax)

Client : operand1 operator operand2  
Server : operand1 opreator operand2 = result  
The operands have to be integers but the result is a double.
That is because we implement the divison.  
The operators implemented are +, -, \*, /.

## Who closes the connection and when?

The client closes the connection with the "BYE" message.  
The server can also close the connection when an error occurs.

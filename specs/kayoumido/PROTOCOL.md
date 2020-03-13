# Lab 3 - Protocol Design

## Students

Ludovic Bonzon

Doran Kayoumi

## Transport protocol

Since the client is expecting an answer from the server, we've decided to use the `TCP` transport protocol.



## Communication between server and clients

Port : 49153

Address : 127.0.0.1 (if client is on the same machine as the server)



## Who speaks first

The client will initiate the dialog with the server.



## Message sequence

![](/home/ducky/HEIG-VD/S4/RES/labo/lab03/specs/kayoumido/img/sequence-diagramm.jpg)



## Message Reception

### Server

* First connection: Sends a greeting message to the connected client
* Calculation request: Processes the request
  * Valid : Performs the arithmetic operation and sends back the result
  * Invalid : Send back an error message

### Client

* First connection: Connects to the server
* Calculation request: Sends a formatted operation
* Answer reception: Is happy



## Message Syntax

### Client

We'll be using a assembly like syntax. For binary operations, three arguments are needed, the operation and two operands (e.g. MPY 42 10). The same idea is applied to unary operations but will require only two arguments.

### Server

Will just return the answer.



## Ending the connection

The client will end the connection with the server.
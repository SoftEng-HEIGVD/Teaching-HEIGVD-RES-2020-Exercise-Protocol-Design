# Protocol specifications

## Labo 2 - Specify and implement a client-server protocol

@Author : Laurent Scherer
@Date   : 20/03/2020

### What transport protocol do we use?

TCP will be our protocol of choice.

### How does the client find the server (addresses and ports)?

For the scope of this project there will be no network discovery from the client; our
 server will not declare itself and it will only have one IPv4 and port defined :
 
  * The client knows the server's IP
  * The client knows on which port the server is listening

### Who speaks first?

The client will speak first to the server.
The way we're model

### What is the sequence of messages exchanged by the client and the server? (flow)
###  What happens when a message is received from the other party? (semantics)
### What is the syntax of the messages? How we generate and parse them? (syntax)
### Who closes the connection and when?
# Protocol specifications

## Labo 2 - Specify and implement a client-server protocol

@Author : Laurent Scherer
@Date   : 20/03/2020

### What transport protocol do we use?

TCP will be our protocol of choice.

### How does the client find the server (addresses and ports)?

For the scope of this project there will be no network discovery from the client; our
 server will not declare itself and it will only have one IPv4 and port defined :
 
  * The client gets the server's IP at launch
  * The client knows on which port the server is listening (we'll use 3300)

### Who speaks first?

The client will speak first.
The way we're modelling our client-server protocol we wish to have our server talking
 as little as possible : it will only talk when talked to, and only answer one client
 at a time.

### What is the sequence of messages exchanged by the client and the server? (flow)

```
        [Server is listenning on a dedicated port]

CLI#001 : Greetings                # A client wishes to talk
SRV     : Greetings
CLI#001 : Asks for computation     # Can be done as long as necessary, or
SRV     : Answers                  # as long as there's no errors 
CLI#001 : Goodbye  
```


### What happens when a message is received from the other party? (semantics)

#### Client side 
  
  * Prints the server's message

#### Server side

"Greetings" message :
  * binds the connection with the new client

"Asked for computation" message : 
  * executes the math and sends the answer to the client

Unknown message : 
  * sends 

### What is the syntax of the messages? How we generate and parse them? (syntax)

### Who closes the connection and when?
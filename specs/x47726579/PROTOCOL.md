# Protocol specifications

## Labo 2 - Phase 1 - Specify a client-server protocol

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
The way we're modelling our client-server protocol we wish to have our server talking as little as possible : it will
 only talk when talked to, and only answer one client at a time.

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
  * executes the math and sends the answer to the client; only integers will be considered

"Goodbye" message : 
  * closes connection

Unknown message : 
  * sends a standard message

### What is the syntax of the messages? How do we generate and parse them? (syntax)

The server awaits the correct "Greetings" message from a client, otherwise it will not answer; once a client-server connection is established, any malformed message from the client will be answered by a unique "standard message" from the server. 

During a client-server connection, the server will first wait for a client message and then look for a `KEYWORD` at the
 start of such string.

The server will tokenize a message based on spaces, for example `KEYWORD A X` will become `KEYWORD`, `A`, `X`. It
 will also limit the size of accepted messages.

List of accepted keywords : 
  * `ADD`
  * `SUB`
  * `MUL`
  * `EXT`


### Who closes the connection and when?

The server closes the connection upon receiving `EXT` from the client and/or after a set period without activity.
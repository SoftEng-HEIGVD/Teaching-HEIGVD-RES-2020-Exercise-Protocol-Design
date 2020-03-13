# RES2020 - Lab03 - Bourqui, Mueller

#### 13.03.2020

## Phase 1

##### What transport protocol do we use?

TCP

##### How does the client find the server (addresses and ports)?

Serveur:
port: 6942
ip: 10.192.104.13

##### Who speaks first?

Client

##### What is the sequence of messages exchanged by the client and the server? (flow)

Client                          Server
        calucle moi 3 + 4  ->
            <- 7       
##### What happens when a message is received from the other party? (semantics)

The server parses the operator and the operands, does the calculation and responds with the result.

##### What is the syntax of the messages? How we generate and parse them? (syntax)

[operator];[operand1];[operand2]

##### Who closes the connection and when?

client when he doesn't use the server anymore

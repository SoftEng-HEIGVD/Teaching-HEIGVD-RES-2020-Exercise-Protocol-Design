# Communication protocol :

- What transport protocol do we use? \
TCP.

- How does the client find the server (addresses and ports)? \
We will enter the address manually and the port is 2205.

- Who speaks first? \
The client speaks first.

- What is the sequence of messages exchanged by the client and the server? (flow)
``` 
-- Server awaits connection
Client : -- connects with the port number
-- Server binds the connection
Client: HELLO
Server : HELLO CALC
Client : 2 * 4
Server : 8
Client : 3 + 9 * 2
Server : ERROR: COMP
Client : BYE
Serveur: Goodbye!
-- Connection closed
```
- What happens when a message is received from the other party? (semantics)
    - **What do you want me to calculate ?** : notifies that the connection is open.
    - **2 * 3** *example* : indicates the operation to compute.
    - **2 * 3 = 6** *example* : indicates the computed operation with it's result (after the = symbol obviously).
    - **Bye** : tells the server to close the connection.

- What is the syntax of the messages? How we generate and parse them? (syntax) \
The calculation requests MUST have the following structure : 
``` 
<number> <symbol> <number>
```
There cannot be more that two components to a computation.\
+ signs are implicit, - signs are explicit.\

Errors:\
If the servers can't process a computation, it will answer:
```
ERROR: _error_
```
Where _error_ can be any error message that will be display to the client

- Who closes the connection and when? \
The connection can be closed on two different occasions :
    - The client closes it
    - The server closes it after a certain amount of time of inactivity (eg.1 min)

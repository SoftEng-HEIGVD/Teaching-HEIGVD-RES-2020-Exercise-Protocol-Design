#  Teaching-HEIGVD-RES-2020-Exercise-Protocol-Design
##### Autor : Walid Massaoudi 

### What transport protocol do we use?
For this laboratory, we need a connection-oriented service that's mean the client must establish a connection with the server before exchanging data (handshake). So we will use the same principle of TCP protocol.
### How does the client find the server (addresses and ports)?
the proposed protocol should be able to guarantee a connection between two different machine (one is the client and the other is the server ) this connection can be deployed using an IP address, otherwise, if we need to run this protocol inside one machine we will not be able to maintain this one to one connection because of both side (client and server) have the same IP address, here is come the utility of the port number to identify each side. As we know all port between 0 and 1024 are reserved (well-known ports) so we sill chose a port number over 1024 (6663 for example ).
### Who speaks first?
The client send a  connection request to the server then the server answer with an acknowledgement . 
### What is the sequence of messages exchanged by the client and the server? (flow)

```sequence  
Client->Server: Hello!  
Server-->Client: Welcome! 
Client->Server: Data  
Server-->Client: ACK (result)
Client->Server: end 
Server-->Client: end 
Client->Server: ACK end 
  ```

### What happens when a message is received from the other party? (semantics)
### What is the syntax of the messages? How we generate and parse them? (syntax)
### Who closes the connection and when?

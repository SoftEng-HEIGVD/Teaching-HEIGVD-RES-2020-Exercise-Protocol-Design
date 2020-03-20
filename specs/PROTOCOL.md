auteur : Diluckshan Ravindranathan

####What transport protocol do we use?
We will be using TCP

####How does the client find the server (addresses and ports)?
With ip adresse of the conatainer at start of jar. Port 3000


####Who speaks first?
The server intiates the connexion and sends at the client a list of operations.


####What is the sequence of messages exchanged by the client and the server? (flow)
1. The serveur sends to the client a list of operation.
2.  The client sends to the server the operation to be done.
3.  The server sends the answer to the client, if there's an error, the server sends "error".



####What happens when a message is received from the other party? (semantics)

Client : Reads the message.  
Serveur : Calculate and sends the answer. 

####What is the syntax of the messages? How we generate and parse them? (syntax)
without error : 

1. Server sends a list of operations ` MUL, DIV, ADD, SUB`.  
2. Client sends the operation followed with the numbers and a space between them : `OPERATION NUM1  NUM2`.  
3. Server sends the answer:  
  1. Without errors :  `Ans NUM`
  2. With calculation errors such as division by 0 : `ERROR`
  3. Client typing error : `Undefinied operation`
 
4. The Client gets one of the answer below.

####Who closes the connection and when?

The client chooses to close the connection when he's done.

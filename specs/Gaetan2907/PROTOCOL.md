### Specification 

**What transport protocol do we use?**

- Protocol used : TCP 

**How does the client find the server (addresses and ports)?**

- Addresses : 
  - Listen port : 2205 
  - Address : localhost (127.0.0.1)

**Who speaks first?**

- The client speaks first 

**What is the sequence of messages exchanged by the client and the server? (flow)**

- The client send an operation to the server 

  - The server understand the operation and send back the answer 
  - The server doesn’t understand and send an error message 

- Example 

  ```
  Server : Listen for any new connection 
  Client : Start a new connection 
  Server : Bind connection 
  Server : Welcome Message 
  Client : operation to process 
  Server : answer 
  Client : EXIT 
  Server : close the connection 
  ```

  

**What happens when a message is received from the other party? (semantics)**

- The server wait on the client (loop) to send a request, when it receives a message it process it 

**What is the syntax of the messages? How we generate and parse them? (syntax)**

- The syntax is : “number operator number”, for example “5 * 7”, the server will parse the message 

**Who closes the connection and when?**

- The server will close the connection when the client send EXIT 


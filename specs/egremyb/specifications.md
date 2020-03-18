# Specifications

**What transport protocol do we use?**

  We use the TCP protocol.

**How does the client find the server (addresses and ports)?**

  If both on application are running on the same machine :
  - Adresses : 127.0.0.1 (localhost)
  Else :
  - Client :
  - Server :
  
  Port used : 2020

**Who speaks first?**

  The client initiate the communication.

**What is the sequence of messages exchanged by the client and the server? (flow)**

  1. The client notify the server his intention of communicate.
  2. The server accepts the request and send his appobation. He is ready to compute client equation.
  3. The cient sends his equation to solve.
  4. The server returns :
      - An error if the request is badly written.
      - The solution if the request is well-written.
  5. The client can do steps 3. again.
  6. The client notify the server his intention to end the communication and end his communication channel with the server.
  7. The server end his communication channel with the client.

**What happens when a message is received from the other party? (semantics)**

  - The server process it and send the result to the client.
  - The client shows the response on screen.

**What is the syntax of the messages? How we generate and parse them? (syntax)**

  Syntaxes for the second question :
  1. Hello message : "hello".
  2. Acceptance message : "welcome".
  3. Equation message : "\<number\> \<operator\> \<number\>".
     - \<number\> : Any decimal number like 3, 5.2, -8, -1.2 .
     - \<operator\> : Only [+, -, *, /, ^].
     - Each element his separated by a blank space.
  4. 
     - Error message : "wrong".
     - Solution message : "\<number\>".
       - \<number\> : Any decimal number like 3, 5.2, -8, -1.2 .
  5. Syntaxe from point 3. need to be applied.
  6. End message : "bye".
  7. No message here.

**Who closes the connection and when?**

The client initiates the closing of the communication when he need to end his process. But before, he will notifiy the server. Then the server closes his end too.

# Protocol design

#### What transport protocol do we use ?

TCP

#### How does the client find the server (addresses and ports) ?

The server opens the port 2020.
 
#### Who speaks first ?

The client initiate the conversation.

#### What is the sequence of messages exchanged by the client and the server ?

1.  Client -> Server : "Hello!".
2.  Server -> Client : "Welcome!".
3.  Server -> Client : "I am a calculator. Please chose an operator : **ADD**, **SUB**, **MUL** and **DIV**".
4.  Client -> Server : "**Operator**".
5.  Server -> Client : "Received **Operator**".
6.  Server -> Client : "Enter first operand".
7.  Client -> Server : "**Operand**".
8.  Server -> Client : "Received **Operand**".
9.  Server -> Client : "Enter second operand".
10. Client -> Server : "**Operand**".
11. Server -> Client : "Received **Operand**".
12. Server -> Client : "**Operand** **Operator** **Operand** = **Result**"

#### What happens when a message is received from the other party ?

- When the client receives a message, it is display in terminal.
- When the server receives a message, he may do some calculations and then answer the client.

#### What is the syntax of the messages ? How we generate and parse them ?

- Initiation message from the client is "**Hello!**"
- Server response to client's initiation is "**Welcome!**"
- **Operator** has to be *ADD*, *SUB*, *MUL* or *DIV*
- **Operands** can be any number.

#### Who closes the connection and when ?

The server closes the connection after sending the result to the client.

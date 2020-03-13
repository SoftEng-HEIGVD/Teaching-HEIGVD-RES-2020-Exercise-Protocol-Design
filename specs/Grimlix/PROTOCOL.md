#**What transport protocol do we use :**
Protocol : TCP

#**How does the client find the server (addresses and ports)**
adress : localhost
port : 2020

#**Who Speaks first ?**
The client speaks first, he will send an "Hello" to the server (calculator) to start the communication.

#**What is the sequence of messages exchanged by the client and the server? (flow)**

The client starts a connection with the server. The server ask for the first operand.
The client sends the first operand. The server asks for the operator.
The client sends the operator. The server ask for the second operand.
The client send the second operand. The server gives the result and stops the communication.

#**Syntax**
"Hello"
"Hey! I'm a calculator. Enter the first operand."
"<number>"
"What is the operator?"
"<operator>"
"What is the second operand?"
"<number>"
"The result is : <result>"


#Remark:
The server will be created with netcat and the command :
nc -kl ==> continue listening  after disconnection 


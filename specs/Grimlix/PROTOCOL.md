#**What transport protocol do we use :**
Protocol : TCP

#**How does the client find the server (addresses and ports)**
adress : localhost
port : 2020

#**Who Speaks first ?**
The client makes the connection with netcat. But the server will write in first asking what the client want to calculate.

#**What is the sequence of messages exchanged by the client and the server? (flow)**

The client starts a connection with the server. The server ask for the first operand.
The client sends the first operand. The server asks for the operator.
The client sends the operator. The server ask for the second operand.
The client send the second operand. The server gives the result and stops the communication.</br>

**Possible errors:**</br>
-The operand value is incorrect. The server ask again.
-The operator value is incorrect. The server ask again.

#**Syntax**
(CMD_HELLO)
"Hey! I'm a calculator. Enter the first operand."

(OPERAND_ERROR)
"Wrong operand. Write again."

(OPERATOR)
"What is the operator?"

(OPERATOR_ERROR)
"Wrong operator. Write again."

(OPERATOR)
"What is the second operand?"

(OPERAND_ERROR)
"Wrong operand. Write again."

(RESULT)
"This is the result : "


#Remark:
The server will be created with netcat and the command :
nc -kl ==> continue listening  after disconnection.</br>

**Limits of the calculator:**</br>
-This calculator can't handle negativ values.
-Only the operation DIVISION, MULTIPLICATION and ADDITION are implemented.

![protocol].\img.protocol.PNG


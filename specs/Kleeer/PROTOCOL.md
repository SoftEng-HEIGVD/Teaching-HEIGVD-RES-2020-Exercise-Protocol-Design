# Specs

**Protocol** :  TCP

**IP address** : 

**Port** :      2020

The client speaks first.

**Flow** :

1. Client -> Server : "Hello"

2. Server -> Client : "Welcome to my super calculator! Valid operators are '+', '-', '*', '/'."

3. Client -> Server : "[Operand] [Operator] [Operand]"

4. Server -> Client : "[Operand] [Operator] [Operand] = [Result]" OR "Invalid operation"

5. Repeat 3 to 4

6. Client -> Server : "Close"

**Semantics** :
- When the client receives a message from server, it displays it in the terminal.
- When the server receives a message from client, it parses it, and either computes the result or generates an error 
message and sends it to the client.

**Syntax** :
[Operand] and [Result] are numbers (can be double, negative).

Numbers :
- optional '-' or '+'
- at least 1 digit [0-9]
- optional '.' and additional digits
- no spaces

[Result] has maximum 6 decimal digits.

[Operator] must be either '+', '-', '*', '/'

The syntax of messages desbribed in **Flow** section must be strictly respected (including spaces) to get an answer 
from server.

The client sends the request to close the connection, but the closure itself is done both on client and server-side.
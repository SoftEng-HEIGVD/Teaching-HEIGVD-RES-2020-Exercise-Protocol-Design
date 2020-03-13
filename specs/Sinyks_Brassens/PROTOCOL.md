What transport protocol do we use?
* We use UDP

How does the client find the server (addresses and ports)?
* IP: splinux-XPS, port 2020

Who speaks first?
* The client

What is the sequence of messages exchanged by the client and the server? (flow)
* The client sends 3 messages: the operator, the left operand and the right operand. The server confirms each messages asking for the left operand, the right operand or simply answers with the result once he received the 3 necessary messages.

What happens when a message is received from the other party? (semantics)
* see answer above

What is the syntax of the messages? How we generate and parse them? (syntax)
* we send ASCII characters, with a maximum of 100 bytes, ended by the null character ('\0'). e.g. "*\0", "42\0"

Who closes the connection and when?
* The client closes the connection. The server always listen to whatever he receives.

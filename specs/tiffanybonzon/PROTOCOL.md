# Phase 1: write the specification
## What transport protocol do we use?
We use TCP

## How does the client find the server (addresses and ports)?
The server can be reached by its IP address (127.0.0.1 if server and clients are on the same machine), and the port 2112

## Who speaks first?
The client initiates the exchange

## What is the sequence of messages exchanged by the client and the server? (flow)
1. Client initializes connection ("Hello")
2. Servers answers with operation list ("OP : ADD SUB MULT DIV POW")
3. Client answers ("OP NUM1 NUM2")
4. Server sends answer
5. Server waits for next operation ("OP NUM1 NUM") or for client to end the session ("END") or times out after 60 seconds

## What happens when a message is received from the other party? (semantics)
The server processes the requests
The client displays the answers

## What is the syntax of the messages? How we generate and parse them? (syntax)
1. Initialization message: "HELLO"
2. First server reply: 
	"Available operations: ADD SUB MULT DIV POW.
	 Syntax: <OP> <NUM1> <NUM2> (Each element is separated by a space, decimal is specified with a dot)"
3. Client replies: "OP NUM1 NUM2"
4. Server return calc answer: "<CALC RESULT>" or "Syntax Error / Operation unknown"
5. Client sends new calc or ends the session: "OP NUM1 NUM2" or "END"

## Who closes the connection and when?
The clients can tell the server to close the connection by sending "END"
The server automatically closes the connection after 60 seconds of inactivity

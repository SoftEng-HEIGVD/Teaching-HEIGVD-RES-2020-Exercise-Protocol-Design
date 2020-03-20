# Calculator Protocol

## Specs
- Network protocol : TCP
- Port : 22500
- Encoding : StandardCharsets.UTF_8

## FLOW
*All transmissions describe bellow are in the good order*

Client speaks first.

1. **Client** : Asks to begin a calculation by sending the keyword : **HELLO**

1. **Server** : Responds **READY** 

1. **Client** :  Sends a complete calculation string (for instance, "39 + 3")

1. **Server** : Fetches the calculation string, parse it, and respond with the result or **ERROR** if the client's request is incorrect

1. Client can now ask new calculations without sending keyword again

1. **Client** : Sends the keyword **BYE** to close the connection

*All other key word or wrong calculations will generate an **ERROR** response from the server.*

## Client Keywords - Grammar
*All keywords are not case sensitive*
- **HELLO** : Ask for a calculation loop
- **BYE** : Close a connection
- Calculations must formatted like so : "`operand1 operation operand2`" (space between each part)
	- Example : 1 + 2
- Available operation are : +, -, *, /

## Example
**Client** : HELLO  
**Server** : READY  
**Client** : 12 + 3  
**Server** : 15   
**Client** : 15 + 2  
**Server** : 17  
**Client** : hey   
**Server** : ERROR   
**Client** : 10/0  
**Server** : ERROR  
**Client** : BYE

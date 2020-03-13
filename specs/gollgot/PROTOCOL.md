# Calculator Protocol

## Specs
- Network protocol : TCP
- Port : 22500
- Server IP : 10.192.107.63
- Client IP : 10.192.95.120

## FLOW
*All transmissions describe bellow are in the good order*

Client speaks first.

1. **Client** : Asks to begin a calculation by sending the keyword : **calculation**

1. **Server** : Responds "Ready" or "Not Ready" (depends on the server workload)

1. **Client** :  Sends a complete calculation string

1. **Server** : Fetches the calculation string, parse it, and respond with the result

*Client can ask new calculation without sending keyword again*

Client : 
- Sends the keyword **bye** to close the connection

Server :
- Sends "Good bye - Client disconnected"

*All other key word will generate a "Unknown Command" from the server and all wrong calculation definition will generate an "Error : {{ msg }}" response from the server.*

## Client Keywords
- **calculation** : Start a calculation loop
- **bye** : Close a connection

## Example
**Client** : calculation  
**Server** : Ready
**Client** : 12 + 3  
**Server** : 15 
**Client** : 15 + 2
**Server** : 17
**Client** : Hello  
**Server** : Error : Bad calculation 
**Client** : 10 / 0
**Server** : Error : Devided by zero
**Client** : bye
**Server** : Client disconnected
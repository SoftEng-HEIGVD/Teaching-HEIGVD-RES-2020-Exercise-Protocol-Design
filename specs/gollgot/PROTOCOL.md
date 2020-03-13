# Calculator Protocol

## Specs
- Network protocol : TCP
- Port : 22500
- Server IP : 10.192.107.63
- Client IP : 10.192.95.120

## Transmission
*All transmissions describe bellow are in the good order*

Client speaks first.

Client : 
- Asks to begin a calculation by sending the keyword : **calculation**

Server :
- Responds to give it's calculation

Client : 
- Sends a complete calculation string

Server :
- Fetches the calculation string, parse it, and respond with the result

Client : 
- Sends the keyword **bye** to close the connection

Server :
- Sends "Good bye - Client disconnected"

*All other key word will generate a "Unknown Command" from the server and all wrong calculation definition will generate a "Bad Calculation" response from the server.*

## Client Keywords
- **calculation** : Start a calculation
- **bye** : Close a connection

## Example
**Client** : calculation  
**Server** : What calculation would you effectuate ?  
**Client** : 12 + 3  
**Server** : 15  
**Client** : Hello  
**Server** : Unknown command  
**Client** : calculation  
**Server** : What calculation would you effectuate ?  
**Client** : a12d * 22  
**Server** : Bad calculation  
**Client** : bye
**Server** : Client disconnected
# Calculator Protocol
## Authors Alban Favre and Alban Favre
### What
This is a small server-client application: the client sends an arithmetical instruction like ADD 1 1, the server does the calculation, and sends back the result.

### How
This server uses a TCP socket on port 2020 (the COVID-19 year).
the protocol is quite simple:
- client sends operator
- server sends ok
- client sends lefthand value
- server sends ok
- client sends righthand value
- server sends ok
- server sends result
- client sends ok
- client stops

it gives the result in double, but can only have Integer as left/righ handvalue.

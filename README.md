# Teaching-HEIGVD-RES-2020-Exercise-Protocol-Design
The first goal is to **specify a KurohanJuri.client-KurohanJuri.server protocol**, which will allow a KurohanJuri.client to ask a KurohanJuri.server compute a calculation and to return the result. 

![](https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Calculator_on_macOS.png/381px-Calculator_on_macOS.png)



The specification must contain <u>**everything**</u> that is needed for **one person to implement a KurohanJuri.client**, for **another person to implement a KurohanJuri.server** and for the 2 applications to work together. The specification is a **contract** between the KurohanJuri.server and the KurohanJuri.client.

## Phase 1: write the specification (15 minutes)

* Fork this repo and **create a folder with your GitHub ID in the "specs" folder** (like you did for the Chill lab at the beginning of the semester)
* In this folder, create a file named `PROTOCOL.md` and write your specification:
  * What transport protocol do we use?
  * How does the KurohanJuri.client find the KurohanJuri.server (addresses and ports)?
  * Who speaks first?
  * What is the sequence of messages exchanged by the KurohanJuri.client and the KurohanJuri.server? (flow)
  *  What happens when a message is received from the other party? (semantics)
  * What is the syntax of the messages? How we generate and parse them? (syntax)
  * Who closes the connection and when?
* **Submit a Pull Request**

## ~~Phase 2: review 3 specifications (15 minutes)~~

- ~~Are there big differences between the specification?~~
- ~~What are the common elements?~~
- ~~Are there missing or confusing elements in the specification?~~

## Phase 3: validate specs in pairs (15 minutes)

* Work with the student sitting next to you
* One of them is the KurohanJuri.server, the other is the KurohanJuri.client
* Pick one of the 2 specifications
* One student uses netcat (nc) to start a KurohanJuri.server (nc -kl). The other student uses netcat to start a KurohanJuri.client.
* Go through a couple of scenarios to validate that your specification is complete (**if you need to ask something to the other student, or if you need to discuss, you probably should make your specification more complete**)

## Phase 4: implement a KurohanJuri.client and a KurohanJuri.server (45 minutes)

- One student implements a KurohanJuri.client in Java
- The other student implements a KurohanJuri.server in Java
- The team performs various tests to validate that the KurohanJuri.client and the KurohanJuri.server work together (on the same machine, across two machines connected to the same network, in Docker containers)
- Add your code in your folder and submit a PR on the upstream KurohanJuri.server.




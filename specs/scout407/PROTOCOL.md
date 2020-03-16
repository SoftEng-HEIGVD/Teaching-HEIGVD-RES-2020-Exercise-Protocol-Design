# Protocl specification

## What transport protocol do we use?
* TCP

## How does the client find the server (addresses and ports)?
* IP   : 183.14.23.234
* Port : 1802

La personne doit connaître l'adresse IP et le port

## Who speaks first?

Le client parle en premier

## What is the sequence of messages exchanged by the client and the server? (flow)

Client (demande) -> Serveur (traitemment et réponse) -> Client (Affichage de la réponse)

## What happens when a message is received from the other party? (semantics)

Client -> Serveur : Lecture du message, vérification de la commande retourne soit une erreur si commande invalide soit la réponse à la demande

Serveur -> Client : Affichage du message

## What is the syntax of the messages? How we generate and parse them? (syntax)

**Syntaxe**

* Client  : "Operator" "operand 1" "operand 2"

* Serveur : "Result : Value"

Dans le cas d'une erreur retourne juste "Erreur dans la demande"

## Who closes the connection and when?

Le serveur après sa réponse
# Exercise Protocol Design - Phase 1
 Auteur : Christian Zaccaria & Nenad Rajic
 Date : 18.03.2020

## What transport protocol do we use?
TCP
## How does the client find the server (addresses and ports)?

Le port est fixé à 3385 et actuellement uniquement par localhost

## Who speaks first?
Le serveur en proposant les différentes actions au client.

## What is the sequence of messages exchanged by the client and the server? (flow)

Le serveur initie la communication et envoie la liste des actions proposées. En cas d'erreur, le serveur notifie le client afin de pouvoir retaper sa commande.

## What happens when a message is received from the other party? (semantics)

Client : Lit le résultat.

Serveur : Calcul des 2 opérandes et envoi du résultat.

## What is the syntax of the messages? How we generate and parse them? (syntax)

Serveur envoi la liste des différentes commandes possible (String) 

Client répond : [_OPERANDE NOMBRE1 NOMBRE2_]

Serveur récupére le resultat, le parse, effectue le calcul et re-envoie le résultat sous forme : [_RESULTAT_]

Le split de chaque information s'effectue au niveau des _WHITESPACES_.

## Who closes the connection and when?

Le client peut arrêter la connexion avec _STOP_. Quand au serveur il reste en écoute tout le temps afin de pouvoir directement intitié la connexion avec d'autres clients.
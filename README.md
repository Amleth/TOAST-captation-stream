# TOAST-captation-stream-twitter4j

Ce programme fait partie de la suite d'outils [TOAST](https://github.com/Amleth/TOAST-outils-pour-l-analyse-semiotique-de-Twitter).
Il s'agit d'un programme Java faisant une utilisation simple de [Twitter4J](http://twitter4j.org/en/index.html). Les tweets capturés via l'[API Streaming](https://dev.twitter.com/streaming/public) sont stockés dans une base [MongoDB](https://www.mongodb.com/). Ce choix est motivé par le fait que le format JSON est natif à ce SGBD, ce qui rend possible de stocker les tweets tels qu'ils donnés par l'API stream de Twitter sans transormation aucune.

## Mise en oeuvre

  1. Installer un JDK.
  2. Installer Maven et avoir l'executable `mvn` dans le path.
  3. Installer MongoDB, par exemple via Docker :

  > docker run --name some-mongo -d mongo
  > docker run -p 27017:27017 mongo

  4. Déclarer une application auprès de Twitter : [ici](https://apps.twitter.com/).

## Paramétrage

Dans le fichier de configuration `config`:

  1. Renseigner les *Consumer Key*, *Consumer Secret*, *Access Token* et *Access Token Secret* obtenus lors de la déclaration de l'application auprès de Twitter.
  3. Renseigner le port local sur lequel tourne MongoDB (`mongo-port`), ainsi que le nom de la base (`mongo-db`)et le nom de la collection recueillant les tweets souhaités (`mongo-collection`).
  4. Saisir la liste des mots suivis (`track`).

## Lancement

  1. `mvn compile`
  2. `mvn exec:java`

## Données

Chaque tweet capté donne lieu à un nouveau document MongoDB. Les différents champs renvoyés par l'API stream pour un tweet sont stockés sous une clef nommée `rawJson`, présente dans chaque document. Ce détail est à garder à l'esprit lors de l'exploitation de la base.
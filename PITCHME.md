# Akka Typed Introduction
A quick introduction to Akka Typed

---
### The next generation of Actor Systems
- AKKA Actor Systems have been around for a while (first public release in January 2010: Akka 0.5)                                     |
- Effective abstraction of units of computation that can be distributed across multiple environments                                   |
- Interaction between actors is limited to sending messages                                                                            |
- Actors implement a (partial) receive function (`Any ==> Unit`)                                                                       |
- Actors react to messages by changing their internal state and (optionally) sending a limited number of messages to other actors      |
- Changing state is done by updating internal var/vals and `become`ing a new actor that reacts differently to future incoming messages |

---
### Adding it to your project

```scala
libraryDependencies += 
    "com.typesafe.akka" %% "akka-actor-typed" % "2.5.13"
``` 

---
### Attributions
This presentation was created using the following resources:

- https://doc.akka.io/docs/akka/current/typed/index.html by Lightbend, Inc. [(licenses)](https://www.lightbend.com/legal/licenses)
- https://heikoseeberger.rocks/decks/20180516-welcome-akka-typed/#/ by Heiko Seeberger [(CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)

# Akka Typed Introduction
A quick introduction to Akka Typed

---
### Actor systems

- Actor systems have been around since the mid 1970s
@ul

- Effective abstraction of units of computation that can be distributed across multiple environments                                  
- Interaction between actors is limited to sending messages
- Processing of messages consists of:
    - sending limited number of (other) messages
    - update internal state
    - optionally terminate                                                                           

@ulend

+++
### Actor systems (continued)
- AKKA Actors first public release in Jan 2010: Akka 0.5
@ul
- Actors implement a receive function (`Any => Unit`)                                                                      
- Changing state is done by updating internal var/vals and `become`ing a new actor that reacts differently to future incoming messages

@ulend

+++
### Actor systems (continued)
- Drawbacks of the untyped nature of AKKA Actors
@ul
- No clear interface (what messages will be digested, what messages can be emitted)
- Multiple behaviours hidden behind same interface (`become` is not visible to the world outside the actor)
- No compile time assistance on correctness
- Reasoning about large actor systems becomes difficult

@ulend

---
### The next generation of Actor Systems

- Akka Typed tries to remove the drawbacks
- While keeping the high performance
- And providing interoperability
    - Typed Actors can interact with Untyped Actors
    - Untyped Actors can interact with Typed Actors
    - Untyped Actor Systems can be converted to Typed Actor Systems

```scala
val system = akka.actor.ActorSystem("UntypedToTypedSystem")
val typedSystem: ActorSystem[Nothing] = system.toTyped
```
More info on coexistence: https://doc.akka.io/docs/akka/current/typed/coexisting.html 

---
### Show me the code
The following pages show the working examples.

+++?code=project/Dependencies.scala&title=Adding dependency
@[4-5]

+++?code=build.sbt&title=Add to your project
@[11]

+++?code=src/main/scala/FirstSteps.scala&title=A simple example

---?code=src/main/scala/ChatRoom.scala&title=ChatRoom

---
### Attributions
This presentation was created using the following resources:

- https://doc.akka.io/docs/akka/current/typed/index.html by Lightbend, Inc. [(licenses)](https://www.lightbend.com/legal/licenses)
- https://heikoseeberger.rocks/decks/20180516-welcome-akka-typed/#/ by Heiko Seeberger [(CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)

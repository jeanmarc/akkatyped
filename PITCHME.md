# Akka Typed Introduction
A quick introduction to Akka Typed

---
### Actor systems
@ul

- Actor systems have been around since the mid 1970s
- Effective abstraction of units of computation that can be distributed across multiple environments                                  
- Interaction between actors is limited to sending messages
- Processing of messages consists of:
    - sending limited number of (other) messages
    - update internal state
    - optionally terminate                                                                           

@ulend

+++
### Actor systems (continued)
@ul
                                    
- AKKA Actors first public release in January 2010: Akka 0.5
- Actors implement a (partial) receive function (`Any => Unit`)                                                                      
- Changing state is done by updating internal var/vals and `become`ing a new actor that reacts differently to future incoming messages

@ulend

+++
### Actor systems (continued)
@ul

- Drawbacks of the untyped nature of AKKA Actors
    - No clear interface (what messages will be digested, what messages can be emitted)
    - Multiple behaviours hidden behind same interface (`become` is not visible to the world outside the actor)
- No compile time assistance on correctness
- Reasoning about large actor systems becomes difficult

@ulend

---
### The next generation of Actor Systems

@ul

- Akka Typed tries to remove the drawbacks
- While keeping the high performance
- And providing interoperability
    - Typed Actors can interact with Untyped Actors and vv
    - Typed Actor Systems can be used as Untyped Actor Systems

@ulend


---?code=project/Dependencies.scala
@title[Adding it to your project]
@[4-5]

---
### Attributions
This presentation was created using the following resources:

- https://doc.akka.io/docs/akka/current/typed/index.html by Lightbend, Inc. [(licenses)](https://www.lightbend.com/legal/licenses)
- https://heikoseeberger.rocks/decks/20180516-welcome-akka-typed/#/ by Heiko Seeberger [(CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)

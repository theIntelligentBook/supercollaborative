package supercollaborative.modelling

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.*
import com.wbillingsley.veautiful.templates.*
import Challenge.Level
import supercollaborative.{Common, MermaidDiagram}
import supercollaborative.versioncontrol.gitsim.*
import scala.collection.mutable
import com.wbillingsley.veautiful.MakeItSo
import org.scalajs.dom.html
import supercollaborative.given


def mermaidAndCode(mermaid:String, code:String, maxHeight:Int = 800) = <.div(
    ^.attr("style") := "border: 15px solid #fafafa; border-radius: 15px; margin: 20px; display: inline-flex; ",
    <.div(^.attr("style") := s"padding: 20px; overflow-y: auto; max-height: ${maxHeight}px",
        MermaidDiagram(mermaid)
    ),
    <.div(^.attr("style") := s"padding: 20px; overflow-y: auto; max-height: ${maxHeight}px",
        <.pre(code)
    ),
)

def captionedLargeImage(src:String, caption:String) = <.div(
  <.div(
    ^.attr("style") := "height: 800px", 
    <.img(^.attr("style") := "object-fit: contain; max-height: 100%;", ^.src := src, ^.alt := caption),
  ),
  <("label")(caption)
)

def floatRight(node:VHtmlNode) = <.div(
  ^.attr("style") := "float: right; margin-left: 50px;", 
    node
)

def bookCoverFloat(src:String, caption:String) = <.div(
  ^.attr("style") := "float: right; width: 300px; margin-left: 50px;", 
    <.img(^.attr("style") := "object-fit: contain; max-width: 100%;", ^.src := src, ^.alt := caption),
)

lazy val designPatternsDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Design Patterns").withClass("center middle")
  .veautifulSlide(<.div(
    <.h3("Hillside Terrace in Peru"),
    captionedLargeImage("https://upload.wikimedia.org/wikipedia/commons/7/71/Pisac006.jpg", "image: Alexson Scheppa Peisino, public domain")
  ))
  .veautifulSlide(<.div(
    <.h3("Lynchets in England"),
    captionedLargeImage("https://upload.wikimedia.org/wikipedia/commons/3/3e/Bishopstonelynchets2.jpg", "image: Mike Barratt, public domain")
  ))
  .veautifulSlide(<.div(
    <.h3("Hillside Terrace in the Philippines"),
    captionedLargeImage("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Banaue_Rice_Terrace_Close_Up_%282%29.JPG/2560px-Banaue_Rice_Terrace_Close_Up_%282%29.JPG", "image: Agricmarketing, public domain")
  ))
  .veautifulSlide(<.div(
    <.h3("Christopher Alexander"),
    bookCoverFloat("https://upload.wikimedia.org/wikipedia/en/b/bf/The_Timeless_Way_of_Building.jpg", "The Timeless Way of Building"),
    Common.marked("""
    |* The Timeless Way of Building* (1979)
    |
    |* What does it mean for a building or a town to be *alive*? The "quality without a name"
    |
    |* *"Each building gets its character from just the patterns 
    |which keep on repeating there."* 
    |
    |* What is needed to be able to "Sit on the porch, watching the world go by"?
    |""".stripMargin)
  ))
  .veautifulSlide(<.div(
    <.h3("Christopher Alexander"),
    bookCoverFloat("https://upload.wikimedia.org/wikipedia/en/e/e6/A_Pattern_Language.jpg", "A Pattern Language"),
    Common.marked("""
    |Example: *South-Facing Outdoors*
    |
    |* "People use open space if it is sunny, and do
    |not use it if it isnâ€™t, in all but desert climates"
    |
    |* "Always place buildings to the north* of the
    |outdoor spaces that go with them, and keep
    |the outdoor spaces to the south*. Never leave
    |a deep band of shade between the building
    |and the sunny part of the outdoors"
    |
    |* Related patterns: Half-Hidden Garden,
    |Positive Outdoor Space, Wings of Light,
    |Indoor Sunlight, North* Face, Sunny Place
    |""".stripMargin)
  ))
    .veautifulSlide(<.div(
    <.h3("Gang of Four"),
    bookCoverFloat("https://images-na.ssl-images-amazon.com/images/I/81gtKoapHFL.jpg", "Design Patterns (Gang of Four book)"),
    Common.marked("""
    |Takes the same concept of Design Patterns and applies it to Object-Oriented Software.
    |
    |> One thing expert designers know not to do is solve every
    |problem from first principles. Rather, they reuse solutions
    |that have worked for them in the past. When they find a
    |good solution, the use it again and again. Such
    |experience is part of what makes them experts.
    |Consequently, you'll find recurring patterns of classes and
    |communicating objects in many object-oriented systems.
    |These patterns solve specific design problems and make
    |object-oriented designs more flexible, elegant and
    |ultimately reusable. They help designers reuse successful
    |designs by basing new designs on prior experience. A
    |designer who is familiar with such patterns can apply them
    |immediately to design problems without having to
    |rediscover them." 
    |
    |Written in the early 2000s and some elements attuned to early-2000s Java
    |""".stripMargin)
  ))
  .markdownSlides("""
    |
    |### A note on patterns
    |
    |As programming languages evolve, some patterns may become subsumed into the language. For example, consider a language before `while` loops:
    |
    |```fortran
    |A: IF T > 10 GOTO B
    |   PRINT T++
    |   GOTO A
    |B: PRINT END
    |```
    |
    |Whereas in a language with `while` it might become:
    |
    |```java
    |while(t <= 10) {
    |  print(t++);
    |}
    |print(end);
    |```
    |
    |---
    |
    |## Parts of a pattern
    |
    |Design patterns typically have:
    |
    |* A name
    |* An intent
    |* Applicability (when's it good?)
    |* Structure
    |* Consequences
    |* Examples
    |* Related patterns
    |
    |---
    |
    |### Singleton
    |
    |**Intent:** Ensure a class only has one instance and provide a global point of access to it
    |
    |**Participants:** Singleton
    |
    |**Collaborators:** Clients access a singleton solely through the Singleton's `getInstance` operation
    |
    |**Examples:** Servers, Communications sessions, Registries
    |
    |Java:  
    |```java
    |public enum MySingleton {
    |
    |  INSTANCE;
    |
    |  // Fields and methods
    |
    |}
    |```
    |
    |Scala:  
    |```scala
    |object MySingleton {
    |  // Fields and methods
    |}
    |```
    |
    |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |### State pattern
    |
    |**Intent:** We have an item whose behaviour should change depending on what state it is in.
    |
    |**Examples:** Monster in a game (whether it patrols, chases, or runs away). By delegating the action to the state object, changing the state changes the behaviour.
    |
    |""".stripMargin),
    mermaidAndCode(
      """|classDiagram
         |  Context o--> State
         |  Context : act()
         |  <<interface>> State
         |  State : act(context)
         |  State <|.. StateA
         |  State <|.. StateB
         |""".stripMargin,
      """|interface MonsterState {
         |  public void act(Monster m);
         |}
         |
         |class Monster {
         |  MonsterState currentState; 
         |
         |  public void setState(MonsterState newState) {
         |    this.currentState = newState;
         |  }
         |
         |  public void act() {
         |    currentState.act(this);
         |  }
         |}
         |""".stripMargin
    )
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Strategy pattern - different in intent rather than structure
    |
    |**Intent:** We have some action that could be accomplished in a number of different ways
    |
    |**Examples:** Our monster could patrol by following a set path, or it could explore randomly
    |
    |""".stripMargin),
    mermaidAndCode(
      """|classDiagram
         |  Context o--> Strategy
         |  Context : act()
         |  <<interface>> Strategy
         |  Strategy : act(context)
         |  Strategy <|.. StrategyA
         |  Strategy <|.. StrategyB
         |""".stripMargin,
      """|interface PatrolStrategy {
         |  public void patrol(Monster m);
         |}
         |
         |class PatrollingState implements MonsterState {
         |  PatrolStrategy strategy; 
         |
         |  PatrollingState(PatrolStrategy strategy) {
         |    this.strategy = strategy;
         |  }
         |
         |  public void act(Monster m) {
         |    strategy.patrol(m);
         |  }
         |}
         |""".stripMargin
    )
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Mediator
    |
    |**Intent:** Mediator reduces chaotic dependencies between objects, when there are many participants. Rather than have each participant keep a reference to every other participator, instead they only keep a reference to a mediator.
    |
    |**Examples:** Telephone exchange
    |
    |""".stripMargin),
    mermaidAndCode(
      mermaid="""|classDiagram 
                 |  class Mediator {
                 |    +register(target)
                 |    +sendMessage(target, message)
                 |  }
                 |
                 |  class Colleague {
                 |    +receive(message)
                 |  }
                 |  Mediator o-- Colleague
                 |  Colleague <|.. Alice
                 |  Colleague <|.. Bob
                 |""".stripMargin,
      code   ="""|class Mediator {
                 |  Map<String, Colleague> colleagues = new HashMap<>();
                 |  
                 |  public void register(String name, Colleague c) {
                 |    colleagues.put(name, c);
                 |  }
                 |
                 |  public void sendMessage(String target, Message message) {
                 |    if (colleagues.containsKey(target)) {
                 |      colleagues.get(target).receive(message);
                 |    }
                 |  }
                 |}
                 |
                 |""".stripMargin,
      maxHeight=500                      
    ),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Decorator
    |
    |**Intent:** The decorator pattern is a flexible alternative to subclassing, that can allow objects to be "decorated" with extra abilities at run-time.
    |
    |**Example:** Special abilities in games; request handlers can be decorated with authentication filters 
    |
    |The example below is taken from *Effective Java* by Joshua Bloch: a map that counts how many times items have *ever* been put into it. Because it decorates a 
    |Map rather than inheriting from one, it can (safely) wrap any Map implementation.
    |
    |""".stripMargin),
    mermaidAndCode(
      mermaid="""|classDiagram
                 |  class Component {
                 |    <<interface>>
                 |    +operation()
                 |  }
                 |  class Decorator
                 |  <<abstract>> Decorator 
                 |  Component <|-- Decorator
                 |  Decorator o--> "1" Component : decorates
                 |
                 |  Decorator <|-- SuperPowerA 
                 |  Decorator <|-- SuperPowerB
                 |""".stripMargin,
      code   ="""|class CountingMap<K,V> implements Map<K,V> {
                 |  Map<K,V> m;
                 |  int count = 0;
                 |
                 |  public void CountingMap(Map<k,V> m) {
                 |    this.m = m;
                 |  }
                 |
                 |  public void put(K key, V value) {
                 |    this.count += 1;
                 |    m.put(key, value);
                 |  }
                 |
                 |  public void putAll(Map<? extends K, ? extends V> toAdd) {
                 |    this.count += toAdd.size();
                 |    m.putAll(toAdd);
                 |  }
                 |
                 |  public void remove(Object key) {
                 |    m.remove(key);
                 |  }                 
                 |}""".stripMargin,
      maxHeight=500                      
    ),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Observer
    |
    |**Intent:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and update automatically
    |
    |**Examples:** Telling all the non-player characters in a game about a game event
    |
    |""".stripMargin),
    floatRight(MermaidDiagram(
       """|classDiagram 
          |  class Subject {
          |    -listeners:List~Listener~ 
          |    + addListener(listener)
          |    + removeListener(listener)
          |  }
          |  class Listener {
          |    <<interface>>
          |    + update(event)
          |  }
          |
          |  Listener <|-- ConcreteListener
          |  Subject o-- "*" Listener : notifies
          |""".stripMargin    
    )),
    Common.marked("""
    |In the Gang of Four book, the Observer pattern is defined as just having an `update()` method (with no arguments) to notify it that something has changed. But this doesn't tell you 
    |*what* has changed. Java's `java.util.Observable` class is deprecated for this reason.
    |
    |In practice, you'll probably have seen event listeners more often, so if I write it like this, it might seem more familiar to you (e.g. from registering mouse events on buttons):
    |
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    <.h3("Observer and lambda functions"),
    floatRight(MermaidDiagram(
      """|sequenceDiagram 
                 |  actor Listener1
                 |  actor Listener2
                 |  participant Subject
                 |  actor Client
                 |
                 |  Listener1 ->>+ Subject : addListener(this)
                 |  Subject ->>- Listener1 : void
                 |
                 |  Listener2 ->>+ Subject : addListener(this)
                 |  Subject ->>- Listener2 : void
                 |
                 |  Client ->>+ Subject : modify(event)
                 |  Subject ->>+ Listener1 : update(event)
                 |  Listener1 ->>- Subject : void
                 |  Subject ->>+ Listener2 : update(event)
                 |  Listener2 ->>- Subject : void
                 |  Subject ->>- Client : void
                 |  
                 |""".stripMargin,           
    )),
    Common.marked("""
                    |The observer pattern lets the subject keep a list of observers that need notifying whenever anything changes.
                    |
                    |As Observer (or Listener) is often a *single abstract method* interface, it can often be expressed as a lamda
                    |
                    |```java
                    |public interface Listener<Event> {
                    |  void update(Event e);
                    |}
                    |```
                    |
                    |```java
                    |subject.addListener((event) -> {
                    |  // do something
                    |})
                    |```
                    |
                    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Observer and lambdas...
    |
    |Since Java 8, Java has gradually been picking up functional programing concepts. In Scala, we might say that a listener is a function that accepts
    |an event:
    |
    |```scala
    |type MyListener = MyEvent => Unit
    |```
    |
    |In Java, we could use `java.util.function.Consumer<Event>` as our listener type.
    |""".stripMargin),
    mermaidAndCode(
      mermaid="""|classDiagram 
                 |  class Subject~Event~ {
                 |    -listeners:List~Consumer&lt;Event&gt;~ 
                 |    + addListener(listener)
                 |    + removeListener(listener)
                 |  }
                 |
                 |  Consumer~Event~ <|-- ConcreteListener 
                 |  Subject o-- Consumer : notifies
                 |""".stripMargin,
      code   ="""|class MySubject {
                 |  List<Consumer<MyEvent>> listeners = new ArrayList<>();
                 |  
                 |  public void addListener(Consumer<MyEvent> l) {
                 |    listeners.append(l);
                 |  }
                 |
                 |  public void removeListener(Consumer<MyEvent> l) {
                 |    listeners.remove(l);
                 |  }
                 |
                 |  private void notifyListeners(MyEvent event) {
                 |    for (Consumer<MyEvent> l : listeners) {
                 |      l.accept(event);
                 |    }
                 |  }
                 |}
                 |
                 |// Somewhere else in code
                 |someSubject.addListener((event) -> {
                 |  // do something with the event
                 |})
                 |""".stripMargin,
      maxHeight=500                      
    ),
  ))
  .markdownSlides("""
    |### Notes on Design Patterns
    |
    |* They describe patterns people tend to use, rather than inventing new concepts. 
    |
    |* There are a lot of patterns. The Gang of Four book identified 23 in early 2000's (Java) code.
    |
    |* As we're talking about how other people implement common structures in code, they evolve over time as programming languages evolve.
    |
    |* It's probably more common to see patterns talked about in *other* contexts now. E.g., UI patterns.
    |
    |
    """.stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
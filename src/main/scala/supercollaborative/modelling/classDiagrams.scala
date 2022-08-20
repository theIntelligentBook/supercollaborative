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

def codeAndMermaid(s:String, maxHeight:Int = 800) = <.div(
    ^.attr("style") := "border: 15px solid #fafafa; border-radius: 15px; margin: 20px; display: inline-flex; ",
    <.div(^.attr("style") := s"padding: 20px; overflow-y: auto; max-height: ${maxHeight}px",
        <.pre(s)
    ),
    <.div(^.attr("style") := s"padding: 20px; overflow-y: auto; max-height: ${maxHeight}px",
        MermaidDiagram(s)
    ),
)


lazy val classDiagramDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# UML & Class Diagrams").withClass("center middle")
  .markdownSlides("""
  |### Talking about programs
  |
  |We often need to communicate information about the programs we're working with
  |
  |This is often easier using diagrams (they're more compact):
  |
  |* Diagrams for the structure of the system
  |
  |* Diagrams for interactions between parts of the system
  |
  |* Diagrams about how the system moves from one state to another
  |
  |---
  |
  |### Class modelling
  |
  |We're going to use the UML Class Diagram Syntax
  |
  |* *Unified Modelling Language* - standardised notation developed in the late 1990s, based on earlier notations by famous engineers. ISO Standard since 2005
  |
  |* Detailed enough that it *could* be used for code generation. "Model Driven Development".
  |
  |* But typically, we're going to use it for *explaining* and *documenting* code. 
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |## How we use diagrams
    |
    |When we're *talking* to another developer, we just hand-draw them.
    |
    |If we're putting them in documentation, it's easier to *write the diagram in code*
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Car
                      |
                      |  Driver ..> Car : drives 
                      |  Car o-- "3..4" Wheel : has 
                      |  Car -- Person :  owns
                      |  Driver --|> Person: is a 
                      |""".stripMargin),
  ))
  .markdownSlides("""
  |
  |### Diagrams from code
  |
  |Probably the two most common lightweight tools are
  |
  |* [PlantUML](http://plantuml.com/)  
  |  
  |  - Detailed, well-established, rendered server-side using GraphViz layout engine. But... only rendered server-side and the web page is full of advertising
  |
  |  - [online server](http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000), or there are [alternatives](https://plantuml-editor.kkeisuke.com/)
  |
  |* [Mermaid.js](https://mermaid-js.github.io/mermaid/#/)
  |
  |  - JavaScript library. Rendered client-side. Included in GitHub and GitLab Markdown so it can be used in Wikis and READMEs.  
  |    <pre>
  |       ```mermaid
  |       classDiagram
  |         class Dog
  |       ```
  |    </pre>
  |
  |  - [Live editor](https://mermaid.live/edit)
  |
  |  - Not quite as detailed. Some limitations on diagrams.
  |
  |I'll *usually* show the Mermaid version, because it's easier for you to include in GitHub or GitLab Wiki pages
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |## A class
    |
    |At its simplest, a class is just a box with it's name in it. (But Mermaid will still tend to draw a "three box" version)
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Dog
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A class
    |
    |If we want to describe the fields and methods, we have a three-celled box:
    |
    |* Name at the top
    |* Fields in the middle
    |* Methods at the bottom
    |
    |**Note**: There's no "two-cell" variety. Even if you just want to show the methods but not the fields, use three cells.
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Dog {
                      |    -Colour colour
                      |    #int weight
                      |    +feed(amount)
                      |    +pat()
                      |  }
                      |""".stripMargin),
    Common.marked("""
                  |We can specify the visibility of fields and methods:
                  |- `-` private; `+` public; `#` protected; `~` package (Java default visibility) 
                  |
                  |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Alternative syntax
    |
    |Mermaid also supports an alternative to the curly-brace syntax for classes:
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Dog 
                      |  Dog : -Colour colour
                      |  Dog : #int weight
                      |  Dog : +feed(amount)
                      |  Dog : +pat()
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Annotations and Stereotypes
    |
    |*Stereotypes* can be written above the class name in &laquo;guillemets&raquo;
    |
    |e.g., abstract, interface, enumeration
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Runnable {
                      |    <<interface>>
                      |    run() void
                      |  }
                      |
                      |  class Suit {
                      |    <<enumeration>>
                      |    CLUBS
                      |    DIAMONDS
                      |    HEARTS
                      |    SPADES
                      |  }
                      |""".stripMargin),
    Common.marked("""
                  |By convention, we could also show an interface or abstract class by putting its name in italics, but that's not great for hand-drawn diagrams.
                  | 
                  |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Abstract and static members
    |
    |Mermaid does abstract and static members via an annotation after the member
    |
    |Conventionally, static members are underlined and abstract members are shown in italics.
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class MyInterface {
                      |    <<abstract>>
                      |    int STATIC_CONSTANT$
                      |    abstractMethod()* void
                      |    concreteMethod() void
                      |  }
                      |""".stripMargin),
    Common.marked("""
                  |Again, in hand-drawn diagrams, italics aren't clear. So we'd just put "&laquo;abstract&raquo;" before the method.
                  | 
                  |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Generics
    |
    |Mermaid uses `~` to wrap generics
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Map~K,V~ {
                      |    empty()$ 
                      |    keySet() Set~K~
                      |    values() Collection~V~
                      |    put(K key, V value)
                      |  }
                      |""".stripMargin),
    Common.marked("""
                  |There are some limitations, such as it doesn't support nested generics (e.g. `Set<Map.Entry<K, V>>`)
                  | 
                  |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Inheritance and subtyping
    |
    |Normally, we want to show *relationships between classes*. Let's start with inheritance. 
    |Usually shown with a white arrowhead, but Mermaid does it in black here. Solid line for extending classes, dotted for implementing interfaces.
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Messageable
                      |  <<interface>> Messageable
                      |  Messageable : sendMessage(message) void
                      |
                      |  class User 
                      |  User : login() void
                      |  User : logout() void
                      |
                      |  class Moderator
                      |  Moderator : +ban(User user)
                      |
                      |  User <|-- Moderator 
                      |  Messageable <|.. Moderator
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Associations
    |
    |For ordinary references between classes, use a line.
    |
    |*Multiplicities* show how many:
    |
    |* number, e.g. *1*
    |* min..max, e.g. *3..4*
    |
    |Note that we often want to label the relationship. It won't always be obvious what the relationship means. Is the line between a user and a document the reader or the author?
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class Car
                      |  Car : +drive()
                      |
                      |  Car "1" -- "3..4" Wheel : rides on
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Navigability
    |
    |On rare occasions, we want to note that a relationship can only be navigated in one direction. In which case use an arrow
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  class SinglyLinkedList~T~ 
                      |  SinglyLinkedList --> "1" SinglyLinkedList : tail
                      |""".stripMargin),
    Common.marked("""
    |Mermaid doesn't have object diagrams (e.g. for showing an example linked list), but does have a way of just putting shapes together
    |
    |""".stripMargin),
    codeAndMermaid("""|graph LR
                      |  A --> B --> C --> D --> Nil
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Aggregation and Composition
    |
    |Sometimes we want to show something is composed of parts. 
    |
    |The wheels are part of the car. But they can be taken off - the wheels' lifecycle and the car's lifecycle aren't identical. (Aggregation)
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  Car o-- "1..3" Wheel
                      |""".stripMargin),
    Common.marked("""
    |A Protest comprises Protestors, but if someone isn't at a Protest they're not a Protestor (the lifecycles are identical). (Composition)
    |
    |""".stripMargin),
    codeAndMermaid("""|classDiagram
                      |  Protest *-- "*" Protestor
                      |""".stripMargin),
  ))
  .markdownSlides("""
  |
  |### Packages
  |
  |Unfortunately, Mermaid doesn't have a mechanism for showing packages (e.g. see the Architecture deck)
  |
  |PlantUML does and uses a similar syntax, though. 
  |
  |![Three layers](http://www.plantuml.com/plantuml/svg/TP2n2eCm48RtFCNXr4Ne8oXrTt3eK3gSoL52R0ovQvVITwynbO3Wzil_VvzS6XQCydfEAg1s-22ehZJ2PRBafOAFKW2t_7K7P71QgQyguuxOoHoE0RC3R3ySZ-rkt7eJPskE0Sn08GQARuhRiWrXiXh5pXvuRV8Hl9Ifxr0GK6jYNekkxLUVGS4HETttJycOVkGqrATpdHIcH5iYbhZOSjuagY5lbhV-0G00)
  |
  |You can link a PlantUML diagram as an image from the PlantUML server. The url contains a compressed version of your diagram sourcecode.
  |
  |[Source rendered from PlantUML](http://www.plantuml.com/plantuml/uml/TP2n2eCm48RtFCNXr4Ne8oXrTt3eK3gSoL52R0ovQvVITwynbO3Wzil_VvzS6XQCydfEAg1s-22ehZJ2PRBafOAFKW2t_7K7P71QgQyguuxOoHoE0RC3R3ySZ-rkt7eJPskE0Sn08GQARuhRiWrXiXh5pXvuRV8Hl9Ifxr0GK6jYNekkxLUVGS4HETttJycOVkGqrATpdHIcH5iYbhZOSjuagY5lbhV-0G00)
  |
  |---
  |
  |### Diagram drawing advice
  |
  |Diagrams are a communication tool. So:
  |
  |* Show what you want to explain
  |
  |* Don't show things you don't want to explain. (It makes it harder to see what you are trying to say)
  |
  |* Remember that you can use more than one diagram to explain your point. 
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
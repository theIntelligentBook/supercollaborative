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


lazy val seqDiagramDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Sequence Diagrams").withClass("center middle")
    .veautifulSlide(<.div(
    <.h3("Sequence Diagrams"),
    <.div(^.attr("style") := "float: right",
      MermaidDiagram("""|sequenceDiagram
                      |  actor Dad
                      |  actor Rebecca
                      |
                      |  Dad ->>+ Rebecca : "Knock knock"
                      |  Rebecca -->>- Dad : "Who's there?"
                      |  Dad ->>+ Rebecca : "Owls go"
                      |  Rebecca -->>- Dad : "Owls go who?"
                      |  Dad ->>+ Rebecca : "Yes, they do."
                      |  Rebecca -->>- Dad: groan 
                      |""".stripMargin),
    ),
    Common.marked("""
    |
    |* *Class diagrams* showed the structure of our entities (static relationships)
    |
    |* *Sequence diagrams* can show the dynamic interactions between them
    |
    |* Each participant has a *lifeline*. Time runs down the page.
    |
    |* The box shows when that participant is *active*
    |
    |* Solid lines represent messages being sent. Dotted lines are responses being returned synchronously.
    |
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Sequence diagrams in Mermaid
    |
    |Mermaid has a reasonably simple syntax for sequence diagrams. 
    |
    |We can explicitly say `activate` and `deactivate` a participant, or the `+` and `-` at the ends of the arrows is a shortcut for synchronous calls.
    |
    |""".stripMargin),
    codeAndMermaid("""|sequenceDiagram
                      |  actor Dad
                      |  actor Rebecca
                      |
                      |  activate Dad
                      |  Dad ->>+ Rebecca : "Knock knock"
                      |  Rebecca -->>- Dad : "Who's there?"
                      |  Dad ->>+ Rebecca : "Owls go"
                      |  Rebecca -->>- Dad : "Owls go who?"
                      |  Dad ->>+ Rebecca : "Yes, they do."
                      |  Rebecca -->>- Dad: groan 
                      |  deactivate Dad
                      |""".stripMargin)
  ))
  .markdownSlides("""
  |
  |### Creating and destroying
  |
  |* Creating participants is shown by putting their box further down the lifeline.
  |
  |* Destroying participants is shown with an X
  |
  |Unfortunately, Mermaid doesn't support this yet, so we'd need to use PlantUML to show this. It has a similar syntax, but some differences in the arrows.
  |
  |![plantUML example](//www.plantuml.com/plantuml/png/RP11KiCm34NtEeMcArXmWIwOmee3S07NLgadZfLnv49lZvCAfIJToVB_UbywZEAAr3x1EGR24xCS-GV0UU62R-cBIkOCGnD57mUN1NTdIaEWiTlZXrOxTACz_ejbZv_HToIJHakT_avDHp33uiK9BRo_0bzURIxkqNC_91VpuZ2eBpIz5M2Uvh-rcZlJDnIIMZBAtCkrFrA1GDi0S7CrlyQS_2pnT3MsVYnseuDQxk5r24q1QwdcuKuzguKBZPmk59xasJX6tbxXVc_RPJLckwjbU7msdtWaQ6qfV7rqO4skoXboe1ngdtu1)
  |
  |[PlantUML source for this diagram](//www.plantuml.com/plantuml/png/RP11KiCm34NtEeMcArXmWIwOmee3S07NLgadZfLnv49lZvCAfIJToVB_UbywZEAAr3x1EGR24xCS-GV0UU62R-cBIkOCGnD57mUN1NTdIaEWiTlZXrOxTACz_ejbZv_HToIJHakT_avDHp33uiK9BRo_0bzURIxkqNC_91VpuZ2eBpIz5M2Uvh-rcZlJDnIIMZBAtCkrFrA1GDi0S7CrlyQS_2pnT3MsVYnseuDQxk5r24q1QwdcuKuzguKBZPmk59xasJX6tbxXVc_RPJLckwjbU7msdtWaQ6qfV7rqO4skoXboe1ngdtu1)
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |### Loops and Alts are shown as fragments
    |
    |""".stripMargin),
    codeAndMermaid("""|sequenceDiagram
                      |  actor Teacher
                      |  participant Moodle
                      |
                      |  Moodle ->>+ Teacher : Assignment is due
                      |  alt Manual Marking 
                      |    loop each assignment
                      |      Teacher ->>+ Moodle : getAssignment()
                      |      Moodle ->>- Teacher : assignment
                      |      Teacher ->> Teacher : mark(assignment)
                      |      activate Teacher
                      |      Teacher ->>+ Moodle : setMark(mark)
                      |      Moodle -->>- Teacher: ok
                      |      deactivate Teacher
                      |    end 
                      |  else Automated marking
                      |    Teacher ->>+ Moodle : getMarks()
                      |    Moodle -->>- Teacher : marks
                      |  end
                      |
                      |  deactivate Teacher
                      |""".stripMargin)
  ))
  .markdownSlides("""
  |
  |### Actors
  |
  |* So far, I've been showing people using the `Actor` logo (stick figure)
  |
  |* Anything outside the "system boundary" (the part you are designing) is usually shown as an actor.
  |
  |  e.g., if you're designing the ATM, that doesn't mean you're designing the bank
  |
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |### An ATM example, showing the bank as an actor
    |
    |""".stripMargin),
    codeAndMermaid("""|sequenceDiagram
                      |  actor Customer
                      |  participant ATM
                      |  actor Bank
                      |
                      |  activate Customer
                      |  Customer ->>+ ATM : insert card
                      |  activate ATM
                      |
                      |  loop until valid pin
                      |    ATM ->>+ Customer : enter PIN
                      |    Customer -->>- ATM : PIN
                      |
                      |    ATM ->>+ ATM : validate PIN
                      |    ATM -->>- ATM : PIN status
                      |  end
                      |
                      |  ATM ->>+ Customer : how can I help?
                      |  Customer -->>- ATM : withdraw $60
                      |
                      |  ATM ->>+ Bank : checkBalance()
                      |  Bank -->>- ATM : balance 
                      |
                      |  alt balance >= $60
                      |    ATM ->>+ Bank : deduct $60
                      |    Bank -->> - ATM : ok
                      |    ATM ->>+ Customer : dispense $60
                      |    Customer -->>- ATM : take money
                      |  else balance < $60
                      |    ATM ->> Customer : Sorry, your balance is too low
                      |  end
                      |
                      |  ATM -->>- Customer : card
                      |  deactivate Customer
                      |
                      |
                      |""".stripMargin)
  ))  
  .markdownSlides("""
    |
    |### More diagram drawing advice
    |
    |I'm not showing you all of the syntax for these diagrams. That's largely because drawing these diagrams is infrequent enough that
    |you'll probably look it up each time you do it.
    |
    |PlantUML syntax is very stable - it's a mature system
    |
    |Mermaid syntax is gradually accruing new features as they make more kinds of diagram and diagram element possible
    |
    |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
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


lazy val stateDiagramDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# State Machines").withClass("center middle")
  .markdownSlides("""
    |
    |### State Machines
    |
    |Some objects should respond differently to a stimulus depending on what state they are in. For example:
    |
    |* Pressing the "minutes" button on the alarm clock, depending on whether you're setting the time or the alarm.
    |
    |* Turning the ignition on your car if the doors are still open
    |
    |It's also common for items in our code to need to respond differently depending on their state. For example, a communication 
    |channel might be pending, connected, completed, or failed.
    |
    |In this deck we'll take a look at state diagrams. Mermaid is a little less detailed in what you can show in a state diagram, compared to PlantUML
    |
    """.stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |### Basic states
    |
    |States are represented by a name in a rounded rectangle 
    |
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                    |  Frozen
                    |  Raw
                    |  Cooked
                    |  Eaten
                    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Transitions
    |
    |Transitions are shown with arrows. It's important to label the events that cause the transitions.
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                    |  Frozen --> Raw : defrost
                    |  Raw --> Cooked : cook
                    |  Raw --> Frozen : freeze
                    |  Cooked --> Eaten : eat
                    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Start and End states
    |
    |Start states are shown as a circle. There is only one.
    |
    |Terminal states are shown as a ringed circle. There can be more than one.
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                      |  Frozen --> Raw : defrost
                      |  Raw --> Cooked : cook
                      |  Raw --> Frozen : freeze
                      |  [*] --> Raw
                      |  Cooked --> Eaten : eat
                      |  Eaten --> [*]
                      |""".stripMargin),
  ))    
  .veautifulSlide(<.div(
    Common.marked("""
    |### Transition Guards
    |
    |Transitions between states can be conditional - that is, they can have a *guard* in square brackets.
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                      |  [*] --> Patrolling
                      |  Patrolling --> Searching : hears player
                      |  Searching --> Patrolling : tick [ player.isDead ]
                      |  Searching --> Chasing : sees player
                      |  Patrolling --> Chasing : sees player
                      |  Chasing --> Patrolling : tick [ player.isDead ]
                      |  Chasing --> Fleeing : hit [ health < 10 ]
                      |  Fleeing --> Patrolling : tick [ distance > 10 ]
                      |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Transition effects
    |
    |We can also show effects we want to take place on a transition by putting them after a `/`
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                    |  [*] --> Patrolling
                    |  Patrolling --> Searching : hears player
                    |  Searching --> Patrolling : tick [ player.isDead ]
                    |  Searching --> Chasing : sees player
                    |  Patrolling --> Chasing : sees player
                    |  Chasing --> Patrolling : tick [ player.isDead ]
                    |  Chasing --> Fleeing : hit [ health < 10 ] / scream()
                    |  Fleeing --> Patrolling : tick [ distance > 10 ]
                    |""".stripMargin),
  ))
  .markdownSlides("""
  |
  |### Entry and Exit Effects
  |
  |In PlantUML, we can also show "entry effects" and "exit effects"
  |
  |![plantUML example](http://www.plantuml.com/plantuml/svg/ZP5DIyOm3CVl_HGvgZ1VhY9Ze7WMF8uTGXTiiUj66i5zUxDHQVSSndFAo_-ND4cx9CZoCmMZZX1yeF0SWezVEV59oDQL-DLXsg9iuHae2g_m03mZtzncx3qGRLYs5POi4quALjAl5rKMZA9vqzyDqBJrAARugfIPjDQfds09k1AR8Xpg5HDllw7FtBrFRuGZ3CV6MbEsIaGNRS-P_snpvSirNoxXl2Zh28CuU86dHnXWFzJ9JgDFWj4Ij3jlEegZVkWV)
  |
  |[PlantUML source for this diagram](//www.plantuml.com/plantuml/uml/ZP5DIyOm3CVl_HGvgZ1VhY9Ze7WMF8uTGXTiiUj66i5zUxDHQVSSndFAo_-ND4cx9CZoCmMZZX1yeF0SWezVEV59oDQL-DLXsg9iuHae2g_m03mZtzncx3qGRLYs5POi4quALjAl5rKMZA9vqzyDqBJrAARugfIPjDQfds09k1AR8Xpg5HDllw7FtBrFRuGZ3CV6MbEsIaGNRS-P_snpvSirNoxXl2Zh28CuU86dHnXWFzJ9JgDFWj4Ij3jlEegZVkWV)
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |### Entry and Exit effects
    |
    |We sort of do this in Mermaid, by putting some HTML into the state label
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                    |  [*] --> Patrolling
                    |  Patrolling --> Searching : hears player
                    |  Searching --> Patrolling : tick [ player.isDead ]
                    |  Searching --> Chasing : sees player
                    |  Patrolling --> Chasing : sees player
                    |  Chasing --> Patrolling : tick [ player.isDead ]
                    |  Chasing --> Fleeing : hit [ health < 10 ] 
                    |  Chasing : Chasing <hr /> entry / roar()
                    |  Fleeing --> Patrolling : tick [ distance > 10 ]
                    |  Fleeing : Fleeing <hr /> entry / scream() <br /> exit / pant()
                    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    <.h3("Compound States"),
    <.div(^.attr("style") := "float: right",
      MermaidDiagram("""|stateDiagram-v2
                |  [*] --> Unafraid 
                |  state Unafraid {
                |    [*] --> Patrolling
                |    Patrolling --> Searching : hears player
                |    Searching --> Patrolling : tick [ player.isDead ]
                |    Searching --> Chasing : sees player
                |    Patrolling --> Chasing : sees player
                |    Chasing --> Patrolling : tick [ player.isDead ]
                |    Chasing : Chasing <hr /> entry / roar()
                |  }
                |  Unafraid --> Fleeing : hit [ health < 10 ] 
                |  Fleeing --> Unafraid : tick [ distance > 10 ]
                |  Fleeing : Fleeing <hr /> entry / scream() <br /> exit / pant()
                |""".stripMargin)
    ),
    Common.marked("""
    |It could be repetetive to show the same transitions from many states. For example, perhaps the game character should also flee if it is hit in the patrolling or searching states (not just the chasing state).
    |
    |We can show this by grouping the sources of those transitions into a compound state. Transitions from a compound state act as if they were defined from each contained state.
    |
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |### Compound states
    |
    |""".stripMargin),
    codeAndMermaid("""|stateDiagram-v2
                    |  [*] --> Unafraid 
                    |  state Unafraid {
                    |    [*] --> Patrolling
                    |    Patrolling --> Searching : hears player
                    |    Searching --> Patrolling : tick [ player.isDead ]
                    |    Searching --> Chasing : sees player
                    |    Patrolling --> Chasing : sees player
                    |    Chasing --> Patrolling : tick [ player.isDead ]
                    |    Chasing : Chasing <hr /> entry / roar()
                    |  }
                    |  Unafraid --> Fleeing : hit [ health < 10 ] 
                    |  Fleeing --> Unafraid : tick [ distance > 10 ]
                    |  Fleeing : Fleeing <hr /> entry / scream() <br /> exit / pant()
                    |""".stripMargin),
  ))
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
package supercollaborative.containers

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("vmsAndContainers", intro),
  "Virtual Machines" -> site.add("vm",
    Alternative("Slides", Deck(() => vmDeck))
  ),
  "Containers" -> site.add("containers",
    Alternative("Slides", Deck(() => containersDeck))
  ),
  "Developer Containers" -> site.add("devcontainers",
    Alternative("Slides", Deck(() => devcontainersDeck))    
  ),
)

val intro = <.div(
  Common.chapterHeading(2, "Virtual Machines and Containers", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
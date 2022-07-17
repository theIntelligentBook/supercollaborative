package supercollaborative.buildsystems

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import templates.markdownDeck
import templates.given

import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("vmsAndContainers", intro),
  "Build systems" -> site.add("buildsystems", 
    Alternative("Slide deck", Deck(() => markdownDeck("Build Systems", "markdown/buildsystems.md")))
  ),
  "Testing" -> site.add("automatedtests", 
    Alternative("Slide deck", Deck(() => markdownDeck("Testing", "markdown/automatedtests.md")))
  ),
)

val intro = <.div(
  Common.chapterHeading(3, "Build Systems and Testing", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
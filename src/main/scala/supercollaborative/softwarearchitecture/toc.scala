package supercollaborative.softwarearchitecture

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import templates.markdownDeck
import templates.given

import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("softwareArchitecture", intro),
  "Software Architecture" -> site.add("softwarearchitecture", 
    Alternative("Slide deck", Deck(() => markdownDeck("Software Architecture", "markdown/softwarearchitecture.md")))
  ),
  "Writing n-Tier apps" -> site.add("ntier", 
    Alternative("Slide deck", Deck(() => markdownDeck("Writing n-Tier apps", "markdown/ntier.md")))
  ),
)

val intro = <.div(
  Common.chapterHeading(5, "Software Architecture", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
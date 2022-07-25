package supercollaborative.continuousdevelopment

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import templates.markdownDeck
import templates.given

import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("continuousDevelopment", intro),
  "Coding for Testability" -> site.add("testability", 
    Alternative("Slide deck", Deck(() => markdownDeck("Coding for Testability", "markdown/moretesting.md")))
  ),
)

val intro = <.div(
  Common.chapterHeading(4, "Continuous Development", "images/xp.png"),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
package supercollaborative.implications

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import templates.markdownDeck
import templates.given
import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("implications", intro),
  "Dependencies, Communities, and Licensing" -> site.add("licensing",
    Alternative("Slides", Deck(() =>markdownDeck("Dependencies, Communities, and Licensing", "markdown/licensing.md")))
  ),
  "Security" -> site.add("oss-security",
    Alternative("Slides", Deck(() =>markdownDeck("Security", "markdown/security.md")))
  ),
)

val intro = <.div(
  Common.chapterHeading(9, "Social Implications", ""),
  Common.marked("""
  |In this section we consider some of the social, ethical, and security aspects that come from the fact that
  |so much of our code depends on things that other people have developed.
  |""".stripMargin)
)
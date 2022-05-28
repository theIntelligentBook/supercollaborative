package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("versioncontrol", intro),
  "Version Control" -> site.add("vcs",
    Alternative("Slides", Deck(() => vcDeck))
  )

)

val intro = <.div(
  Common.chapterHeading(1, "Version Control", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
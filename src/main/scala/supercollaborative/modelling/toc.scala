package supercollaborative.modelling

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("modelling", intro),
  "Class Diagrams" -> site.add("class-diagrams",
    Alternative("Slides", Deck(() => classDiagramDeck))
  ),
  "Sequence Diagrams" -> site.add("sequence-diagrams",
    Alternative("Slides", Deck(() => seqDiagramDeck))
  )
)

val intro = <.div(
  Common.chapterHeading(6, "Diagrams & Modelling", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
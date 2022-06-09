package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._
import scalajs.js
import scala.util.Random


val branchDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Branches").withClass("center middle")
  .veautifulSlide(<.div(
    Common.marked("""
    |## Tags
    |
    |Suppose we have the 
    |
    |""".stripMargin),
    DAGAndTree(Seq(gitExample.head), 400)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Tags
    |
    |Suppose we have the 
    |
    |""".stripMargin),
    DAGAndTree(
      Git.init
        .commit("Will", "A", 1)
        .commit("Will", "B", 2)
        .branch("feature")
        .switch("feature").commit("Will", "C", 3)
        .switch("main").commit("Will", "D", 4)
        .switch("feature").commit("Will", "E", 5)
        .switch("main").commit("Will", "F", 6)
        .refs.toSeq, 
      400
    )
  ))
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
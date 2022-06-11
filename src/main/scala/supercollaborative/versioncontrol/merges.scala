package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

val mergesDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Merges").withClass("center middle")
  .markdownSlides("""
  |## Branches in practice
  |
  |When we introduced branches, we motivated it with the idea of maintaining old releases. But that's
  |not the only reason to use branches.
  |
  |Usually, you are not the only programmer on your product. You are working in parallel with your
  |colleagues. 
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
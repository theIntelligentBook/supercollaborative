package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

val diffsDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Diffs").withClass("center middle")
  .markdownSlides("""
  |## What's changed?
  |
  |One of the most common questions you might find yourself asking is:
  |
  |> Someone's changed something. What did they do?
  |
  |Version control holds snapshots of our code in different commits.
  |Git only knows that they are files, not the operations you performed when editing them.
  |
  |So, we need to be able to work out *the difference between two text files*.
  |
  |We're computer scientists, so let's figure out how that might work
  |
  |---
  |
  |## The difference between two strings
  |
  |Suppose we use, as an example, the "mondegreen" line from the Earl of Moray
  |
  |> <pre>AND LAID HIM ON THE GREEN</pre>
  |
  |vs
  |
  |> <pre>AND LADY MONDEGREEN</pre>
  |
  |---
  |
  |## The difference between two strings
  |
  |But, just so we can see them if we colour them, let's change the spaces for underscores
  |
  |> <pre>AND_LAID_HIM_ON_THE_GREEN</pre>
  |
  |vs
  |
  |> <pre>AND_LADY_MONDEGREEN</pre>
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._
import scalajs.js

case class BigDiffViewer(initialA:String, initialB:String, byLine:Boolean = true) extends VHtmlNode {
  
  var editorA:Option[js.Dynamic] = None
  var editorB:Option[js.Dynamic] = None

  def update() = if byLine then updateByLine() else updateByChar()

  def updateByLine() = {
    
    for 
      a <- editorA
      b <- editorB
    do
      val ta = a.textContent.asInstanceOf[String].split("\n")
      val tb = b.textContent.asInstanceOf[String].split("\n")

      val lcs = longestCommonSubsequence(ta, tb)

      val c = compare(ta.toSeq, tb.toSeq)

      a.innerHTML = (c.collect {
        case CompareResult.Both(c) => s"<span class='both'>$c</span>\n"
        case CompareResult.Left(c) => s"<span class='left'>$c</span>\n"
      }).mkString

      b.innerHTML = (c.collect {
        case CompareResult.Both(c) => s"<span class='both'>$c</span>\n"
        case CompareResult.Right(c) => s"<span class='right'>$c</span>\n"
      }).mkString
  }

  def updateByChar() = {
    for 
      a <- editorA
      b <- editorB
    do
      val ta = a.textContent.asInstanceOf[String]
      val tb = b.textContent.asInstanceOf[String]

      val lcs = longestCommonSubsequence(ta, tb)

      val c = compare(ta.toSeq, tb.toSeq)

      a.innerHTML = (c.collect {
        case CompareResult.Both(c) => s"<span class='both'>$c</span>"
        case CompareResult.Left(c) => s"<span class='left'>$c</span>"
      }).mkString

      b.innerHTML = (c.collect {
        case CompareResult.Both(c) => s"<span class='both'>$c</span>"
        case CompareResult.Right(c) => s"<span class='right'>$c</span>"
      }).mkString
  }

  val divA = <.div(^.cls := "boxA " +CodeStyle.styling.className,
    initialA,
  )

  val divB = <.div(^.cls := "boxB " + CodeStyle.styling.className,
    initialB,
  )

  val structure = <.div(^.cls := (if byLine then "horizontal " else "vertical ") + CodeStyle.lcsViewer.className, 
    divA, divB
  )

  export structure.attach
  export structure.detach
  export structure.domNode


  override def afterAttach():Unit = {
    for 
      a <- divA.domNode 
      b <- divB.domNode
    do
      val jarA = CodeJar(a) { editor => 
        editorA = Some(editor)
        update()
      }
    
      val jarB = CodeJar(b) { editor => 
        editorB = Some(editor)
        update()        
      }
  }

}


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
  .veautifulSlide(<.div(
    Common.marked("""
    |## Longest Common Subsequence
    |
    |If we go through both strings, we can identify the *longest common subsequence* between theses
    |strings. i.e., the same letters in the same order.
    |
    |In the widget below, we've coloured the text that is unique to the top string in red, and the
    |text that is unique to the bottom string in green. The longest common subsequence is in black.
    |
    |(The text is editable, so you can see how the longest common subsequence updates)
    |
    |""".stripMargin),
    BigDiffViewer("AND_LAID_HIM_ON_THE_GREEN", "AND_LADY_MONDEGREEN", byLine=false),
    Common.marked("""
    |This gives us a way of describing how to transform from one string to the other. To go from the
    |top string to the bottom string:
    |
    |* remove all the red letters
    |* add all the green letters
    |
    |It doesn't necessarily describe what you *did*, but is an algorithmically calculated difference between the strings.
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Diff 
    |
    |In practice, it wouldn't be useful to do a letter-by-letter diff on a text file. 
    |
    |A line-by-line diff is more useful: I was changing a line of the poem.
    |
    |""".stripMargin),
    BigDiffViewer(moray, mondegreen),
    Common.marked("""
    |It doesn't necessarily describe what you *did*, but is an algorithmically calculated difference between the strings.
    |""".stripMargin),

  ))
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
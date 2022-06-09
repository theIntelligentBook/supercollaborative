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

case class BigDiffViewer(initialA:String, initialB:String, byLine:Boolean = true, showGrid:Option[(Int, Int)] = None) extends VHtmlNode {

  object memoBox extends VHtmlNode {

    val node = <.div()

    export node.domNode
    export node.attach
    export node.detach

    def updateGraph[T](a:Seq[T], b:Seq[T], ops:Seq[CompareResult[T]]) = 
      node.makeItSo(<.div(for (w, h) <- showGrid yield memoBox(a, b, ops)(w, h)))

    def memoBox[T](a:Seq[T], b:Seq[T], ops:Seq[CompareResult[T]])(h:Int, w:Int) = {     
      def cx(x:Int) = 5 + w * x
      def cy(y:Int) = 5 + h * y

      def line(x1:Int, y1:Int, x2:Int, y2:Int, cls:String) = SVG.line(
        ^.cls := cls, ^.attr("x1") := cx(x1), ^.attr("x2") := cx(x2), ^.attr("y1") := cy(y1), ^.attr("y2") := cy(y2), 
      )

      <.svg(
        ^.attr("width") := 10 + (a.length * w), ^.attr("height") := 10 + (b.length * w),

        for x <- 0 to a.length yield line(x, 0, x, b.length, "path"),
        for y <- 0 to b.length yield line(0, y, a.length, y, "path"),

        for 
          x <- a.indices
          y <- b.indices if (a(x) == b(y))
        yield line(x, y, x+1, y+1, "path"),

        for { x <- 0 to a.length; y <- 0 to b.length } yield SVG.circle(
          ^.attr("cx") := cx(x), ^.attr("cy") := cy(y), ^.attr("r") := 3
        ),

        {
          var x = 0
          var y = 0

          ops.map {
            case CompareResult.Left(_) => 
              val l = line(x, y, x+1, y, "path left")
              x = x + 1
              l
            case CompareResult.Right(_) => 
              val l = line(x, y, x, y+1, "path right")
              y = y + 1
              l
            case CompareResult.Both(_) => 
              val l = line(x, y, x+1, y+1, "path both")
              x = x + 1
              y = y + 1
              l
          }
        }



      )
    }
  }
  
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

      memoBox.updateGraph(ta, tb, c)

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

      memoBox.updateGraph(ta, tb, c)

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
    divA, divB, 
    for _ <- showGrid yield memoBox
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

def randomBytes(n:Int):String = 
  (for _ <- 0 until n yield Random.nextInt(255).toHexString).mkString

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
    |## LCS algorithm
    |
    |There are a few algorithms to produce a Longest Common Subsequence. 
    |
    |One is to imagine it like trying to find your way through a grid-like maze. The lines in the grid correspond to the letters in
    |the words. If the letters match, there's a diagonal path. If not, there isn't.
    |
    |Once you've found the shortest path, the diagonal paths you used are the longest common subsequence. 
    |
    |""".stripMargin),
    BigDiffViewer("AND_LAID_HIM_ON_THE_GREEN", "AND_LADY_MONDEGREEN", byLine=false, Some((20, 20)))
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
  .veautifulSlide(<.div(
    Common.marked("""
    |## That algorithm again
    |
    |Done line-by-line, the algorithm for finding the LCS can be the same, but each line in the grid now corresponds to
    |a line in one of the text files
    |
    |""".stripMargin),
    BigDiffViewer(moray, mondegreen, showGrid = Some((25, 25)),
  )))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A problem with binary files...
    |
    |This all works very neatly for *text* files.
    |
    |Unfortunately, binary files (e.g. image files) just end up being completely different from each other.
    |
    |""".stripMargin),
    BigDiffViewer(
      (for l <- 0 until 4 yield randomBytes(40)).mkString, 
      (for l <- 0 until 4 yield randomBytes(40)).mkString
    ), 
    <.p("As we'll see later, this will cause us a few issues with keeping large binary files under version control")
  ))
  .markdownSlides("""
  |## The story so far
  |
  |* Git helps us keep snapshots of the source files in our working tree
  |
  |* As most of these are text files, we can calculate the `diff` between files in different snapshots
  |
  |* If we know the difference between the files, we could also generate a `patch` (a sequence of delete, keep, and add operations) that
  |  would transform a file from one version to another.
  |
  |* This helps git to describe what has changed between versions. 
  |
  |* But it only really works for text files. For binary files, we can only tell that the file has changed.
  |
  |---
  |
  |## `diff` at the command-line
  |
  |`diff` is a common unix utility that will show you the difference between two files, using an algorithm a bit more efficient than
  |the one we used.
  |
  |```
  |
  |
  |```
  |
  |---
  |
  |## `git diff`
  |
  |`git diff` is a comman that will show you the difference between files in a git repository
  |
  |* just `git diff` will show you differences between your working tree and git's `index`. 
  |  (i.e. anything you haven't done `git add` to yet)
  |
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
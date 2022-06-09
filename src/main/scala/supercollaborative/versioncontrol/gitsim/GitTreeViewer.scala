package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._
import supercollaborative.templates._

import scalajs.js.Date


def HorizontalBranch(commits:Map[Commit, (Int, Int)], r:Int, selected:Option[Commit])(f: Commit => Unit) = {

  val w = 10 * r
  val h = w
  val maxH = 1 + commits.values.maxBy(_._2)._2
  val pxWidth = commits.size * r * 10 + 100
  val pxHeight = maxH * r * 10 + 100


  def cx(x:Int) = pxWidth - (w + w * x)
  def cy(y:Int) = h + h * y

  def commitCircle(x:Int, y:Int)  = SVG.circle(^.attr("cx") := cx(x), ^.attr("cy") := cy(y), ^.attr("r") := r)

  def smallMsg(c:Commit) = {
    val (x, y) = commits(c)
    SVG.text(^.cls := "commit-label", ^.attr("x") := cx(x), ^.attr("y") := cy(y) - 20, c.comment.split("\n").headOption.getOrElse("empty msg"))
  }

  def commitArrow(x1:Int, y1:Int, x2:Int, y2:Int) = {
    val xx1 = cx(x1) - r
    val xx2 = cx(x2) + r
    val yy1 = cy(y1)
    val yy2 = cy(y2)

    SVG.path(^.cls := "parent-arrow", ^.attr("d") := s"M $xx1 $yy1 C ${0.5 * xx1 + 0.5 * xx2} $yy1, ${0.5 * xx1 + 0.5 * xx2} $yy2, $xx2 $yy2 l 10 10 l -10 -10 l 10 -10")
  }


  def commitLabel(c:Commit) = {
    val cc = commits(c)
    SVG.g(
      (for p <- c.parents if commits.contains(p) yield 
        val pp = commits(p)
        commitArrow(cc._1, cc._2, pp._1, pp._2)
      ),
      commitCircle(cc._1, cc._2),
      smallMsg(c),
    )
  }


  <.svg(^.attr("width") := pxWidth, ^.attr("height") := pxHeight, ^.attr("viewBox") := s"0 0 0 $pxWidth $pxHeight",
    for (c) <- commits.keys.toSeq yield
      commitLabel(c)
  )

}

case class DAGAndTree(refs:Seq[Ref], height: Int) extends VHtmlComponent {

  def render = {
    val commits = layoutRefs(refs)

    <.div(^.cls := CodeStyle.commitDAG.className, 
      HorizontalBranch(commits, 10, None) { _ => () }
    )
  }

}

case class BranchHistoryAndTree(branch:Ref.Branch, height: Int) extends VHtmlComponent {

  var selected = branch.commit

  val laidOut = temporalTopological(Seq(branch.commit))

  def select(c:Commit):Unit = 
    selected = c
    rerender()

  def render = <.div(^.cls := CodeStyle.history.className, 
    <.div(^.cls := "history",
      for c <- laidOut yield <.button(
        ^.cls := (if c == selected then "history-row selected" else "history-row"), 
        ^.onClick --> select(c),
        <.span(^.cls := "hash", c.hash),
        <.span(^.cls := "author", c.author),
        <.span(^.cls := "comment", c.comment),
        <.span(^.cls := "date", (new Date(c.time)).toLocaleString),
      )
    ),
    MorphingTreeViewer()(selected.tree, height)
  )

}
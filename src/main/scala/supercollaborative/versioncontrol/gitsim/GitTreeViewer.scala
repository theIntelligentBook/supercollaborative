package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._
import supercollaborative.templates._

import scalajs.js.Date


case class HorizontalBranchConfig(
  r: Int, // radius of a commit circle
  hGap: Int, // gap between commits,
  vGap: Int, // 
  label: (Commit, (Int, Int)) => VHtmlNode,
  commitClass: (Commit) => String,
  lineClass: (Commit, Commit) => String,
  onClick: (Commit) => Unit
)

// A layout for graphs that have a single letter in the commit comment
val hbLetterInComment = HorizontalBranchConfig(
  10, 100, 100,
  label = { case (c, (x, y)) => 
    SVG.text(^.cls := "commit-label", ^.attr("x") := x, ^.attr("y") := y - 20, c.comment.split("\n").headOption.getOrElse("empty msg"))
  },
  commitClass = (c) => "",
  lineClass = (c, p) => "",
  onClick = (c) => ()
)

// A layout for graphs that have a single letter in the commit comment
val hbHashOnly = HorizontalBranchConfig(
  10, 150, 150,
  label = { case (c, (x, y)) => 
    SVG.text(^.cls := "commit-label hash-only", ^.attr("x") := x, ^.attr("y") := y - 20, c.hash)
  },
  commitClass = (c) => "",
  lineClass = (c, p) => "",
  onClick = (c) => ()
)

// A layout for graphs that have a single letter in the commit comment
val hbCompactLabel = HorizontalBranchConfig(
  15, 300, 250,
  label = { case (c, (x, y)) => 
    SVG.foreignObject(^.attr("x") := x - 125, ^.attr("y") := y - 225, ^.attr("width") := 250, ^.attr("height") := 200,
      <.div(^.cls := "compact-commit-label-box",
        <.div(^.cls := "hash", c.hash),
        <.div(^.cls := "author", c.author),
        <.div(^.cls := "comment", c.comment.split("\n").headOption.getOrElse("empty msg")),
        <.div(^.cls := "time", (new Date(c.time)).toLocaleString),
      )
    )
  },
  commitClass = (c) => "",
  lineClass = (c, p) => "",
  onClick = (c) => ()
)


def HorizontalBranch(commits:Map[Commit, (Int, Int)], refs:Seq[Ref], config:HorizontalBranchConfig) = {

  def r = config.r
  def w = config.hGap
  def h = config.vGap
  val maxH = 1 + commits.values.maxBy(_._2)._2
  val pxWidth = commits.size * w + w
  val pxHeight = maxH * h + h


  def cx(x:Int) = w + w * x
  def cy(y:Int) = h + h * y

  def commitCircle(c:Commit, x:Int, y:Int)  = SVG.circle(
    ^.cls := "commit " + config.commitClass(c),
    ^.onClick --> config.onClick(c),
    ^.attr("cx") := cx(x), ^.attr("cy") := cy(y), ^.attr("r") := r
  )

  def commitArrow(c:Commit, p:Commit, x1:Int, y1:Int, x2:Int, y2:Int) = {
    val xx1 = cx(x1) - r
    val xx2 = cx(x2) + r
    val yy1 = cy(y1)
    val yy2 = cy(y2)

    SVG.path(
      ^.cls := "parent-arrow " + config.lineClass(c, p), 
      ^.attr("d") := s"M $xx1 $yy1 L ${xx2 + 3 * w/4} $yy1 C ${xx2 + w/2} $yy1, ${xx2 + w/2} $yy2, ${xx2 + w/4} $yy2 l ${-w/4} 0 l 10 10 l -10 -10 l 10 -10")
  }


  def commitLabel(c:Commit) = {
    val (x, y) = commits(c)
    val xx = cx(x)
    val yy = cy(y)

    val reflabels = refLabels(c, (xx, yy)) // Tag label takes pixel coordinates

    SVG.g(
      (for p <- c.parents if commits.contains(p) yield 
        val (xx, yy) = commits(p)
        commitArrow(c, p, x, y, xx, yy)
      ),
      refLine(reflabels, (xx, yy), r),
      commitCircle(c, x, y),
      reflabels,
      config.label(c, (xx, yy)), // Config label takes pixel coordinates
    )
  }

  def tags(c:Commit):Seq[Ref.Tag] = refs.collect { case t:Ref.Tag if t.commit == c => t }
  def branches(c:Commit):Seq[Ref.Branch] = refs.collect { case t:Ref.Branch if t.commit == c => t }
  def named(c:Commit):Seq[Ref.NamedDetached] = refs.collect { case t:Ref.NamedDetached if t.commit == c => t }

  def refLine[T](labels:Seq[T], p:(Int, Int), r:Int) = 
    val lineHeight = 25
    if labels.nonEmpty then 
      val (xx, yy) = p
      Some(SVG.line(^.cls := "ref-line", ^.attr("x1") := xx, ^.attr("y1") := yy + r, ^.attr("x2") := xx, ^.attr("y2") := yy + lineHeight + lineHeight * labels.length ))
    else None

  def refLabels(c:Commit, p:(Int, Int)) = 
    val lineHeight = 25
    val (xx, yy) = p
    (tags(c) ++ branches(c) ++ named(c)).zipWithIndex collect { 
      case (Ref.Tag(n, _), i) => SVG.text(^.cls := "tag-label", ^.attr("x") := xx + 10, ^.attr("y") := (yy + lineHeight + (lineHeight * i)), "tag: " + n)
      case (Ref.Branch(n, _), i) => SVG.text(^.cls := "branch-label", ^.attr("x") := xx + 10, ^.attr("y") := (yy + lineHeight + (lineHeight * i)), "branch: " + n)
      case (Ref.NamedDetached(n, _), i) => SVG.text(^.cls := "named-detached-label", ^.attr("x") := xx + 10, ^.attr("y") := (yy + lineHeight + (lineHeight * i)), n)
    }

  <.svg(^.attr("width") := pxWidth, ^.attr("height") := pxHeight, ^.attr("viewBox") := s"0 0 $pxWidth $pxHeight",
    for (c) <- commits.keys.toSeq yield
      commitLabel(c)
  )

}

/** Displays a git graph hozontally */
case class HDAGOnly(refs:Seq[Ref], config:HorizontalBranchConfig) extends VHtmlComponent {

  lazy val commits = layoutRefs(refs)

  def render = {
    <.div(^.cls := CodeStyle.horizontalCommitDAG.className, 
      HorizontalBranch(commits, refs, config)
    )
  }

}

/** Displays a gir graph horizontally, allowing elements to be selected */
case class SelectableHDAG(refs:Seq[Ref]) extends VHtmlComponent {

  lazy val commits = layoutRefs(refs)
  var selected:Option[Commit] = None

  def select(c:Commit):Unit = 
    selected = if selected.contains(c) then None else Some(c)
    rerender()

  def render = {
    val ancestors = selected.toSet.flatMap(_.ancestors) ++ selected

    <.div(^.cls := CodeStyle.horizontalCommitDAG.className, 
      HorizontalBranch(commits, refs, HorizontalBranchConfig(
        10, 150, 150,
        label = { case (c, (x, y)) => 
          SVG.text(^.cls := "commit-label hash-only", ^.attr("x") := x, ^.attr("y") := y - 20, c.hash)
        },
        commitClass = (c) => if ancestors.contains(c) then "selected" else "",
        lineClass = (c, p) => if ancestors.contains(c) then "selected" else "",
        onClick = (c) => select(c)
      ))
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
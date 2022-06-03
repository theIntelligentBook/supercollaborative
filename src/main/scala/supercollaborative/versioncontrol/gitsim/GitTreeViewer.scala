package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._
import supercollaborative.templates._


def HorizontalBranch(commits:Map[Commit, (Int, Int)], selected:Option[Commit])(f: Commit => Unit) = {
  <.svg(
    for c <- commits.values.toSeq yield
      SVG.circle()
  )


}

case class BranchHistoryAndTree(branch:Ref.Branch, height: Int) extends VHtmlComponent {

  var selected = branch.commit

  val laidOut = layoutRefs(Seq(branch)).filterNot({ (c, _) => c == Commit.Empty }).reverse

  def select(c:Commit):Unit = 
    selected = c
    rerender()

  def render = <.div(^.cls := CodeStyle.history.className, 
    <.div(^.cls := "history",
      for (c, i) <- laidOut yield <.button(
        ^.cls := (if c == selected then "history-row selected" else "history-row"), 
        ^.onClick --> select(c),
        <.span(^.cls := "hash", c.hash),
        <.span(^.cls := "author", c.author),
        <.span(^.cls := "comment", c.comment),
        <.span(^.cls := "date", c.time.toString),
      )
    ),
    MorphingTreeViewer()(selected.tree, height)
  )

}
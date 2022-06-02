package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._
import supercollaborative.templates._


def HorizontalBranch(commits:Map[Commit, (Int, Int)], selected:Option[Commit])(f: Commit => Unit) = {
  <.svg(
    for c <- commits.values.toSeq yield
      SVG.circle()
  )


}

case class HorizontalBranchAndTree(branch:Ref.Branch, height: Int) extends VHtmlComponent {

  var selected = branch.commit

  val laidOut = layoutRefs(Seq(branch)).filterNot { (c, _) => c == Commit.Empty }

  def select(c:Commit):Unit = 
    selected = c
    rerender()

  def render = <.div(
    for (c, i) <- laidOut yield <.div(
      <.button(c.hash, ^.onClick --> select(c)),
      <.span(c.comment)  
    ),
    TreeViewer(selected.tree, height)
  )

}
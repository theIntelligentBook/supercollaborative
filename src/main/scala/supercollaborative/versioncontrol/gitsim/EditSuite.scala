package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.*
import scalajs.js.Date


enum EditSuiteView:
  case Tree(path:Seq[String])
  case Graph
  case History

case class EditSuiteConfig(
  view: EditSuiteView,
  git: Option[Git] = None,
  height: Int = 600
)

case class EditSuite(tree:MutableFile.Tree)(config: EditSuiteConfig) extends VHtmlComponent with Morphing(config) {

  override val morpher = createMorpher(this)

  def toggle = prop.view match {
    case EditSuiteView.Tree(_) => 
      <.span(
        <.button("Tree", ^.attr("disabled") := "disabled", ^.cls := "active"),
        <.button("Graph", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.Graph))),
        <.button("History", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.History)))
      )
    case EditSuiteView.Graph => 
      <.span(
        <.button("Tree", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.Tree(Seq.empty)))),
        <.button("Graph", ^.attr("disabled") := "disabled", ^.cls := "active"),
        <.button("History", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.History)))
      )
    case EditSuiteView.History => 
      <.span(
        <.button("Tree", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.Tree(Seq.empty)))),
        <.button("Graph", ^.onClick --> updateProp(prop.copy(view = EditSuiteView.Graph))),
        <.button("History", ^.attr("disabled") := "disabled", ^.cls := "active")
      )
  }

  def branchIndicator = for g <- prop.git yield g.head match {
    case Ref.Branch(n, _) => <.span(^.cls := "branch-indicator", s"On branch $n. ")
    case _ => <.span(^.cls := "branch-indicator", s"Detached mode, commit ${g.head.commit.hash}. ")
  } 

  def uncommitedIndicator = for g <- prop.git if g.uncommittedChanges yield
    <.span(^.cls := "uncommitted-message", "Uncommitted changes in index.")

  def treeView = MutableTreeViewer()(tree, config.height, 
    highlight = { (path, text) => 
      for 
        g <- prop.git
        File.TextFile(leftText) <- g.index.find(path.toList)
      yield
        val diffs = compare(leftText.split("\n"), text.split("\n"))

        (diffs.collect {
          case CompareResult.Left(c) => s"<span class='deleted' />"
          case CompareResult.Both(c) => s"<span class='both' >$c\n</span>"
          case CompareResult.Right(c) => s"<span class='added' >$c\n</span>"
        }).mkString
    },

    entryClass = { (path, f) =>
      for 
        g <- prop.git 
      yield g.index.find(path.toList) match {
        case None => "untracked"
        case Some(ff) if f.toImmutable == ff => "unmodified"
        case _ => "modified"
      }
    })

  def hvscrollBox = <.div(^.attr("style") := "max-width: 100%; max-height: 100%; overflow: auto")
  
  def render = {

    val config = prop

    <.div(
      config.git match {
        case None => 
          <.div( 
            <.div(^.cls := CodeStyle.gitToolbar.className, "Not a git repository"), 
            treeView
          )
        case Some(g) => 
          val laidOut = temporalTopological(Seq(g.head.commit))

          <.div(
            <.div(^.cls := CodeStyle.gitToolbar.className, 
              toggle, branchIndicator, uncommitedIndicator
            ),
            config.view match {
              case EditSuiteView.Graph => hvscrollBox(
                SelectableHDAG(g.refs.toSeq :+ g.headAsDetached)
              )
              case EditSuiteView.History => <.div(
                HistoryAndTree(g, config.height, 250)
              )
              case EditSuiteView.Tree(path) => <.div(
                treeView
              )
            }
          )

      }
    )
  }

}
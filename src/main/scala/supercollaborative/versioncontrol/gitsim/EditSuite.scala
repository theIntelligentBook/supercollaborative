package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.*


enum EditSuiteView:
  case Tree(path:String)
  case Graph

case class EditSuiteConfig(
  view: EditSuiteView,
  git: Option[Git] = None,
  height: Int = 600
)

case class EditSuite(tree:MutableFile.Tree)(config: EditSuiteConfig) extends VHtmlComponent with Morphing(config) {

  override val morpher = createMorpher(this)

  def render = {
    val config = prop

    <.div(
      config.view match {
        case EditSuiteView.Graph => <.div()
        case EditSuiteView.Tree(path) => MutableTreeViewer()(tree, config.height)
      }
    )
  }

}
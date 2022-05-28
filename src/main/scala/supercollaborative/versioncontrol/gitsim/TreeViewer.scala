package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._



case class TreeViewer(tree:MutableFile.Tree) extends VHtmlComponent {

  object fileSelector extends VHtmlComponent {
    var selected:Option[File] = None

    def component(depth:Int, name:String, f:MutableFile) = f match {
      case f:MutableFile.TextFile => <.div(name)
      case _ => <.div()
    }
    
    def render = <.div(
      <.div(
        for (n, f) <- tree.files.toSeq.sortBy(_._1) yield component(0, n, f)
      )
    )
  }

  def render = <.div(
    fileSelector, 
    <.pre()
  )







}
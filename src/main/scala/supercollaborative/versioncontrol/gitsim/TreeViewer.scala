package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html._
import supercollaborative.templates._

case class TreeViewer(tree:File.Tree, height: Int) extends VHtmlComponent {

  object fileSelector extends VHtmlComponent {

    val pairs = tree.files.toSeq.sortBy(_._1)
    var selected:Option[(String, File)] = pairs.headOption

    def select(pair:(String, File)):Unit = 
      selected = Some(pair)
      TreeViewer.this.rerender()

    def selector(depth:Int, name:String, f:File) = f match {
      case f:File.TextFile => 
        if selected.contains((name, f)) then <.button(^.cls := "fileSelector selected", name) else <.button(^.cls := "fileSelector", ^.on("click") --> select((name, f)), name)
      case _ => <.div()
    }

    def render = 
      <.div(^.cls := "fileList",
        for (n, f) <- pairs yield selector(0, n, f)
      )

  }

  def viewer(name:String, f:File) = f match {
    case f:File.TextFile => 
      <.pre(^.cls := "fileViewer readOnly", f.text)
    case _ => <.div()
  }

  def render = <.div(^.cls := CodeStyle.codeCard.className,
    <.div(^.cls := CodeStyle.selectorAndViewer.className, ^.attr("style") := s"height: ${height}px",
      fileSelector, 
      for (n, f) <- fileSelector.selected yield viewer(n, f)
    )
  )







}
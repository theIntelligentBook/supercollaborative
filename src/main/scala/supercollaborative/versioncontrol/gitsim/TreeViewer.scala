package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.Morphing
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



case class MorphingTreeSelector(path:Seq[String])(tree:File.Tree, selected:Seq[String], onSelect: Seq[String] => Unit) extends VHtmlComponent with Morphing(tree, selected, onSelect) {

  var expanded = true
  val morpher = createMorpher(this)

  def toggle():Unit =
    expanded = !expanded
    rerender()

  def render = 
    val (tree, selected, onSelect) = prop
    if expanded then
      <.div(^.cls := "expander expanded",
        <.button(^.cls := "fileSelector expander expanded", ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.lastOption.getOrElse(""), ^.onClick --> toggle()),
        for n <- tree.files.keys.toSeq.sorted yield 
          morphingFileSelector(path :+ n, selected, tree.files(n))(onSelect)
      )
    else
      <.div(^.cls := "expander collapsed",
        <.button(^.cls := "fileSelector expander collapsed",  ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.lastOption.getOrElse(""), ^.onClick --> toggle())
      )

}

def morphingFileSelector(path:Seq[String], selected:Seq[String], f:File)(onSelect: Seq[String] => Unit) = f match {
  case t:File.Tree => MorphingTreeSelector(path)(t, selected, onSelect)
  case _ => <.button(
    ^.cls := (if path == selected then "fileSelector textfile selected" else "fileSelector textfile"),  
    ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.last, ^.onClick --> onSelect(path)
  )
}


case class MorphingTreeViewer()(tree:File.Tree, height: Int) extends VHtmlComponent with Morphing(tree) {

  val morpher = createMorpher(this)

  var selected:Seq[String] = Seq.empty

  def select(seq:Seq[String]):Unit = 
    selected = seq
    rerender()

  def viewer(name:String, f:File) = f match {
    case f:File.TextFile => 
      <.pre(^.cls := "fileViewer readOnly", f.text)
    case _ => <.div()
  }

  def render = <.div(^.cls := CodeStyle.codeCard.className,
    <.div(^.cls := CodeStyle.selectorAndViewer.className, ^.attr("style") := s"height: ${height}px",
      <.div(^.cls := "fileList", 
        MorphingTreeSelector(Seq.empty)(tree, selected, select(_)),
      ),
      for f <- prop.find(selected.toList) yield viewer("", f)
    )
  )


}

package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html._
import supercollaborative.templates._


case class MutableTreeSelector(path:Seq[String])(tree:MutableFile.Tree, selected:Seq[String], onSelect: Seq[String] => Unit, entryClass: (Seq[String], MutableFile) => Option[String]) extends VHtmlComponent with Morphing(tree, selected, onSelect, entryClass) {

  var expanded = true
  val morpher = createMorpher(this)

  def toggle():Unit =
    expanded = !expanded
    rerender()

  def render = 
    val (tree, selected, onSelect, entryClass) = prop
    if expanded then
      <.div(^.cls := "expander expanded",
        <.button(^.cls := "fileSelector expander expanded", ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.lastOption.getOrElse(""), ^.onClick --> toggle()),
        for n <- tree.files.keys.toSeq.sorted yield 
          mutableFileSelector(path :+ n, selected, tree.files(n))(onSelect, entryClass)
      )
    else
      <.div(^.cls := "expander collapsed",
        <.button(^.cls := "fileSelector expander collapsed",  ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.lastOption.getOrElse(""), ^.onClick --> toggle())
      )

}

def mutableFileSelector(path:Seq[String], selected:Seq[String], f:MutableFile)(onSelect: Seq[String] => Unit, entryClass: (Seq[String], MutableFile) => Option[String]) = f match {
  case t:File.Tree => MorphingTreeSelector(path)(t, selected, onSelect)
  case _ => <.button(
    ^.cls := (if path == selected then "fileSelector textfile selected " + entryClass(path, f).mkString else "fileSelector textfile " + entryClass(path, f).mkString),  
    ^.attr("style") := s"padding-left: ${5 + 10 * path.length}px", path.last, ^.onClick --> onSelect(path)
  )
}


case class MutableTreeViewer()(tree:MutableFile.Tree, height: Int, highlight: (Seq[String], String) => Option[String] = { (_, _) => None }, entryClass: (Seq[String], MutableFile) => Option[String]) extends VHtmlComponent with Morphing(tree) {

  val morpher = createMorpher(this)

  var selected:Seq[String] = Seq.empty

  def select(seq:Seq[String]):Unit = 
    selected = seq
    rerender()

  def viewer(path:Seq[String], f:MutableFile) = f match {
    case f:MutableFile.TextFile => 
      <.div(^.cls := CodeStyle.editorDiff.className + " fileViewer",
        breadcrumbs(path),
        CodeJarFileEditor(f) { text => 
          update()
          highlight(selected, text)
        }
      )
    case _ => <.div()
  }

  def render = <.div(^.cls := CodeStyle.codeCard.className,
    <.div(^.cls := CodeStyle.selectorAndViewer.className, ^.attr("style") := s"height: ${height}px",
      <.div(^.cls := "fileList", 
        MutableTreeSelector(Seq.empty)(tree, selected, select(_), entryClass)
      ),
      for f <- prop.find(selected.toList) yield viewer(selected, f)
    )
  )


}

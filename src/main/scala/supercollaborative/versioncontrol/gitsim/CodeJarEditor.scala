package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.Update
import com.wbillingsley.veautiful.html.*
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.{Event, Node, html}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("codejar", "CodeJar")
object JSCodejar extends js.Object:
  def apply(element:HTMLElement, highlight: js.Function):js.Dynamic = js.native

object CodeJar:
  def apply(element:HTMLElement)(highlight: js.Dynamic => Unit) = JSCodejar(element, highlight)

case class CodeJarFileEditor(file:MutableFile.TextFile)(highlight: String => Option[String] = { _ => None }) extends VHtmlNode with Update {

    export structure.attach
    export structure.detach
    export structure.domNode

    val structure = <.div(^.cls := CodeStyle.styling.className,
      file.text
    )

    private var editor:Option[js.Dynamic] = None

    override def update() = reload()

    def reload():Unit = 
      for e <- editor do 
        if e.textContent.asInstanceOf[String] != file.text then e.updateCode(file.text)

    override def afterAttach():Unit = {
      for n <- domNode do
        Some(CodeJar(n) { editor => 
          this.editor = Some(editor)
          val text = editor.textContent.asInstanceOf[String]
          if file.text != text then
            file.text = text
            for replace <- highlight(text) do editor.innerHTML = replace          
        })
      
    }
  
}
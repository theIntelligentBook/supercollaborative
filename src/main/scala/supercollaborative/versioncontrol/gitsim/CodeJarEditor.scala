package supercollaborative.versioncontrol.gitsim

import com.wbillingsley.veautiful.html.*
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.{Event, Node, html}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("codejar", "CodeJar")
object Codejar extends js.Object:
  def apply(element:HTMLElement):js.Dynamic = js.native

case class CodeJarEditor(name:String)(val initialText:String) extends VHtmlNode {

    export structure.attach
    export structure.detach
    export structure.domNode

    val structure = <.div(^.cls := CodeStyle.styling.className,
      initialText,
    )

    override def afterAttach():Unit = {
      for n <- domNode do
        val editor = Codejar(n)
      
    }
  
}
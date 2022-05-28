package supercollaborative.templates

import com.wbillingsley.veautiful.html.{Styling, VHtmlNode, VHtmlComponent, <, ^}
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

import supercollaborative.given
import supercollaborative.Common.marked

case class FileLoader(f:String) extends VHtmlComponent {

  private def loadText():Future[String] =
    import scalajs.js.Thenable.Implicits._ 
    for 
      response <- org.scalajs.dom.experimental.Fetch.fetch(f)
      text <- response.text()
    yield text

  def render = <.div(
    FutureComp[String](loadText()) { text => 
      <.pre(text.split("\n").flatMap(s => Seq(<.span(^.cls := "code-content", s), <.br()))) 
    }
  )

}

case class Example(pwd:String, path:String, sources:Seq[String]) extends VHtmlComponent {

  def filePath(fileName:String) = (if pwd.isEmpty || pwd.endsWith("/") then pwd + fileName else pwd + "/" + fileName)

  case class FileViewer(pwd:String, sources:Seq[String]) extends VHtmlComponent {
    var selected = 0

    def select(i:Int):Unit = 
      selected = i
      rerender()

    def render = <.div(^.cls := "sources",
      <.div(^.cls := "fileList", 
        for (f, i) <- sources.zipWithIndex yield 
          if i == selected then
            <.button(^.cls := "selected", ^.on("click") --> select(i), f)
          else
            <.button(^.on("click") --> select(i), f)
      ),
      <.div(^.cls := "viewer",
        if sources.indices.contains(selected) then FileLoader(filePath(sources(selected))) else <.div()
      )
    )

  }

  def render = <.div(^.cls := Example.styling.className, 
      <.div(^.cls := "embed",
        <.iframe(^.src := filePath(path))
      ),
      FileViewer(pwd, sources)
  )

}

object Example {
  val styling = Styling(
    """
      |""".stripMargin
  ).modifiedBy(
    " .embed" -> "border: 1px solid #aaa;",
    " iframe" -> "border: none; width: 100%; height: 60vh;",
    " .fileList" -> "display: flex; flex-wrap: nowrap; border-bottom: 1px solid #aaa; width: 100%; overflow-x: auto;",
    " .fileList button" -> "border-width: 0 1px 0 0; border-color: #aaa; background: #eee;",
    " .fileList button:hover" -> "background: #ddd;",
    " .fileList button.selected" -> "background: #dde;",
    " .fileList button.selected:hover" -> "background: #eef;",
    " .viewer" -> "width: 100%; max-height: 90vh; padding: 5px; overflow: auto;",
    " .viewer pre" -> "counter-reset: line; overflow: visible;",
    " .viewer pre span" -> "counter-increment: line;",
    " .viewer pre span:before" -> "content: counter(line); text-align: right; padding-right: 5px; border-right: 1px solid #aaa; width: 2em; display: inline-block; margin-right: 5px;",
  ).register()
}

case class OutsideExample(show:String, source:String) extends VHtmlComponent {

  def render = <.div(^.cls := Example.styling.className,
    <.div(^.cls := "embed",
      <.iframe(^.src := show),
    ),
    <.div(
      "Source: ", <.a(^.attr("target") := "_blank", ^.href := source, source)
    )
  )

}


case class GitTutorial(url:String, title:String, md:String = "") extends VHtmlComponent {

  def render = <.div(
    <.h1(title),
    marked(md),
    <.div(^.cls := "tutorial-link",
      <.p("This tutorial is hosted in a git repository on an external site:"),
      <.ul(<.li(<.a(^.attr("target") := "_blank", ^.href := url, url)))
    )


  )

}
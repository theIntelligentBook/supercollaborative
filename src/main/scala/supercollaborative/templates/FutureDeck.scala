package willscala.templates

import scala.util.{Try, Success, Failure}
import scala.concurrent.Future
import com.wbillingsley.veautiful.{Morphing, MakeItSo, Unique}
import com.wbillingsley.veautiful.html.{VHtmlComponent, VHtmlNode, <}
import com.wbillingsley.veautiful.templates.{VSlides, DeckBuilder}
import com.wbillingsley.veautiful.doctacular.DeckPlayer

import com.wbillingsley.handy.Latch

import scala.concurrent.ExecutionContext.Implicits.global
import willscala.given
import willscala.Common.{marked, chapterHeading}

case class FutureComp[T](future:Future[T])(f: T => VHtmlNode) extends VHtmlComponent with Morphing(f) {
  if !future.isCompleted then future.onComplete { _ => rerender() }

  // This is unchecked due to the runtime not being able to verify that f: T => VHtmlNode in an "equal"
  // component it is morphing to is the same type. However, in that case it would contain a different
  // Future[T] and could not be regarded as equal anyway (hence that match would never be called).
  val morpher = new MakeItSo {
    def makeItSo = {
      case c: FutureComp[T] @unchecked => 
        updateProp(c.prop)
    }
  }

  override def render = future.value match {
    case None => <.div("Loading...")
    case Some(Success(t)) => <.div(prop(t))
    case Some(Failure(x)) => <.div(s"Failed to load: ${x.getMessage}")
  }

}


given futurePlayer[T : DeckPlayer]:DeckPlayer[Future[T]] with {

    extension(fv:Future[T]) {
      def defaultView(name:String) = FutureComp(fv)(v => v.defaultView(name))
      def fullScreenPlayer = Some((name:String, slide:Int) => FutureComp(fv) { v => 
        v.fullScreenPlayer match {
          case Some(f) => f(name, slide)
          case None => <.div("No renderer found after the deck loaded!")
        }
      })

    }

}


given latchlayer[T : DeckPlayer]:DeckPlayer[Latch[T]] with {

    extension(lv:Latch[T]) {
      def defaultView(name:String) = FutureComp(lv.request)(v => v.defaultView(name))
      def fullScreenPlayer = Some((name:String, slide:Int) => FutureComp(lv.request) { v => 
        v.fullScreenPlayer match {
          case Some(f) => f(name, slide)
          case None => <.div("No renderer found after the deck loaded!")
        }
      })

    }

}

val markdownDeckMap = scala.collection.mutable.Map.empty[String, Latch[VSlides]]
val markdownPageMap = scala.collection.mutable.Map.empty[String, Latch[VHtmlNode]]

def markdownDeck(title:String, path:String):Latch[VSlides] = markdownDeckMap.getOrElseUpdate(path, loadMarkdownDeck(title, path))

def loadMarkdownDeck(title:String, path:String):Latch[VSlides] = Latch.lazily({
  import scalajs.js.Thenable.Implicits._ 
  
  for 
    response <- org.scalajs.dom.experimental.Fetch.fetch(path)
    text <- response.text()
  yield 
    DeckBuilder(1920, 1080)
      .markdownSlide("# " + title).withClass("center middle")
      .markdownSlides(text)
      .renderSlides

})

def markdownPage(path:String):VHtmlNode = 
  FutureComp(markdownPageMap.getOrElseUpdate(path, loadMarkdownPage(path)).request)(identity)

def loadMarkdownPage(path:String):Latch[VHtmlNode] = Latch.lazily({
  import scalajs.js.Thenable.Implicits._ 
  
  for 
    response <- org.scalajs.dom.experimental.Fetch.fetch(path)
    text <- response.text()
  yield 
    Unique(marked(text))

})

def markdownChapterPage(number:Int, title:String, image:String, path:String):VHtmlNode = 
  FutureComp(markdownPageMap.getOrElseUpdate(path, loadMarkdownChapterPage(number, title, image, path)).request)(identity)

def loadMarkdownChapterPage(number:Int, title:String, image:String, path:String):Latch[VHtmlNode] = Latch.lazily({
  import scalajs.js.Thenable.Implicits._ 
  
  for 
    response <- org.scalajs.dom.experimental.Fetch.fetch(path)
    text <- response.text()
  yield 
    Unique(<.div(
      chapterHeading(number, title, image),
      marked(text)
    ))

})
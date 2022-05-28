package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

/** A little example showing how code goes through broken states */
case class BreakingChangePlayer() extends VHtmlComponent {

  val before = ""
  val after = "def bark():Unit = println(\"Woof!\")"

  def text = <.pre(
    """
    |class Dog(name:String) {
    |
    |  """.stripMargin, after.take(step), <.span(^.attr("style") := "border-left: 1px solid #aaf;"), """
    |
    |}
    |""".stripMargin
  )

  def compiles = step == 0 || step == after.length

  var step = 0
  def stepForward():Unit = {
    if step < after.length then 
      step += 1
      rerender()
    else 
      animator.stop()
      rerender()
  }

  def reset() = {
    step = 0
    animator.stop()
    rerender()
  }

  def toEnd() = {
    step = after.length
    animator.stop()
    rerender()
  }

  val animator = Animator(this) { (t, elapsed, dt) => 
    if dt > 250 then 
      stepForward()
      true;
    else false
  }

  def render = <.div(
    text,
    <.p(
      <.button("⇤", ^.onClick --> reset()),
      <.button("▶", ^.onClick --> animator.start()),
      <.button("⇥", ^.onClick --> toEnd()),
      if compiles then 
        <.span(^.attr("style") := "color: #4a4;", " ✓ Compiles")
      else 
        <.span(^.attr("style") := "color: #c44;", " ✕ Does not compile")
    )
  )

}

val textFiles = File.Tree(Map(
  "moray.txt" -> File.TextFile("Bla bla"),
  "malaprop.txt" -> File.TextFile("Promise to forget him. To illiterate him, I say, quite from your memory")
))

val example1 = textFiles.toMutable

val vcDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control").withClass("center middle")
  .veautifulSlide(<.div(
    <.h1("Changes are discrete."),
    <.p("Whenever we edit programs, we break them. They don't become unbroken until we've finished making our change."),
    BreakingChangePlayer(),
    <.p("That's a trivial example in a single file, but the same is true between files. E.g. changing a method definition in one class means we need to change where it's called too."),
  ))
  .markdownSlides("""
  |# Programs are fragile. Take snapshots
  |
  |The source code repository is going to take snapshots of our work. 
  |We call these *commits* because we are *commit* the snaphsot to the repository
  |
  |The repository is then going to contain the *history* of our code.
  |
  |---
  |
  |## A brief history of Version Control
  |
  |* Back in 1982, `rcs` (Revision Control System) kept change histories for individual files. But that's painful, because changes might involve working
  |  on more than one file at once.
  |
  |* In 1986, `cvs` (Concurrent Versions Systems) added support for coordinating commits across multiple files and sharing changes with a server.
  |  This started the era of *centralised version control*, where a server holds the full history of the project and developers "check out" 
  |  only the version they are working on.
  |
  |* In the early 2000s, several projects started working on *distributed* version control, where every developer would have have the full history of their repository.
  |  They can send changes to or from any other repository.
  |
  |* `git` is now the de facto standard version control system. It was developed for managing the source-code of Linux around 2005. 
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |## The working tree
    |
    |Suppose this is the directory we're putting under version control
    |""".stripMargin),
    TreeViewer(example1)
  ))
  .renderSlides
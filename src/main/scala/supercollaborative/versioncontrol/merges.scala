package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

val mergeExample = Git.init
  .commit("Will", "A", 1)
  .commit("Will", "B", 2)
  .branch("feature")
  .commit("Will", "C", 3)
  .switch("feature")
  .commit("Will", "D", 4)
  .commit("Will", "E", 5)
  .switch("main")
  .commit("Will", "F", 6)


case class ThreeWayDiff(left:String, common:String, right:String) extends VHtmlComponent {

  import CompareResult.*

  val chunks = threeWayChunk(left.split("\n"), common.split("\n"), right.split("\n"))

  def render = <.div(^.cls := CodeStyle.threeWayDiff.className,
    for (a, o, b) <- chunks yield
      <.div(^.cls := "chunk",
        <.pre(a.mkString("\n")),
        <.pre(o.mkString("\n")),
        <.pre(b.mkString("\n"))
      ) 
  )


}


case class ThreeWayDiff1(left:String, common:String, right:String) extends VHtmlComponent {

  import CompareResult.*

  val lChanges = compare(common.split("\n"), left.split("\n"))
  val rChanges = compare(common.split("\n"), right.split("\n"))
  val bothChanges = compare(lChanges, rChanges)

  enum Op[+T]:
    case Keep(s:T)
    case Delete
    case Blank
    case Add(s:T)

  import CompareResult.*
  import Op.*

  val show = bothChanges.map { 
    case Both(Both(s)) => (Keep(s), Keep(s), Keep(s))
    case Both(Left(s)) => (Delete, Some(s), Delete)
    case Both(Right(s)) => (Add(s), Blank, Add(s))
    case Left(Both(s)) => (Keep(s), Blank, Blank)
    case Left(Left(s)) => (Delete, Blank, Blank)
    case Left(Right(s)) => (Add(s), Blank, Blank)
    case Right(Both(s)) => (Delete, Keep(s), Keep(s))
    case Right(Left(s)) => (Blank, Keep(s), Delete)
    case Right(Right(s)) => (Blank, Blank, Add(s))
  }

  def render = <.div(^.cls := CodeStyle.threeWayDiff.className,
    <.pre(
      for 
        (l, _, _) <- show 
        el <- Seq(<.span(l.toString), <.br())
      yield el
    ),
    <.pre(
      for 
        (_, m, _) <- show 
        el <- Seq(<.span(m.toString), <.br())
      yield el
    ),
    <.pre(
      for 
        (_, _, r) <- show 
        el <- Seq(<.span(r.toString), <.br())
      yield el
    )
  )


}

val mergesDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Merges").withClass("center middle")
  .markdownSlides("""
  |## Merging
  |
  |Our version history can *branch*, in which case a commit may have more than one child
  |A *merge* brings those branches back together. 
  |
  |We also often have to merge changes from other developers' main branches into ours.
  |
  |Merges come in two kinds:
  |
  |* Fast-forward merges, if we're merging changes that are strictly ahead of our branch pointer.
  |  (i.e. our branch's current commit is one of the ancestors of the commit we're merging)
  |
  |* Merges that create a "merge commit"
  |
  |`git merge branch-to-merge-from` will merge another branch into our currently checked out branch.
  |Somehow git (with the programmer's help) needs to figure out what the merged work should look like.
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |## What are the changes we're merging?
    |
    |Suppose we want to merge the changes from `feature` into `main`. 
    |
    |""".stripMargin),
    {
      Seq(
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          mergeExample.refs.toSeq :+ mergeExample.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |We're trying to merge two branches of changes to some code. But changes from what?
    |
    |The first step git will try to do is identify a common ancestor to compare them to.
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Common ancestor
    |
    |Git tries to find a third commit to compare them to. Typically, this will be their most recent common ancestor.
    |
    |""".stripMargin),
    {
      Seq(
        <.p(blockLabel("local")(hscrollBox(MergeHDAG(
          mergeExample.refs.toSeq :+ mergeExample.headAsDetached, from = mergeExample.branches("feature"), to = mergeExample.head
        ))))
      )
    },
    Common.marked(s"""
    |Git now needs to try to a *three-way merge*. It needs to consider
    |
    |* The changes (diff) between the common ancestor and the destination, and
    |* The changes (diff) between the common ancestor and the source
    |
    |So that it can merge the two sets of changes
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Two sets of diffs
    |
    |If we look at any of the text files, we may find we now have two sets of changes to resolve.
    |
    |""".stripMargin),
    {
      val common = moray
      val r = mondegreen
      val l = """Ye Hielan's an' ye Lowlan's
      |O, where's ya bin?
      |I don't mean ya wheelie
      |But the place ya bin in
      |They hae slain the Earl of Moray
      |And lain him on the green.
      |""".stripMargin


      ThreeWayDiff(l, common, r)
    },
    Common.marked(s"""
    |Git now needs to try to a *three-way merge*. It needs to consider
    |
    |* The changes (diff) between the common ancestor and the destination, and
    |* The changes (diff) between the common ancestor and the source
    |
    |So that it can merge the two sets of changes
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Chunks
    |
    |The usual algorithm is to divide the text file into "chunks", lining up the
    |areas where all three version match.
    |
    |""".stripMargin),
    {
      val common = moray
      val r = mondegreen
      val l = """Ye Hielan's an' ye Lowlan's
      |O, where's ya bin?
      |I don't mean ya wheelie
      |But the place ya bin in
      |They hae slain the Earl of Moray
      |And lain him on the green.
      |""".stripMargin


      ThreeWayDiff(l, common, r)
    },
    Common.marked(s"""
    |Then, for each chunk:
    |
    |* If all three versions match, accept that chunk
    |* If one version matches the original, but the other doesn't, accept the changed chunk
    |* If both sides differ from the original (and each other), mark it as a *conflict*.
    |""".stripMargin),
  ))
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
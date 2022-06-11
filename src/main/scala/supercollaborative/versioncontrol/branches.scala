package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._
import scalajs.js
import scala.util.Random

val revertedExample = gitExample
      .addAll(gitExample.head.commit.parents(0).tree)
      .commit("Algernon Moncrieff", s"Reverted commit ${gitExample.head.commit.hash}", t0.getTime)

val longLinearExample = (1 to 100).foldLeft(Git.init) { case (g, i) => g.commit("Will", s"Generated commit $i", i) }

val tagExample = longLinearExample
    .checkout_^(20)
    .tag("release2.0")
    .checkout_^(25)
    .tag("release1.0")
    .switch("main")

val branchExample = Git.init
    .commit("Will", "A", 1)
    .commit("Will", "B", 2)
    .branch("v1.0")
    .tag("release1.0")
    .commit("Will", "C", 3)
    .commit("Will", "D", 4)
    .tag("release2.0")
    .branch("v2.0")

def hscrollBox = <.div(^.attr("style") := "max-width: 100%; overflow-x: auto")

lazy val branchDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Branches").withClass("center middle")
  .veautifulSlide(<.div(
    Common.marked("""
    |## Histories
    |
    |So far, we've described a very simple version history with just a linear
    |history of changes.
    |
    |""".stripMargin),
    BranchHistoryAndTree(revertedExample.branches("main"), 300),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Drawing a graph
    |
    |Let's lay that out as a *graph*, horizontally for now
    |
    |""".stripMargin),
    HDAGOnly(Seq(revertedExample.head.namedDetach("HEAD")), hbCompactLabel),
    Common.marked("""
    |Note that each commit contains a reference to its parent, but doesn't know
    |who its children are (or will be). So, we draw the arrow
    |pointing from the child to the parent.
    |
    |We'll call this sort of node and line diagram a *git graph*.
    |
    |HEAD is shows what we currently have checked out. Right now, it looks like
    |the head of a linked list pointing back in time.
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A longer graph
    |
    |When we're working on software, we might have a lot of revisions we've made over time.
    |
    |Let's imagine this is the git graph for some software we're developing (showing just
    |the commit hashes, not the comments etc, for space)
    |
    |""".stripMargin),
    <.p(hscrollBox(
        HDAGOnly(Seq(longLinearExample.head.namedDetach("HEAD")), hbHashOnly),
    )),
    Common.marked("""
    |
    |Suppose that somewhere during the development, you released a version of the software
    |to a customer. Which version was it?
    |
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Tags
    |
    |We probably don't just want to use the commit hash as the release name (it's not memorable)
    |but we would like to mark which version our release was.
    |
    |Instead, we can ask git to store *tags* that point to particular commits
    |
    |""".stripMargin),
    <.p(hscrollBox(
        HDAGOnly(Seq(tagExample.head.namedDetach("HEAD"), tagExample.tags("release1.0"), tagExample.tags("release2.0")), hbHashOnly),
    )),
    Common.marked("""
    |To create a *lightweight* tag on the current commit, 
    |
    |```sh
    |git tag mytagname
    |```
    |
    |To create a *tag object* with a comment,
    |
    |```sh
    |git tag -a mytagname
    |```
    |
    |To checkout a tag,
    |
    |```sh
    |git checkout mytagname
    |```
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Maintaining old releases
    |
    |Let's pretend our git history is shorter so it'll fit more easily on the page. 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({      
        val graph = branchExample      
        graph.tags.values.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
    Common.marked("""
    |
    |Once we've released a version of some software, we will need to maintain it.
    |
    |Even though we've moved on to developing version 2.0, there will be customers on version 1.0
    |who need their bugs fixed.
    |
    |Somehow we need to checkout `release 1.0`, fix some bugs, and release `release 1.1` without 
    |needing to take on all the changes up to `release 2.0`
    |
    |Our version history is going to *branch*
    |
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Branch pointers
    |
    |Let's create *branches* for our two released. We'll call them `v1.0` and `v2.0`
    |
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
      val graph = branchExample

        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
    Common.marked("""
    |
    |A branch is a named pointer to a commit, but the pointer will *move* as we work on the branch.
    | 
    |Notice we've also got a branch called `main`. This is the *default branch* &mdash; the branch a new repository starts on.
    |
    |(The default branch has always been there. I just haven't shown it in previous diagrams)
    |
    |I've also relabelled HEAD to show that I've got `main` checked out.
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Branch pointers
    |
    |If I do a new commit, notice that HEAD moves on, the branch I have checked out moves on, 
    |but `release 2.0` stayed where it was.
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
    Common.marked("""
    |
    |Notice we've also got a branch called `main`. This is the *default branch* &mdash; the branch a new repository starts on.
    |
    |(The default branch has always been there. I just haven't shown it in previous diagrams)
    |
    |I've also relabelled HEAD to show that I've got `main` checked out.
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Checkout an old branch
    |
    |Now let's checkout the `v1.0` branch. Only HEAD moves. None of the commits have changed.
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .checkout(branchExample.branches("v1.0"))
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
    Common.marked("""
    |
    |We'd also find git would update our working tree so that we've got the files from that snapshot checked out.
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Adding a commit to an old branch
    |
    |Now let's do a commit on the `v1.0` branch. Our commit graph no longer looks linear.
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .checkout(branchExample.branches("v1.0"))
        .commit("Will", "F", 6)
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Checkout `main`
    |
    |Now let's switch back to main.
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .switch("v1.0")
        .commit("Will", "F", 6)
        .switch("main")
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Checkout `main`
    |
    |And do another commit on `main`
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .switch("v1.0")
        .commit("Will", "F", 6)
        .switch("main")
        .commit("Will", "G", 7)
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Checkout `v2.0`
    |
    |Let's switch to `v2.0`
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .switch("v1.0")
        .commit("Will", "F", 6)
        .switch("main")
        .commit("Will", "G", 7)
        .switch("v2.0")
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A commit on the `v2.0` branch
    |
    |And let's do a commit on `v2.0`
    | 
    |""".stripMargin),
    <.p(hscrollBox(HDAGOnly({
        val graph = branchExample
        .commit("Will", "E", 5)
        .switch("v1.0")
        .commit("Will", "F", 6)
        .switch("main")
        .commit("Will", "G", 7)
        .switch("v2.0")
        .commit("Will", "H", 7)
      
        graph.refs.toSeq :+ graph.headAsDetached
      }, 
      hbHashOnly
    ))),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Branch history
    |
    |The git graph is a *directed acyclic graph*. 
    |
    |""".stripMargin),
    SelectableHDAG({
      val graph = branchExample
        .commit("Will", "E", 5)
        .switch("v1.0")
        .commit("Will", "F", 6)
        .switch("main")
        .commit("Will", "G", 7)
        .switch("v2.0")
        .commit("Will", "H", 7)
      
      graph.refs.toSeq :+ graph.headAsDetached
    }),
    Common.marked("""
    |
    |If we checkout a commit or a branch and ask for its history, we'll get all the ancestors of that commit.
    |
    |In the graph above, click on a commit's circle to see the history of the commit highlighted.
    |
    |""".stripMargin),
  ))
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
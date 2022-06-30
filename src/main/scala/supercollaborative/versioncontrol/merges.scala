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

      Seq(
        BigDiffViewer(common, l),
        BigDiffViewer(common, r)
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
    |## Chunks
    |
    |The usual algorithm is to divide the text file into "chunks", lining up the
    |areas where all three version match. (I've put the original in the middle here.)
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
    |
    |In the example above, the changes don't overlap, so this can be merged automatically
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A conflicting change
    |
    |The usual algorithm is to divide the text file into "chunks", lining up the
    |areas where all three version match. (I've put the original in the middle here.)
    |
    |""".stripMargin),
    {
      val common = moray
      val r = mondegreen
      val l = """Ye Hielan's an' ye Lowlan's
      |O, where have ye been?
      |They hae slain the Earl of Moray
      |And lain him underground.
      |""".stripMargin


      ThreeWayDiff(l, common, r)
    },
    Common.marked(s"""
    |In the example above, the changes do overlap, so this would create a merge conflict
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A conflicting change
    |
    |In this example, we've made a change that "humanly" could be merged. One change edits the
    |line; the other change turns the text to capitals. 
    |
    |""".stripMargin),
    {
      val common = moray
      val r = mondegreen
      val l = """Ye Hielan's an' ye Lowlan's
      |O, where have ye been?
      |They hae slain the Earl of Moray
      |AND LAIN HIM ON THE GREEN.
      |""".stripMargin


      ThreeWayDiff(l, common, r)
    },
    Common.marked(s"""
    |We might think "the solution is to put the new line in capitals", but git cannot infer that for us.
    |It'll mark it as a merge conflict, and we have to fix it manually
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Conflict markup
    |
    |Where git cannot merge a file automatically, it'll mark the path as unmerged and the file will contain
    |the text marked up showing the conflict
    |
    |""".stripMargin),
    {
      val common = moray
      val r = mondegreen
      val l = """Ye Hielan's an' ye Lowlan's
      |O, where have ye been?
      |They hae slain the Earl of Moray
      |AND LAIN HIM ON THE GREEN.
      |""".stripMargin

      val (_, markedup) = mergeResult(l, common, r)
      CodeJarFileEditor(MutableFile.TextFile(markedup))()
    },
    Common.marked(s"""
    |It's then up to us to:
    |
    |1. Edit the file to what we think it should look like (manually merge the changes)
    |2.  `git add` the file to mark the conflict as resolved
    |3. When we've fixed all the conflicts, `git commit` to complete the merge. (Git will write a commit message for us)
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Merging someone else's changes
    |
    |In our developer story for remotes, we had this stage where our local `main` and `origin/main` each differed by one commit.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val local  = local1.commit("Will", "4", 4)
      val remote = remote1.commit("Algernon", "5", 5)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        )))),
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Let's fetch the repository
    |
    |If we just do `git fetch` we can see the branching more easily in our local repository. We're going to need to merge `origin/main` into `main`.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val remote = remote1.commit("Algernon", "5", 5)
      val local  = local1.commit("Will", "4", 4).fetch("origin", remote)

      Seq(
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        )))),
        Common.marked(s"""
        |
        |The second stage of our `git pull` is (effectively) `git merge origin/main`. We could get a merge conflict.
        """.stripMargin)
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## `git merge origin/main`
    |
    |Here, we can see git trying to create the merge commit. If the merge produces a confict, we'll have to resolve
    |the conflict to help it do the merge. So part of this step might be manual.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val remote = remote1.commit("Algernon", "5", 5)
      val local  = local1.commit("Will", "4", 4).fetch("origin", remote).nonFFMerge("Will", ("origin" -> "main"), 6)

      Seq(
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        )))),
        Common.marked(s"""
        |
        |And we're going to have to do it before we can push our changes to origin.
        |
        |We should then push our changes to origin *soon*.
        |(Until we've pushed our merged changes, someone else might push more changes that we don't have, and then we'll need to merge those changes in too before we can push!)
        """.stripMargin)
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## After `git push origin main`
    |
    |After we've resolved the conflict **and pushed it to origin** we're back in a happier state.
    |
    |Our changes are merged and pushed, and any other developer making changes will have to deal with merging their changes before they can push.
    |
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val remote2 = remote1.commit("Algernon", "5", 5)
      val local2  = local1.commit("Will", "4", 4).fetch("origin", remote2).nonFFMerge("Will", ("origin" -> "main"), 6)
      val (local, remote) = local2.pushBranch("origin", "main", remote2)

      Seq(

        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |**BUT:** We only want to push changes that compile and **don't break the build**.
    |(Otherwise any other developer pulling from origin will find we just broke their build too.)
    |More on that when we get to testing...
    |""".stripMargin),
  ))
  .markdownSlides("""
  |## Merge hell
  |
  |* If the merge produces a few lines of conflict in one method, you'll probably find you can work out how to resolve it fairly easily.
  |
  |* If the merge produces *hundreds* of lines of conflict across *dozens* of methods and files, you're in for a grim day.
  |
  |"**Merge hell**" or "**integration hell**" is where merging your changes is harder than making the changes was in the first place!
  |
  |---
  |
  |## The myth of parallel development
  |
  |You and your colleagues are not working "in parallel". Every change you make and don't push makes your version of the code **diverge** from their version of the code.
  |
  |* The more your code diverges, the more likely you are to get merge conflicts.
  |
  |* The more your code diverges, the harder each conflict could be to resolve
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    <.h2("A change getting conceptually more complex"),
    {
      val orig =       
        """|class Dog(name:String) {
           |
           |}
           |""".stripMargin

      val left = MutableFile.TextFile(orig)
      val right = MutableFile.TextFile(orig)

      val output = new VHtmlComponent {
        def render = <.pre(^.attr("style") := "background: #fafafa; border-radius: 5px; padding: 5px;",
          mergeResult(left.text, orig, right.text)._2
        )
      }

      <.div(^.attr("style") := "display: grid; grid-template-columns: 1fr 1fr 1fr;",
        CodeJarFileEditor(left)({ _ => output.update(); None}),
        CodeJarFileEditor(right)({ _ => output.update(); None}),
        output      
      )
    }

  ))
  .markdownSlides("""
  |
  |## Avoiding merge hell #1
  |
  |If two people are trying to add the same functionality at the same time (and don't know it),  
  |they will probably need to edit the same locations in the code (slightly differently).  
  |Merge conflicts will ensue.
  |
  |  * **We need a way of telling each other what we're working on**.  
  |    ("Ticket management!")
  |
  |---
  |
  |## Avoiding merge hell #2
  |
  |Suppose there is one class in the code that does *everything*.  
  |Every change will involve editing that class.  
  |Even if two people are working on different functionality, they'll still need to edit the same code.  
  |Merge conflicts will ensue
  |
  |  * **We need to write code so that each part has distinct and coherent responsibilities**.  
  |    ("Good design!")
  |
  |---
  |
  |## Avoiding merge hell #3
  |
  |The more your code has diverged from what's on origin/main,  
  |the more likely you are to get a merge conflict.  
  |  
  |  * **We need to make small coherent changes and share them often**.  
  |
  |---
  |
  |## Avoiding merge hell #4
  |
  |The more the code on origin/main has diverged from your code (from other people making changes),  
  |the more likely you are to get a merge conflict.
  |
  |  * **We need to pull from origin/main regularly**.
  |
  |---
  |
  |## Extreme programming (XP) and Continuous Integration (CI)
  |
  |Extreme Programming (XP) is a programming philosophy that asked
  |
  |> What if we take all the things people think are good practice, and take them to extremes
  |
  |* What if developers don't just review code, they review it continuously (pair programming)
  |
  |* What if developers don't just test their code, they write the tests first (test-driven development)
  |
  |* What if developers don't just merge often, they merge every day (continuous integration)
  |
  |Continuous Integration, as a practice ("merge early, merge often") came from this XP philosophy
  |
  |> Each development pair is responsible for integrating their own code when ever a reasonable break presents itself. 
  |> This may be when the unit tests all run at 100% or some smaller portion of the planned functionality is finished. 
  |> Only one pair integrates at any given moment and after only a few hours of coding to reduce the potential problem location to almost nothing.  
  |> &mdash; [Don Wells, extremeprogrammingrules.org](http://www.extremeprogramming.org/rules/integrateoften.html)
  |
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.*
import com.wbillingsley.veautiful.templates.Challenge
import Challenge.Level
import supercollaborative.Common.*
import supercollaborative.versioncontrol.gitsim.*
import scala.collection.mutable
import com.wbillingsley.veautiful.MakeItSo
import org.scalajs.dom.html

class VNodeStage(n: => VHtmlNode) extends Challenge.Stage {

  override def completion: Challenge.Completion = Challenge.Open

  override def kind: String = "text"

  override protected def render = Challenge.textColumn(n)

}

lazy val tutorialTree = MutableFile.Tree(mutable.Map(
  "doubledactyl.txt" -> MutableFile.TextFile("""
  A Double Dactyl by Will...

  Huppity puppity,
  All of a muckity!
  Muddy dog drawing his
  Shapes on the wall.

  Swishing his flickity,
  Wagging and whippety - 
  Muddochromatically,
  Painting them all.
  """.stripIndent),
  "limerick.txt" -> MutableFile.TextFile("""
  A nonsense limerick by Will...

  There once was a frog (not a prince)
  Who's taken up etiquette since
  He heard at the races
  If he put on some graces
  They'd make him a knight in a cinch

  Sir Frog, with his tie in a bow
  Would tug it to greet his hello
  One winter so glum
  His fingers so numb
  He lost the bow tie in the snow

  Unfurnished, now, with his attire
  He no longer looks like a squire
  Hopping around
  And scouring the ground
  He's searching all over the shire
  """.stripIndent),

))

case class GitSimEx(tree:MutableFile.Tree)(config: EditSuiteConfig) extends Challenge.Stage with Morphing(config) {
  override val morpher = createMorpher(this)
  
  override def completion = Challenge.Open

  override def kind: String = "exercise"

  def isComplete:Boolean = completion match {
    case Challenge.Complete(_, _) => true
    case _ => false
  }

  def ocu() = {
    rerender()
  }

  def lossyLocalChanges(g:Git, to:Ref) = 
    g.head.commit != to.commit && tree.toImmutable != g.head.commit.tree

  def step1  = <.div(
    marked("""
    ### 1. Initialising the repository

    Right now, the files on the right are just files - it's not a git repository yet.
    So, it's not highlighting any changes because there's no repository. But you
    can still select and edit the files.
    
    To turn this into a git repository, let's do `git init` (click the execute button for the command below).
    This will also make most of the rest of the instructions (which need a git repository) appear.
    """.stripIndent),
    prop.git match {
      case Some(_) => 
        marked("""
        Done: `git init`

        Now that we have a git repository, git will compare the files to the tree in its index (currently empty). 
        So, everything looks new and untracked. Here, we've shown that by marking the untracked files with a "U"
        in the file selector, and colouring their filenames green.

        Now that we have a git repository, we can show you the next instructions. (We needed a git repo to run
        the commands on)
        """.stripIndent)
      case None =>
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", "git init"),
          <.button(^.cls := "execute", "▶", ^.onClick --> {
            updateProp(prop.copy(git = Some(Git.init)))
          })
        )
    },
  )

  def step2 = for g <- prop.git yield {
    var name = g.authorName
    var email = g.authorEmail

    <.div(
      markedF(s"""
      ### 2. Configuring you

      Once you've created the repository, you need to tell it who you are. Right now, it thinks you are:

      ```
      ${g.currentAuthor}
      ```

      If you'd like to configure your name and email address, you can do so using the commands below.
      """.stripIndent),
      <.div(^.cls := CodeStyle.commandBlock.className,
        <.span(^.cls := "sh", "git config user.name \""),
        <.input(^.cls := "sh", ^.on("change") ==> { e => for s <- e.inputValue do name = s }),
        <.span(^.cls := "sh", "\""),
        <.button(^.cls := "execute", "▶", ^.onClick --> {
          updateProp(prop.copy(git = Some(g.copy(authorName = name))))
        })
      ),
      <.div(^.cls := CodeStyle.commandBlock.className,
        <.span(^.cls := "sh", "git config user.email \""),
        <.input(^.cls := "sh", ^.on("change") ==> { e => for s <- e.inputValue do email = s }),
        <.span(^.cls := "sh", "\""),
        <.button(^.cls := "execute", "▶", ^.onClick --> {
          updateProp(prop.copy(git = Some(g.copy(authorEmail = email))))
        })
      )
    )
  }

  def addCommand(path:List[String]) = {
    for g <- prop.git; f <- tree.find(path) yield 
      <.div(^.cls := CodeStyle.commandBlock.className,
        <.span(^.cls := "sh", s"git add ${path.mkString("/")} "),
        <.button(^.cls := "execute", "▶", ^.onClick --> {
          updateProp(prop.copy(git = Some(g.add(path, f.toImmutable))))
        })
      )
  }

  /** git restore --worktree path */
  def restoreWorktreeCommand(path:List[String]) = {
    for g <- prop.git; f <- g.index.find(path) yield 
      <.div(^.cls := CodeStyle.commandBlock.className,
        <.span(^.cls := "sh", s"git restore --worktree ${path.mkString("/")} "),
        <.button(^.cls := "execute", "▶", ^.onClick --> {
          tree.add(path, f.toMutable)
          rerender()
        })
      )
  }

  /** git restore --staged path */
  def restoreStagedCommand(path:List[String]) = {
    for g <- prop.git; f <- g.head.commit.tree.find(path) yield 
      <.div(^.cls := CodeStyle.commandBlock.className,
        <.span(^.cls := "sh", s"git restore --staged ${path.mkString("/")} "),
        <.button(^.cls := "execute", "▶", ^.onClick --> {
          updateProp(prop.copy(git = Some(g.add(path, f))))
        })
      )
  }

  def commitCommand = {
    var message = "" 

    for g <- prop.git yield 
      if g.index == g.head.commit.tree then 
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", s"(Can't commit - no uncommitted changes in index)"),
        )
      else g.head match {
        case Ref.Branch(n, _) => 
          <.div(^.cls := CodeStyle.commandBlock.className,
            <.span(^.cls := "sh", "git commit -m \""),
            <.input(^.cls := "sh", ^.attr("placeholder") := "Put a message here", ^.on("change") ==> { e => for s <- e.inputValue do message = s }),
            <.span(^.cls := "sh", "\""),
            <.button(^.cls := "execute", "▶", ^.onClick --> {
              updateProp(prop.copy(git = Some(g.commit(message, now))))
            })
          )
        case _ => 
          <.div(^.cls := CodeStyle.commandBlock.className,
            <.span(^.cls := "sh", s"(Can't commit - detached mode)"),
          )
      }
  }

  case class TagCommand() extends VHtmlComponent {
    var name = "" 

    def setName(s:String):Unit = 
      name = s
      rerender()

    def render = <.div(
      for g <- prop.git yield {
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", "git tag \""),
          <.input(^.cls := "sh", ^.attr("placeholder") := "name", ^.on("input") ==> { e => for s <- e.inputValue do setName(s) }),
          <.span(^.cls := "sh", "\""),
          <.button(^.cls := "execute", "▶", 
            ^.attr("disabled") ?= (if name.isEmpty || g.tags.contains(name) then Some("disabled") else None),
            ^.onClick --> { updateProp(prop.copy(git = Some(g.tag(name)))) }
          )
        )
      }
    )

  }

  case class BranchCommand() extends VHtmlComponent {
    var name = "" 

    def setName(s:String):Unit = 
      name = s
      rerender()

    def render = <.div(
      for g <- prop.git yield {
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", "git branch \""),
          <.input(^.cls := "sh", ^.attr("placeholder") := "name", ^.on("input") ==> { e => for s <- e.inputValue do setName(s) }),
          <.span(^.cls := "sh", "\""),
          <.button(^.cls := "execute", "▶", 
            ^.attr("disabled") ?= (if name.isEmpty || g.branches.contains(name) then Some("disabled") else None),
            ^.onClick --> { updateProp(prop.copy(git = Some(g.branch(name)))) }
          )
        )
      }
    )

  }

  case class CheckoutTagCommand() extends VHtmlComponent {
    var name = "" 

    def setName(s:String):Unit = 
      name = s
      rerender()

    def render = <.div(
      for g <- prop.git yield {
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", "git checkout \""),
          <("select")(^.cls := "sh", ^.on("change") ==> { e => 
              for s <- e.target match {
                case i:html.Select => Some(i.value)
                case _ => None
              } do setName(s) 
            },
            <("option")(""),
            for t <- g.tags.keys.toSeq yield <("option")(^.prop("value") := t, t)
          ),
          <.span(^.cls := "sh", "\""),
          <.button(^.cls := "execute", "▶", 
            ^.attr("disabled") ?= (if name.isEmpty || !g.tags.contains(name) || g.lossyChangesTo(g.tags(name)) || lossyLocalChanges(g, g.tags(name)) then Some("disabled") else None),
            ^.onClick --> { 
              val target = g.tags(name).commit
              if g.head.commit != target then 
                tree.files.clear
                tree.files.addAll(target.tree.toMutable.files)
              updateProp(prop.copy(git = Some(g.checkout(g.tags(name))))) 
            }
          )
        )
      }
    )

  }

  case class CheckoutBranchCommand() extends VHtmlComponent {
    var name = "" 

    def setName(s:String):Unit = 
      name = s
      rerender()

    def render = <.div(
      for g <- prop.git yield {
        <.div(^.cls := CodeStyle.commandBlock.className,
          <.span(^.cls := "sh", "git switch \""),
          <("select")(^.cls := "sh", ^.on("change") ==> { e => 
              for s <- e.target match {
                case i:html.Select => Some(i.value)
                case _ => None
              } do setName(s) 
            },
            <("option")(""),
            for t <- g.branches.keys.toSeq yield <("option")(^.prop("value") := t, t)
          ),
          <.span(^.cls := "sh", "\""),
          <.button(^.cls := "execute", "▶", 
            ^.attr("disabled") ?= (if name.isEmpty || !g.branches.contains(name) || g.lossyChangesTo(g.branches(name)) || lossyLocalChanges(g, g.branches(name))  then Some("disabled") else None),
            ^.onClick --> { 
              val target = g.branches(name).commit
              if g.head.commit != target then 
                tree.files.clear
                tree.files.addAll(target.tree.toMutable.files)
              updateProp(prop.copy(git = Some(g.switch(name)))) 
            }
          )
        )
      }
    )

  }

  def step3 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 3. Adding the files to the index

      When we first create the repository, the index is empty. Every file is untracked.

      To add the two files, execute the commands below:
    
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      <.p("Or, we can add both in one go by adding the current directory:"),
      addCommand(List(".")),
      markedF("""
      When you've added the files to the index, they will no longer show as untracked.

      If you edit the files after adding them, you'll notice they become "modified". The
      file in your working tree differs from the file in the index. You can update the
      file in the index with the file in the working tree by adding the file again.

      In this simulation, the Tree view will highlight differences against the index.
      The indicator in the top toolbar should day "Uncommitted changes in index". If you
      switch to the History view, you can see what's in the index.

      (Other editors mark up the UI in different ways. E.g. Visual Studio Code defaults to
      highlighting the file you're editing with diffs against the index, but putting
      "modified" indicators in the file selector for any files that differ from the latest
      commit.) 
      """.stripIndent)
    )
  }

  def step4 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 4. Making your first commit

      Now that we've made some uncommitted, changes, let's commit them.
    
      """.stripIndent),
      commitCommand,
      markedF("""
      Once we've done the commit, we should no longer see the "Uncommitted changes in index"
      marker in this editor, and if we switch to the History view we'll see the commit
      in the history.

      **Note:** You can't edit in the history view. Only in the tree view!
      """.stripIndent)
    )
  }

  def step5 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 5. Tagging our commit

      Let's create a tag for our commit. We have to give it a name that
      isn't empty and isn't a tag we've already created.
    
      """.stripIndent),
      TagCommand(),
      markedF("""
      Now switch to the Graph view and see that your tag has been added to the current commit.
      """.stripIndent)
    )
  }

  def step6 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 6. Make some more changes

      Make some more changes to the files and create another commit. (We've put the commands below
      again, but you can also use the earlier command boxes.)
    
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      commitCommand,
      markedF("""
      Now switch to the Graph view and see that the branch has moved on, but the tag we added is
      still on the previous commit.
      """.stripIndent)
    )
  }

  def step7 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 7. Detached HEAD state

      Let's check-out the tag we made earlier. 
    
      """.stripIndent),
      CheckoutTagCommand(),
      markedF("""
      Files are just files, so we can still edit files in the working tree.

      Tags aren't branches, however, so we'll now be in "detached HEAD state". 
      We can't commit any changes we make, because we're not on a branch to commit them to.
      (If you've been following the instructions, the commit command below is 
      probably disabled, even if you've added some changes to the index.)
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      commitCommand,
      markedF("""
      We're going to need to get out of Detached HEAD State before we can commit any
      changes, though (in step 8).
      """.stripIndent)
    )
  }

  def step8 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 8. Creating a branch

      Let's create a new branch. If we're in the detached HEAD state, this will
      take us out of it because we'll be on the new branch.

      (We could also just discard our changes and switch to an existing branch)
    
      """.stripIndent),
      BranchCommand(),
      markedF("""
      Creating a branch just creates it. Next we need to switch to our newly
      created branch.

      In this simulation, we've disabled switching to a different branch if you might 
      lose any uncommitted changes in the index or in the working tree. However, you 
      can safely switch to another branch that is pointing to the same commit. 
      (e.g. If you've just created a branch to commit your changes to.)

      So, you'll probably find that you can switch to your new branch, but can't change
      to `main` (because that would involve overwriting your uncommited changes)
      """.stripIndent),
      CheckoutBranchCommand(),
      markedF("""
      You should now be able to add and commit any changes you make again.
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      commitCommand,
      markedF("""
      Once you've committed your changes, explore the Graph view to see how the
      git graph is changing.
      """.stripIndent),
    )
  }

  def step9 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 9. Switching back to main

      If you've been following the instructions, you'll now have a new 
      branch that has a commit on it that `main` doesn't. 
      
      (If you haven't been following the instructions, don't worry &mdash;
      it's a simulation not a fixed progression. You can play with the
      commands and see what they do to the git graph, tree, and history.)

      Let's switch back to the `main` branch. 
      """.stripIndent),
      CheckoutBranchCommand(),
      markedF("""
      Now try making another commit on main
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      commitCommand,
    )
  }

  def step10 = for g <- prop.git yield {
    <.div(
      markedF("""
      ### 10. Discarding changes

      Suppose you've made some changes you don't want to commit.
      The `git restore` command lets us restore a file to a saved state.

      There are two versions of the command I'll show you

      First, try making some changes to some files and add them to the index.
      Then, to discard the changes in the index (restore the file in the 
      index to how it was in the most recent commit), use the `git restore --staged`
      command.
      """.stripIndent),
      addCommand(List("doubledactyl.txt")),
      addCommand(List("limerick.txt")),
      restoreStagedCommand(List("doubledactyl.txt")),
      restoreStagedCommand(List("limerick.txt")),
      markedF("""
      Next, try making some changes just in the working tree.
      Use the `git restore --worktree` command to discard those changes
      (and restore the file in the working tree to its state in the index).
      """.stripIndent),
      restoreWorktreeCommand(List("doubledactyl.txt")),
      restoreWorktreeCommand(List("limerick.txt")),
    )
  }

  def render = <.div(^.cls := CodeStyle.gitChallengeSplit.className,
    <.div(^.cls := "instructions", Challenge.textColumn(
      marked("""
      # A git simulation 

      In this first part of the tutorial, we're going to work with a simulation
      of git that's built into this site. It works in a very similar way as
      the real git, expect that it doesn't know about `.gitignore` files and 
      the hashes it comes up with for commits will have different numbers.

      On the right hand side of the page is a little in-built editor, for a 
      set of (simulated) files. In each step, you'll work with these files as
      a git repository. You can explore and edit the files at any time.

      The controls for git itself, however, will be in the text over here on the
      left. We're going to put some command-line commands that you'll be able to
      execute on the git repository by clicking on them.

      **Note**: This simulation just takes place in your browser's memory. 
      Nothing is really being saved. If you reload the page, everything will reset. 
      """.stripIndent),
      step1, step2, step3, step4, step5, step6, step7, step8, step9, step10

    )),
    <.div(
      EditSuite(tutorialTree)(prop),
    )
  )

}

lazy val localTutorial = Seq(
  Level("Simulation", Seq(
    GitSimEx(tutorialTree)(EditSuiteConfig(EditSuiteView.Tree(Seq("limerick.txt")), git=None))
  ))
)
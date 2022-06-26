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

class MarkdownStage(text:String) extends Challenge.Stage {

  override def completion: Challenge.Completion = Challenge.Open

  override def kind: String = "text"

  override protected def render = Challenge.textColumn(^.cls := CodeStyle.markdownSh.className, markedF(text))

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
  )),
  Level("Command-line Git", Seq(
    MarkdownStage("""
    ## 1. Initialising the repository

    Now that you've worked with a simulated git environment in the browser, it's time to try
    working with git itself on your own computer.

    If you haven't already got it installed, you can get it from [https://git-scm.com/](https://git-scm.com/).

    From a terminal, create a new directory.
    In that directory, using your favourite text editor, create two text files.
    You can use `doubledactyl.txt` and `limerick.txt` from the simulation if you're stuck for content to put in them.

    Next, initialise a repository using `git init`.

    If you look at the directory, you should now find a `.git` directory inside it. This contains git's
    internal content. On unix-like systems, because `.git` starts with a `.`, it'll be hidden by default. 
    So you'd need to use `ls -a` rather than just `ls` to see it.

    For example:

    <pre class="sh">
    ls -a
    </pre>
    <pre class="output">
    .  ..  .git  doubledactyl.txt  limerick.txt
    </pre>

    """.stripIndent),
    MarkdownStage("""
    ## 2. Configuring you

    In step 2 of our simulation, we configured our name and email address. Let's do that again.
    But first, let's have a look at *all* the configuration git has by default. Run

    <pre class="sh">
    git config -l
    </pre>

    You should see quite a few settings, including something like

    <pre class="output">
    user.name=A default name
    user.email=default@example.com
    </pre>

    Your computer may have tried to work out a default name and email address based on your user account and the computer's name.
    To update your name and email address

    <pre class="sh">
    git config author.name "Your Name"
    git config author.email "youremail@example.com"
    </pre>

    (Substituting in your name and email address, of course!)    
    """.stripIndent),
    MarkdownStage("""
    ## 3. Adding files to the index

    Now that we've initialised an empty repository, let's look at the *status* of the repository.

    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master

    No commits yet

    Untracked files:
      (use "git add <file>..." to include in what will be committed)
            doubledactyl.txt
            limerick.txt

    nothing added to commit but untracked files present (use "git add" to track)
    </pre>

    Let's add the files to the index:

    <pre class="sh">
    git add limerick.txt
    git add doubledactyl.txt
    </pre>

    And then let's try `git status` again

    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master

    No commits yet

    Changes to be committed:
      (use "git rm --cached <file>..." to unstage)
            new file:   doubledactyl.txt
            new file:   limerick.txt
    </pre>
    """.stripIndent),
    MarkdownStage("""
    ## 4. Making your first commit

    Now that there are some staged changes, we can commit them.

    **Remember:** If we do `git commit` without the `-m` flag to give it a message, 
    git will open the default text editor on our computer to let us enter a message.
    Often, this is [vi](https://www.cs.colostate.edu/helpdocs/vi.html)

    <pre class="sh">
    git commit -m "Initial commit"
    </pre>
    <pre class="output">
    [master (root-commit) b2655a3] Initial commit
    2 files changed, 30 insertions(+)
    create mode 100644 doubledactyl.txt
    create mode 100644 limerick.txt
    </pre>

    Let's have a look at our status now:

    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master
    nothing to commit, working tree clean
    </pre>

    And take a look to see what it put in the log. (Yours will be different.)

    <pre class="sh">
    git log
    </pre>
    <pre class="output">
    commit b2655a36e258a11fdd0538c70aef79b76a01deba (HEAD -> master)
    Author: William Billingsley &lt;wbillingsley@example.com&gt;
    Date:   Sun Jun 26 09:57:39 2022 +0000

    Initial commit
    </pre>
    """.stripIndent),
    MarkdownStage("""
    ## 5. Tagging a commit

    Let's create a tag for our commit.

    <pre class="sh">
    git tag first
    </pre>

    If we take a look at the log again, we'll see the commit has been tagged

    <pre class="sh">
    git log
    </pre>
    <pre class="output">
    commit b2655a36e258a11fdd0538c70aef79b76a01deba (HEAD -> master, tag: first)
    Author: William Billingsley &lt;wbillingsley@example.com&gt;
    Date:   Sun Jun 26 09:57:39 2022 +0000

    Initial commit
    </pre>
    """.stripIndent),
    MarkdownStage("""
    ## 6a. Make some more changes

    Make some more changes to the files (use your favourite text editor)
    
    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master
    Changes not staged for commit:
      (use "git add <file>..." to update what will be committed)
      (use "git restore <file>..." to discard changes in working directory)
            modified:   doubledactyl.txt
            modified:   limerick.txt

    no changes added to commit (use "git add" and/or "git commit -a")
    </pre>
    
    Use `git diff` to take a look at your changes. The content of your changes will depend on what you edited.
    (You might need to hit "space" to page through the output to get back to the command line.)

    <pre class="sh">
    git diff
    </pre>
    <pre class="output">
    diff --git a/doubledactyl.txt b/doubledactyl.txt
    index b7aefba..98ea29f 100644
    --- a/doubledactyl.txt
    +++ b/doubledactyl.txt
    @@ -1,4 +1,4 @@
    -A Double Dactyl by Will...
    +"Canine Graffiti" by Will
    
    Huppity puppity,
    All of a muckity!
    diff --git a/limerick.txt b/limerick.txt
    index 2d20741..4e73449 100644
    --- a/limerick.txt
    +++ b/limerick.txt
    @@ -1,4 +1,4 @@
    -A nonsense limerick by Will...
    +"The Well Dressed Frog" by Will
    
    There once was a frog (not a prince)
    Who's taken up etiquette since
    </pre>


    
    """.stripIndent),
    MarkdownStage("""
    ## 6b. Commit some more changes

    add the changes to the index,
    and commit them

    <pre class="sh">
    git add limerick.txt
    git add doubledactyl.txt
    git commit -m "Additional changes to demo branch moving on"
    </pre>
    <pre class="output">
    [master 06e0758] Additional changes to demo branch moving on
    2 files changed, 2 insertions(+), 2 deletions(-)
    </pre>

    If we take a look at the log again, we'll see the branch has moved on but the tag is still there.

    <pre class="sh">
    git log
    </pre>
    <pre class="output">
    commit 06e0758df4830ad4bc4042e5146b6730973f5dc6 (HEAD -> master)
    Author: William Billingsley &lt;wbillingsley@example.com>
    Date:   Sun Jun 26 10:17:14 2022 +0000

        Additional changes to demo branch moving on

    commit b2655a36e258a11fdd0538c70aef79b76a01deba (tag: first)
    Author: William Billingsley &lt;wbillingsley@example.com>
    Date:   Sun Jun 26 09:57:39 2022 +0000

        Initial commit
    </pre>
    """.stripIndent),
    MarkdownStage("""
    ## 7. Detached HEAD state

    Checking out the tag will put us into "detached HEAD state", as we'll have checked out
    a specific commit rather than a branch.

    <pre class="sh">
    git checkout first
    </pre>
    <pre class="output">
    Note: switching to 'first'.

    You are in 'detached HEAD' state. You can look around, make experimental
    changes and commit them, and you can discard any commits you make in this
    state without impacting any branches by switching back to a branch.

    If you want to create a new branch to retain commits you create, you may
    do so (now or later) by using -c with the switch command. Example:

      git switch -c <new-branch-name>

    Or undo this operation with:

      git switch -

    Turn off this advice by setting config variable advice.detachedHead to false

    HEAD is now at b2655a3 Initial commit
    </pre>

    In our simulation, we got out of this state by creating a new branch with `git branch`
    and switching to it with `git switch`.  
    The help text from git tells us we can do both steps
    in a single command using `git switch -c` (the `-c` is for `create`)

    <pre class="sh">
    git switch -c feature
    </pre>
    <pre class="output">
    Switched to a new branch 'feature'
    </pre>

    We're no longer in "detached HEAD state", we're on our new branch called "feature"
    """.stripIndent),
    MarkdownStage("""
    ## 8. Create a new commit on the feature branch

    Edit the files again and make a new commit

    <pre class="sh">
    git add limerick.txt
    git add doubledactly.txt
    git commit -m "Demonstrating making a change on the feature branch"
    </pre>
    <pre class="output">
    [feature a924a8e] Demonstrating making a change on the feature branch
    2 files changed, 6 insertions(+), 2 deletions(-)
    </pre>
    
    This new commit we've created is on the *feature* branch but isn't on the *master* branch.
    If we do `git log`, we'll see our new commit and the initial commit, but not the commit
    we made on master.

    <pre class="sh">
    git log
    </pre>
    <pre class="output">
    commit a924a8ee67503206c485d74433ac57c510213f25 (HEAD -> feature)
    Author: William Billingsley &lt;wbillingsley@example.com>
    Date:   Sun Jun 26 10:32:13 2022 +0000

        Demonstrating making a change on the feature branch

    commit b2655a36e258a11fdd0538c70aef79b76a01deba (tag: first)
    Author: William Billingsley &lt;wbillingsley@example.com>
    Date:   Sun Jun 26 09:57:39 2022 +0000

        Initial commit
    </pre>
    """.stripIndent),
    MarkdownStage("""
    ## 9. Switching back to master

    By default, git called the primary branch of our repository "master".
    Many modern repositories use "main" instead, but it'd make this first tutorial
    more complex, so we've left it as-is with git's default for now.

    Let's switch back to master and take a loog at the log

    <pre class="sh">
    git switch master
    git log
    </pre>
    <pre class="output">
    commit 06e0758df4830ad4bc4042e5146b6730973f5dc6 (HEAD -> master)
    Author: William Billingsley <wbillingsley@cantab.net>
    Date:   Sun Jun 26 10:17:14 2022 +0000

        Additional changes to demo branch moving on

    commit b2655a36e258a11fdd0538c70aef79b76a01deba (tag: first)
    Author: William Billingsley <wbillingsley@cantab.net>
    Date:   Sun Jun 26 09:57:39 2022 +0000

        Initial commit
    </pre>
    
    The changes we made earlier on the master branch are back, and we can't see
    the change we made on the feature branch.

    Take a look in the files in the directory to see they've changed to the version on master too.
    """.stripIndent),
    MarkdownStage("""
    ## 10a. Restoring changes in the index

    Make some edits to the files again, and save them. Then run `git status`

    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master
    Changes not staged for commit:
      (use "git add <file>..." to update what will be committed)
      (use "git restore <file>..." to discard changes in working directory)
            modified:   doubledactyl.txt
            modified:   limerick.txt

    no changes added to commit (use "git add" and/or "git commit -a")
    </pre>

    Add the changes to the index and check the status again. I've used the
    shortcut version `git add .` to add all the changes from the current directory.

    <pre class="sh">
    git add .
    git status
    </pre>
    <pre class="output">
    On branch master
    Changes to be committed:
      (use "git restore --staged <file>..." to unstage)
            modified:   doubledactyl.txt
            modified:   limerick.txt
    </pre>

    Now let's un-stage those changes using `git restore --staged`

    <pre class="sh">
    git restore --staged .
    git status
    </pre>
    <pre class="output">
    On branch master
    Changes not staged for commit:
      (use "git add <file>..." to update what will be committed)
      (use "git restore <file>..." to discard changes in working directory)
            modified:   doubledactyl.txt
            modified:   limerick.txt

    no changes added to commit (use "git add" and/or "git commit -a")
    </pre>
    """.stripIndent),
     MarkdownStage("""
    ## 10a. Restoring changes in the working tree

    Our changes have been un-staged by restoring the index, so they're now sitting in
    the working tree.

    <pre class="sh">
    git status
    </pre>
    <pre class="output">
    On branch master
    Changes not staged for commit:
      (use "git add <file>..." to update what will be committed)
      (use "git restore <file>..." to discard changes in working directory)
            modified:   doubledactyl.txt
            modified:   limerick.txt

    no changes added to commit (use "git add" and/or "git commit -a")
    </pre>

    Let's restore the files in the working tree to the ones in the index (throwing away our changes).

    <pre class="sh">
    git restore --worktree .
    git status
    </pre>
    <pre class="output">
    On branch master
    nothing to commit, working tree clean
    </pre>

    ## The End.

    This brings us to the end of the command-line git tutorial (for local repositories).
    You might want to open up the repository in a visual git client such as [GitHub Desktop](https://desktop.github.com/)
    or [SourceTree](https://www.sourcetreeapp.com/).
    You could also open the directory up in [Visual Studio Code](https://code.visualstudio.com/), which is an editor that
    has an in-built understanding of git.

    These visual clients will tend to make you more efficient working with git, but it is important still to understand
    the command-line commands.
    """.stripIndent),
  ))
)
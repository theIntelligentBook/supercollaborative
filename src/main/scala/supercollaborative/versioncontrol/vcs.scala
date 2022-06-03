package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

val moray = """Ye Hielan's an' ye Lowlan's
O, where have ye been?
They hae slain the Earl of Moray
And lain him on the green.
"""

val mondegreen = """Ye Hielan's an' ye Lowlan's
O, where have ye been?
They hae slain the Earl of Moray
And Lady Mondegreen.
"""

val sheridan = """Mrs. MALAPROP
But the point we would request of you is, that you will promise to forget 
this fellow -- to illiterate him, I say, quite from your memory.

LYDIA
O patience! -- Do, ma'am, for Heaven's sake! tell us what is the matter?

Mrs. MALAPROP
Why, murder's the matter! slaughter's the matter! killing's the
matter! -- but he can tell you the perpendiculars.
"""

val sheridanFixed = """Mrs. MALAPROP
But the point we would request of you is, that you will promise to forget 
this fellow -- to obliterate him, I say, quite from your memory.

LYDIA
O patience! -- Do, ma'am, for Heaven's sake! tell us what is the matter?

Mrs. MALAPROP
Why, murder's the matter! slaughter's the matter! killing's the
matter! -- but he can tell you the particulars.
"""

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

val example1 = File.Tree(Map(
  "moray.txt" -> File.TextFile(moray),
  "rivals.txt" -> File.TextFile(sheridanFixed)
))

val example2 = File.Tree(Map(
  "moray.txt" -> File.TextFile(mondegreen),
  "rivals.txt" -> File.TextFile(sheridan)
))

val gitExample = Git.init
  .addAll(example1)
  .commit("Algernon Moncrieff", "Initial revision", 1)
  .addAll(example2)
  .commit("Algernon Moncrieff", "Added some literary devices", 2)

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
    |Version control systems typically work with files. They don't know the files contain code.
    |
    |Suppose this is the directory we're putting under version control
    |
    |""".stripMargin),
    TreeViewer(example1, 300),
    Common.marked("""
    |Let's call this the *working tree*. This working tree is your files on disk that we're going to take snapshots of.
    |
    |Let's put it under version control
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## The initial revision
    |
    |With only one revision in the repository, it looks very much like we might expect
    |
    |""".stripMargin),
    BranchHistoryAndTree(
      Git.init
        .addAll(example1)
        .commit("Algernon Moncrieff", "Initial revision", 1).branches("main")
      , 300),
    Common.marked("""
    |Let's use a couple of literary devices as an example, rather than code:
    |
    |* A mondegreen is something misheard as something else.
    |* A malapropism is a comical use of an inappropriate word.
    |
    |So, let's introduce a new *version* of our files that introduces a mondegreen and a malapropopism.
    |""".stripMargin),
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A history of revisions
    |
    |With two revisions in our repository, we can start to see how the snapshots are different
    |depending on which revision we're looking at
    |
    |""".stripMargin),
    BranchHistoryAndTree(gitExample.branches("main"), 300),
    Common.marked("""
    |In the `history`, you can see that rather than give the versions numbers (1, 2, 3, etc) they have
    |a hexadecimal identifiers. This is called a `commit hash`. 
    |The hash is calculated from the *content* of the commit and its *parents*.
    |
    |If we want to look at the files in a particular commit, we can `checkout` that commit into our
    |working tree.
    |""".stripMargin),
  ))
  .markdownSlides("""
  |## The story so far...
  |
  |Some quick notes from what we've already seen
  |
  |* When we store a "commit" in version control, we're storing a snapshot of some files.
  |
  |* The commit has some metadata, including the author, the time, and a commit message.
  |  We try to write meaningful commit messages (not just "Changed some stuff")
  |
  |* The commits have *hashes* rather than sequential numbers. The *hashes* depend on the *contents*,
  |  so if we were to change a commit's content, it'd have a different hash and be a different commit.
  |
  |---
  |
  |## Setting up a git repository from scratch
  |
  |From a directory, at the command line:
  |
  |* `git init`
  |
  |This creates a subdirectory, `.git` in which git stores all its data.
  |
  |Next you should configure the repository so it know who you are
  |
  |* `git config user.name "Algernon Moncrieff"` (remembering to use *your* name!)
  |* `git config user.email "algernon@example.com"` (remembering to use *your* email address!)
  |
  |If you forget to set your name and email address, your metadata will be wrong in the commits.
  |
  |You can't change the metadata of past commits (after you've shared them) because youur commits would get new hashes (and therefore be different commits)
  |
  |---
  |
  |## The `status` command
  |
  |Whenever you want to know the status of your repository
  |
  |* `git status`
  |
  |You may find yourself using this command *a lot*.
  |
  |---
  |
  |## The `index`
  |
  |Git doesn't just add files or changes from your working tree blindly into the repository. It asks you to add the files to
  |its `index` first. This is like a staging area - telling git you'd like to stage this file, to be commited in the 
  |next commit
  |
  |* `git add myfile.txt`  
  |   will add a single file
  |* `git add .`   
  |   (note the dot) will add everything under the current directory
  |
  |Typically, after you've done a `git add`, do a `git status` to see what has been staged for commit
  |
  |---
  |
  |## Committing your changes
  |
  |To commit your changes, we use `git commit`
  |
  |**Beware:** This will open up the *default text editor on your computer* for you to enter a commit message.
  |Often, that editor is [vi](https://www.redhat.com/sysadmin/introduction-vi-editor), which has unusual controls!
  |
  |The commit will be completed when you save and exit the text editor. It will be aborted if you quit without saving.
  |
  |If you want to put a short summary message without opening the text editor
  |
  |* `git commit -m "Added some literary devices to the example text files"`
  |
  |Often, however, commits will have a short summary line followed by a blank line and then a fuller descripion. e.g.
  |
  |```
  |Added some literary devices to the example text files
  |
  |To demonstrate differences in files between revisions, this commit modifies the text files to add
  |literary devices to some of the lines:
  |moray.txt now contains the famous mondegreen, where the fourth line is misheard "And Lady Mondegreen".
  |malaprop.txt has been modified so that Mrs Malaprop's dialog contains the malapropisms that Sheridan wrote.
  |These are just toy examples, but they are useful to show revisions to text files simply.
  |```
  |
  |---
  |
  |## Seeing the revision history
  |
  |To look at the history of revisions to your files:
  |
  |* `git log`
  |
  |This will show the summary comment, hash, author and time of commits, going backwards in time.
  |""".stripMargin)
  .renderSlides
package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html.*
import com.wbillingsley.veautiful.templates.Challenge
import Challenge.Level
import supercollaborative.Common.*
import supercollaborative.versioncontrol.gitsim.*
import scala.collection.mutable

class VNodeStage(n: => VHtmlNode) extends Challenge.Stage {

  override def completion: Challenge.Completion = Challenge.Open

  override def kind: String = "text"

  override protected def render = Challenge.textColumn(n)

}

abstract class GitSimStage(title:String)(text: => String) extends Challenge.Stage {
  override def kind: String = "exercise"

  def challengeNodes:VHtmlNode

  override def render = Challenge.textColumn(
    twoColumn(title)(marked(text), <.div(challengeNodes))
  )
}

def twoColumn(title:String)(left: => VHtmlNode, right: => VHtmlNode):VNodeStage = {
  VNodeStage(
    <.div(
      <.h2(title),
      Challenge.split(<.div(^.attr("style") := "margin-right: 50px;", left))(right),
    )
  )
}

lazy val tutorialTree = MutableFile.Tree(mutable.Map(
  "doubledactyl.txt" -> MutableFile.TextFile("""
  A Double Dactyl by Will...

  Huppity puppity,
  All of a muckity!
  Muddy dog drawing his
  Nonsense in rhyme.

  Swishing his flickity,
  Wagging and whippety - 
  Mudochromatically,
  Tail drawing lines.
  """.stripIndent),
  "limerick.txt" -> MutableFile.TextFile("""
  A nonsense limerick by Will...

  There once was a frog (not a prince)
  Who's taken up etiquette since
  He heard at the races
  If he put on some graces
  They'd make him a knight of the plinth

  Sir Frog, with his tie in a bow
  Would tug it to greet his hello
  One winter so glum
  His fingers so numb
  He lost the damned thing in the snow

  Unfurnished with his attire
  He no longer looks like a squire
  Hopping around
  And scouring the ground
  He's searching all over the shire
  """.stripIndent),

))

var localTutorialGit:Option[Git] = None

lazy val localTutorial = Seq(
  Level("Simulation", Seq(
    new GitSimStage("A git simulation")(
      """
      In this first part of the tutorial, we're going to work with a simulation
      of git that's built into this site. It works in a very similar way as
      the real git, expect that it doesn't know about `.gitignore` files and 
      the hashes it comes up with for commits will have different numbers.

      On the right hand side of the page is a little in-built editor, for a 
      set of (simulated) files. In each step, you'll work with these files:
      turning them into a (simulated) git repository, making changes, and 
      switching branches.

      **Note**: In this simulation, the files aren't really saved anywhere. 
      They're just in memory. So if you reload the page, the changes will be gone. 

      On each page of the tutorial, we'll add some controls to get you to do a task.

      Right now, the files on the right are just files - it's not a git repository yet.
      So, it's not highlighting any changes because there's no repository.
      
      We need you to do `git init`.

      Once you've done so, you should notice the editor update, and it will think all
      the files are new. 
      """.stripIndent) {

      override def challengeNodes =       
        <.div(
          EditSuite(tutorialTree)(EditSuiteConfig(EditSuiteView.Tree("limerick.txt"))),

          <.div(
            <.span(^.cls := "console", "git init"),
            <.button(^.cls := "", "run command")
          )


        )

      override def completion = Challenge.Open
    }

    
  ))
)
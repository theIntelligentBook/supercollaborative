package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html.*
import com.wbillingsley.veautiful.templates.Challenge
import Challenge.Level
import supercollaborative.Common.*

class VNodeStage(n: => VHtmlNode) extends Challenge.Stage {

  override def completion: Challenge.Completion = Challenge.Open

  override def kind: String = "text"

  override protected def render = Challenge.textColumn(n)

}

def twoColumn(title:String)(left: => VHtmlNode, right: => VHtmlNode):VNodeStage = {
  VNodeStage(
    <.div(
      <.h2(title),
      Challenge.split(left)(right),
    )
  )
}

val localTutorial = Seq(
  Level("Simulation", Seq(
    twoColumn("A git simulation")(
      marked("""
      In this first part of the tutorial, we're going to work with a simulation
      of git that's built into this site. It works in a very similar way as
      the real git, expect that it doesn't know about `.gitignore` files and 
      the hashes it comes up with for commits will have different numbers.
      """.stripIndent), <.div()
    )

    
  ))
)
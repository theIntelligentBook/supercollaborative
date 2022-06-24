package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.doctacular._
import Medium._
import supercollaborative._
import site.given

val toc:site.Toc = site.Toc(
  "Intro" -> site.addPage("versioncontrol", intro),
  "Keeping history" -> site.add("vcs-history",
    Alternative("Slides", Deck(() => vcDeck))
  ),
  "Branches" -> site.add("vcs-branch",
    Alternative("Slides", Deck(() => branchDeck))
  ),
  "Diffs" -> site.add("vcs-diff",
    Alternative("Slides", Deck(() => diffsDeck))
  ),
  "Tutorial: Git locally" -> site.addChallenge("gitLocalTutorial", localTutorial),
  "Remotes" -> site.add("vcs-remote",
    Alternative("Slides", Deck(() => remotesDeck))
  ),
  "Merges" -> site.add("vcs-merge",
    Alternative("Slides", Deck(() => mergesDeck))
  )


)

val intro = <.div(
  Common.chapterHeading(1, "Version Control", ""),
  Common.marked("""
  |Lorem ipsum etc
  |""".stripMargin)
)
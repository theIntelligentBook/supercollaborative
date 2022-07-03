package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.versioncontrol.gitsim.MutableFile
import supercollaborative.versioncontrol.gitsim.CodeJarFileEditor

val hostsDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Git project hosting").withClass("center middle")
  .markdownSlides("""
  |## Git project hosting
  |
  |Services such as 
  |
  |* [GitHub](https://github.com)
  |* [GitLab](https://gitlab.com)
  |* [BitBucket](https://bitbucket.org) 
  |
  |are common places to host sourcecode repositories
  |
  |Alongside the source-code there is typically - 
  |
  |* An issue management system for bug reports and managing development tasks
  |* Some additional collaboration features (e.g. pull requests)
  |* A wiki for documentation
  |* Static hosting of websites (e.g. GitHub Pages) 
  |* Automated actions that can be triggered from pushing changes
  |
  |---
  |
  |## Issue management
  |
  |Each repository has an issue management system. This let's us describe tasks in terms of
  |
  |* Whether it's a bug report or a feature
  |* Prioritisation
  |* Scheduling (e.g. against milestones, or kanban-style tracking)
  |* Who it's assigned to
  |* Linking it to commits by including the commit hash in the close comment
  |
  |---
  |
  |## Wiki
  |
  |The wiki that goes with a project is typically
  |
  |* Written in [Markdown](https://github.github.com/gfm/) format
  |
  |* Kept as a git repository! (You can clone it and pull and push changes)
  |
  |Markdown also tends to be used for README files in the repository (README.md)
  |
  |More recently, GitHub and GitLab have started supporting diagrams in markup, using
  |mermaid.js (which we'll meet in a later week).
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    <.h2("Markdown demo"),
    {
      val textFile = MutableFile.TextFile("Some **Markdown** text to edit")

      val output = new VHtmlComponent {
        def render = <.div(^.attr("style") := "background: #fafafa; border-radius: 5px; padding: 5px;",
          Common.markedF(textFile.text)
        )
      }

      <.div(^.attr("style") := "display: grid; grid-template-columns: 1fr 1fr 1fr;",
        CodeJarFileEditor(textFile)({ _ => output.update(); None}),
        output      
      )
    }
  ))
  .markdownSlides("""
  |
  |## Pull requests
  |
  |In an open source project (or many corporate projects) the developer editing a feature might
  |not directly have commit rights into the repository. 
  |
  |Instead, they can fork the repository, create a branch to make their changes and *ask the development
  |team* to "pull" (merge) the branch into the repository.
  |
  |Typically, pull-requests are numbered (like issues) and provide an issue-like discussion stream so
  |that committers to the project can review and discuss the proposed change before merging it into the
  |repository.
  |
  |---
  |
  |## Actions
  |
  |Sometimes we'd like an action to be triggered by a push to the repository. E.g.
  |
  |* Building and publishing a documentation website
  |
  |* Running tests and packaging a release
  |
  |These can often be triggered to run in the cloud, for instance on GitHub Actions or via a third party
  |workflow service.
  |
  |
  |---
  |
  |## GitHub Pages
  |
  |GitHub Pages was developed for hosting project documentation as websites. 
  |
  |A typical workflow was
  |
  |1. Create an *orphan* branch called `gh-pages` in the repository  
  |   `git checkout --orphan gh-pages`
  |
  |   The branch is an *orphan* because it has no (common) parent commit with the rest of the code.
  |
  |2. Put the documentation website in the orphan branch,
  |
  |3. Push it to GitHub
  |
  |This can be combined with GitHub Actions. For example, a site can be built from its
  |source code in a GitHub Action and the resulting output automatically pushed to the gh-pages branch.
  |
  |
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
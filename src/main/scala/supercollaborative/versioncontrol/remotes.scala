package supercollaborative.versioncontrol

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

import gitsim._

def blockLabel(s:String) = <.div(^.cls := CodeStyle.blockLabel.className,
  <("label")(^.cls := "block-label", s)
)

val remoteStart = branchExample.commit("Will", "E", 5)

val remoteShort = Git.init
  .commit("Will", "1", 1)
  .commit("Will", "2", 2)
  .tag("release1.0")


val localStart = Git.init.addRemote("origin", "git@example.com/example/example.git")

val remotesDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Version Control &mdash; Remotes").withClass("center middle")
  .markdownSlides("""
  |## Yours is not the only repository
  |
  |So far, we've talked about git as if you are the only developer and yours is the
  |only repository. That's not usually the case. Even if you are working on a project alone,
  |you will usually want to "push" your repository to a remote location, e.g. GitHub
  |or GitLab
  |
  |Usually, there will also be other developers on your team, each of whom also has 
  |a copy of the repository.
  |
  |Somehow we have to be able to *clone* repositories, and to *fetch*, *pull*, and *push* changes
  |
  |---
  |
  |## `git remote`
  |
  |To view a list of remotes in your repository
  |
  |```sh
  |git remote -v
  |```
  |
  |Each remote has a *name* and a *URL*, e.g.
  |
  |```sh
  |origin	https://github.com/wbillingsley/handy.git (fetch)
  |origin	https://github.com/wbillingsley/handy.git (push)
  |```
  |
  |"Origin" is just git's default name for a remote repository. We can call them anything we like.
  |
  |If you've only just created a repository (via `git init`) there won't be any remotes configured.
  |
  |---
  |
  |## git urls
  |
  |Remotes set up git as a server to handle incoming requests. As users, we won't 
  |normally need to do that - we just use git locally (on our repo) and as a client
  |(for pulling and pushing changes from the server).
  |
  |Generally, there are two different kinds of git url you're likely to see. 
  |
  |* git\@example.com:path/to/repo.git  
  |  This uses SSH for communication. It's passwordless and uses a public/private key pair
  |  for authentication. This is very useful if you're working from a linux or unix-like system,
  |  but you have to create a public/private key pair and give the public key to the server.
  |
  |* https\://example.com/path/to/repo.git  
  |  This uses HTTPS for communication. Authentication comes in two flavours:
  |
  |  * HTTPS Basic authentication. Traditionally, git would ask your username and password from
  |    the command line and send it to the server using HTTPS basic authentication.
  |    Some major git hosts (e.g. GitHub) have stopped supporting this because it doesn't support
  |    multi-factor authentication and other modern security features.
  |
  |  * Token-based authentication via a bearer token kept in a credential manager.
  |    If you install one of the major git desktop apps (e.g. GitHub Desktop), it will also
  |    install a credential manager. Log in to the desktop app, and it'll use a helper to ensure
  |    that git has your authentication token when you run git fetch and push commands.
  |
  |---
  |
  |## `git remote add`
  |
  |To add a new remote
  |
  |```sh
  |git remote add name url
  |```
  |
  |e.g.
  |
  |```sh
  |git remote add origin https://github.com/UNEsestudio/a-blank-repo.git
  |```
  |
  |or
  |
  |```sh
  |git remote add gitlab https://gitlab.com/unecosc220/a-blank-repo.git
  |```
  |
  |If you create a new project on GitHub or GitLab, the empty project's home page will
  |give you instructions for adding the remote
  |
  |""".stripMargin)
  .veautifulSlide(<.div(
    Common.marked("""
    |## A git reminder
    |
    |It's useful to remind ourselves of a couple of things about git's inner workings
    | 
    |""".stripMargin),
    <.p(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    ))),
    Common.marked("""
    |
    |Internally, git is storing:
    |
    |* Some number of *objects* (e.g., files and directories, in all their versions)
    |
    |* Some number of *refs* (tags and branches)
    |
    |A remote repository is just another repository. 
    |
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Let's start from empty
    |
    |Suppose we have a remote repository with some commits and an empty local repository
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = localStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    |As we haven't done any commits on the local repo, I've shown it pointing to the "empty commit".
    |
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Let's fetch from the remote
    |
    |If we `git fetch` from the remote, we fetch all its objects and branches.
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = localStart.fetch("origin", remoteStart)
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    |Git has downloaded all the objects (so we can checkout any of the remote's versions)  
    |It's also set up **remote tracking branches** for each of the remote's branches.  
    |Our local branches and HEAD have not changed or moved.
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Let's merge `origin/main` into `main`
    |
    |To bring the changes from `origin/main` into our local `main` branch, we can `git merge origin/main`
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = localStart.fetch("origin", remoteStart).fastForwardMerge("origin", "main")
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    |Git merge tries to merge two branch's histories. If the branch we're merging into (here, `main`)
    |is pointing at one of the ancestors of the branch we're merging from, this is a *fast-foward merge*
    |and just moves the branch pointer.
    |
    |The empty commit is considered an ancestor of all the other commits (even though I don't draw the arrow for it)
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## `git pull` does a fetch and a merge
    |
    |Let's go back to our empty repository
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = localStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    |`git pull` would try to do that `git fetch` and `git merge` in one command
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## After git pull
    |
    |After git has done the fetch and merge, we're at this state again:
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteStart
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = localStart.fetch("origin", remoteStart).fastForwardMerge("origin", "main")
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## A developer story...
    |
    |Let's suppose we're not the only ones working on our repository. Let's start with only two commits in the remote.
    | 
    |""".stripMargin),
    <.p(blockLabel("remote")(hscrollBox(SelectableHDAG({
        val graph = remoteShort
        graph.refs.toSeq 
      }
    )))),
    <.p(blockLabel("local")(hscrollBox(SelectableHDAG({
        val graph = Git.init
      
        graph.refs.toSeq :+ graph.headAsDetached
      }
    )))),
    Common.marked("""
    |
    |And an empty local repository
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Pull the two commits
    |
    |Let's pull (fetch + merge) the two commits into our local repostory.
    | 
    |""".stripMargin),
    {
      val remote = remoteShort
      val local = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remote).fastForwardMerge("origin", "main")

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |
    |Next, we're going to make a local commit
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## We made a local commit
    |
    |Now our repository has an extra commit that the remote doesn't
    | 
    |""".stripMargin),
    {
      val remote = remoteShort
      val local = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remote).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |
    |We can *push* this extra commit to the remote repository
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Git push
    |
    |To push our changes, we'd use `git push origin main`
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local, remote) = local0.pushBranch("origin", "main", remoteShort)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |
    |We can only push if the remote branch can be *fast-forwarded*. Otherwise it will reject our push.
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Another local commit
    |
    |Let's do another local commit.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote) = local0.pushBranch("origin", "main", remoteShort)
      val local  = local1.commit("Will", "4", 4)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
    Common.marked("""
    |
    |Now we're ahead of `origin/main` by a commit again
    """.stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Someone else pushes a commit!
    |
    |We're not the only developer. Suppose someone else pushes to origin.
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
        Common.marked(s"""
        |
        |Now our local repository and the remote have diverged. Note that our repository does not yet even
        |know the remote repository has commit `${remote.head.commit.hash}`.
        |
        |`origin/main` is `${local.remoteBranches("origin" -> "main").commit.hash}`  
        |`origin`'s `main` is `${remote.head.commit.hash}`
        """.stripMargin)
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## Let's fetch the repository
    |
    |If we do a `git fetch`, we can see our `main` has diverged from origin's. Despite only using `main`, our history has branched.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val remote = remote1.commit("Algernon", "5", 5)
      val local  = local1.commit("Will", "4", 4).fetch("origin", remote)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        )))),
        Common.marked(s"""
        |
        |We would not be able to do a `git push` because the remote's `main` cannot be fast-forwarded to match our `main`
        """.stripMargin)
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## `git merge origin/main`
    |
    |If merge origin/main into our main, it'll be a non-fast-forward merge. This will create a new commit with two parents.
    | 
    |""".stripMargin),
    {
      val local0 = Git.init.addRemote("origin", "git@example.com:example/example.git")
          .fetch("origin", remoteShort).fastForwardMerge("origin", "main").commit("Will", "3", 3)

      val (local1, remote1) = local0.pushBranch("origin", "main", remoteShort)
      val remote = remote1.commit("Algernon", "5", 5)
      val local  = local1.commit("Will", "4", 4).fetch("origin", remote).nonFFMerge("Will", ("origin" -> "main"), 6)

      Seq(
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        )))),
        Common.marked(s"""
        |
        |However, now our `main` is strictly ahead of `origin`'s `main` so we can push again
        """.stripMargin)
      )
    },
  ))
  .veautifulSlide(<.div(
    Common.marked("""
    |## After `git push origin main`
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
        <.p(blockLabel("remote")(hscrollBox(SelectableHDAG(
          remote.refs.toSeq 
        )))),
        <.p(blockLabel("local")(hscrollBox(SelectableHDAG(
          local.refs.toSeq :+ local.headAsDetached
        ))))
      )
    },
  ))
  .markdownSlides("""
  |## The story so far
  |
  |Even when we think we're just working on `main`, we're still working with branches because
  |the remote's `main` and other developers' `main` branch are different from ours.
  |
  |We fetch, merge, and push from these other (remote) branches on the repositories
  |
  |This can also make our local git graph look more complicated. It's not linear. But if we
  |ask for a log, it'll traverse all the ancestors commits of our branch in (roughly) date order.
  |
  |---
  |
  |## A typical way of working
  |
  |When we're doing development work, this means sharing our changes can be a multi-step process
  |even if we're only working on the main branch
  |
  |1. `git fetch` to get any changes that have been made on the remote repository
  |
  |2. `git merge origin/branchname` to pull any of those remote changes into our current branch
  |
  |3. `git push origin branchname` to share our changes back to the remote server.
  |
  |Sometimes we'll want to work on branches other than `main`. We must not share broken work
  |to `origin`'s `main` branch (or we'll break everyone else's build). So sometimes we'll want to
  |work on a different branch.
  |
  |That still works the same way, it's just more branch pointers in the repository.
  |
  |---
  |
  |## Those non-fast forward merges
  |
  |So far, I haven't told you *how* git merges any changes. Or that merges sometimes *fail*.
  |
  |The mechanics of merging different people's changes to files will be in another deck!
  |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
package supercollaborative.containers

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.Common
import supercollaborative.templates.Animator
import org.scalajs.dom

val devcontainersDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Developer Containers").withClass("center middle")
  .markdownSlides("""
    |# Development containers
    |
    |Development containers let you do your development inside a Docker container
    |
    |* Avoid installing (too many) development tools individually in your local operating system
    |
    |* Develop in an environment closer to production (e.g. in Linux even though you're working on Mac/Windows)
    |
    |* Start a disposable development container in the cloud and connect to it remotely (GitHub CodeSpaces)
    |
    |They are particularly used with Visual Studio Code - either:
    |
    |* A browser-hosted instance of Visual Studio Code connected to a container in the cloud, using GitHub CodeSpaces, or
    |
    |* Visual Studio Code on your own computer (with the "Remote - Containers" add-on) connected to a Docker container on your computer
    |
    |---
    |
    |## Defining a development container
    |
    |A repository can contain a `.devcontainer` directory. This would hold:
    |
    |* `devcontainer.json` providing configuration (e.g. Visual Studio Code plugins to install)
    |
    |* Usually, a `Dockerfile` defining the container image to start from
    |
    |---
    |
    |## Starting a developer container
    |
    |You could build and start a developer container manually, but really the idea is to get Visual Studio Code or GitHub Codespaces to
    |do it for you.
    |
    |On Visual Studio Code
    |
    |* Install the "Remote &amp; Containers" plugin
    |
    |* Open a directory containing a `.devcontainer` directory.
    |
    |Visual Studio Code should prompt you if you want to "Reopen in Container"
    |
    |This will build and start the container for you automatically. It'll stop the container when you exit Visual Studio Code, and restart it
    |when you reopen the project.
    |
    |At any time, you can ask Visual Studio Code to "Rebuild Container" from the green menu in the bottom-left
    |
    |If you go to **Terminal** -> **New Terminal**, Visual Studio Code will open a terminal *inside* your development container
    |
    |---
    |
    |## Ports
    |
    |When you start a container, by default all its "ports" are hidden. 
    |
    |A "port" is a number on your computer so that network requests can be sent to the right program. e.g. Most people start web servers on port `80`
    |in production and `8080` in development.
    |
    |You can "forward" a port from your container to a port on your computer. This lets you start a web server on your container and connect to it 
    |from your computer.
    |
    |Typically, if you start a program (e.g. a webserver) from your container in VS Code (with the Remote Containers plugin), it will automatically
    |forward the port after a while. But you can also do it manually from the "ports" tab.
    |
    |---
    |
    |## Bind mounts
    |
    |Although we're running a container for development, we're running Visual Studio Code on our own computer.
    |
    |This means we're editing the source files natively on our own computer and saving them on our computer (outside the container). 
    |So how can the compiler (in the container) find them?
    |
    |Docker "bind mounts" are a way of telling Docker to synchronise files from a folder on your computer into a folder in the container.
    |That way, you can edit the files from VS Code (on Windows or Mac), but your compiler can see your updated files from the terminal in the container.
    |
    |This slide is just telling you what's happening - if you're using a devcontainer, Visual Studio Code will set up the bind mount for you.
    |
    |---
    |
    |## Personal experience
    |
    |In computing, I find there are sometimes tradeoffs in terms of set-up and fragility later.
    |
    |* If I set up development tools natively on my Mac, it's relatively easy. But next year, I'll have forgotten what I installed and where I installed it.
    |  Sometimes I'll have installed something twice accidentally, and won't know which I'm using when it comes time to update things.
    |
    |* If I work in a devcontainer, it is often a much fiddlier process getting it set up. There are a lot of moving parts between Docker, what I've installed
    |  in my container, my Mac machine at home, my Windows machine at the office, and what I'm trying to run. But if it all goes wrong, I can blow away the
    |  container - it's all just built from a Dockerfile.
    |
    |Consequently, I'd advice using VS Code and Docker devcontainers only for reasonably advanced students at the moment. When you're studying a programming subject, 
    |you're not using the development environment for long (only a few months). Devcontainers are more useful if you have several different projects you're
    |working on on your computer and you need to switch between them over a period of time.
    |
    |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
package supercollaborative.containers

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.{Common, MermaidDiagram}
import supercollaborative.templates.Animator
import org.scalajs.dom

val vmDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Virtual Machines").withClass("center middle")
  .markdownSlides("""
    |## Tech humour...
    |
    |> ![There is no cloud](https://fsfe.org/contribute/promopics/thereisnocloud-bw-preview.png)
    |> "There is no cloud, just other people's computers" &mdash; anon.
    |
    |To an extent this is true - anything we run "in the cloud" is running on a machine in someone else's data centre.
    |
    |But there are some genuine differences...
    |
    |""".stripMargin)
  .veautifulSlide(<.div(
    Common.markedF("""
      |## Let's break down a machine
      |
      |Suppose we could break a computer up into some component parts:
      |
      |""".stripMargin),
    <.p(MermaidDiagram("""
    |graph TD
    |
    |  subgraph "Computer"
    |
    |    subgraph "CPU"
    |    Core1
    |    Core2
    |    Core3
    |    Core4
    |    end
    |
    |    Memory
    |    Storage
    |    NI("Network Interface")
    |
    |  end
    |
    |  Network --- NI
    |""".stripMargin)),
  Common.markedF("""
    |Now suppose we could configure each of those *separately*, and have a software-defined computer
    |
    |Each part can be scaled separately.  
    |
    |""".stripMargin)
))
.markdownSlides("""
    |## Cloud virtual machines support "scale out"
    |
    |* Two scaling strategies:
    |
    |  - **scale-up**: buy a bigger server
    |
    |  - **scale-out**: buy more servers
    |
    |* EdX "Sunday night problem"
    |
    |  - Lots of courses with assignments due Sun 23:59
    |
    |  - Tens of thousands of users submitting Sun 23:00 -- 23:59
    |
    |  - Very few users submitting Mon 07:00 -- 08:00
    |
    |* If you co-tenant internally, you're likely to have correlated load  
    |  If you co-tenant in the cloud, you're probably with dissimilar services
    |
    |---
    |
    |## An example of a short-lived computer
    |
    |In the last chapter, we saw how GitHub Actions can run an action in the cloud.
    |
    |If we look at the log, the computer only existed for [57 seconds](https://github.com/theIntelligentBook/supercollaborative/actions/runs/2603905811)
    |
    |If we look at the past logs, over a month, computers to run this job have existed for less than half an hour in total.
    |
    |Clearly, it is more efficient for this to be run in GitHub's cloud virtual machines (where I just borrow their computer for a minute at a time when I need it) 
    |than having a dedicated server that would sit idle for most of its life.
    |
    |---
    |
    |## Infrastructure as a Service (IaaS)
    |
    |IaaS lets you start computing, storage, and networking resources on-demand, usually billed per minute.
    |
    |* Amazon AWS
    |* Microsoft Azure
    |* Google Cloud
    |
    |Usually offer APIs so you can start servers programmatically (scale out) or automatic load scaling
    |
    |---
    |
    |## Virtual Machine
    |
    |An emulation of a physical computer. This lets us:
    |
    |* Run many virtual machines on a single physical machine. 
    |
    |* Run one operating system on top of another. e.g. emulate Android on your PC
    |
    |* Provide an abstraction layer, programming different machines as if they were the same. *eg, the Java Virtual Machine*
    |
    |---
    |
    |## Hardware-assisted Virtualisation
    |
    |* First introduced in 1972; popular since around the mid-2000s.
    |
    |* Machine's hardware provides support for running multiple guest operating systems in isolation
    |
    |* Typically how IaaS solutions work
    |
    |IaaS gives you a lot of flexibility: eg, stop an instance, but keep the disk. Re-start it later attached to a different physical CPU
    |
    |---
    |
    |## Starting a hardware-assisted Virtual Machine on your own computer
    |
    |* [VirtualBox](https://www.virtualbox.org/) lets you run a virtual machine on your own computer. 
    |
    |---
    |
    |### Using VMs for development and testing
    |
    |A problem programmers can encounter is that they write a program and it works on their own computer, but then it fails in production.
    |
    |* Differences in operating system (e.g. Windows vs Linux)
    |
    |* Differences in installed software (e.g. )
    |
    |One way to help minimise the differences between *development* and *production* is if the developer runs the code using a virtual
    |machine with the same operating system, etc., as the production environment.
    |
    |---
    |
    |### Vagrant
    |
    |[Vagrant](https://www.vagrantup.com/) Lets you script virtual machines locally
    |
    |* Define a `vagrantfile` to define your environment  
    |  (We won't detail the contents of a vagrantfile, but it's a set of steps to install the programs you need on a base operating system)
    |
    |* Run `vagrant up` to start a virtual machine in VirtualBox
    |
    |This has commonly been used by developers to have their running environment match (somewhat) the production environment
    |
    |But time has moved on and now there's another technology you should know about...
    |
    |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides


package supercollaborative.containers

import com.wbillingsley.veautiful.html._
import com.wbillingsley.veautiful.templates.DeckBuilder
import supercollaborative.given
import supercollaborative.{Common, MermaidDiagram}
import supercollaborative.templates.Animator
import org.scalajs.dom

val containersDeck = DeckBuilder(1920, 1080)
  .markdownSlide("# Containers").withClass("center middle")
  .veautifulSlide(<.div(
    Common.markedF("""
      |## An awkwardness with Virtual Machines
      |
      |Suppose we have one physical computer running 100 virtual machines
      |
      |Those 100 virtual machines are running Ubuntu Linux and various combinations of Node.js, a Java Virtual Machine, and PostgreSQL
      |
      |""".stripMargin),
    <.p(MermaidDiagram("""
      |graph TD
      |
      |  subgraph "Server 1"
      |  App1("Applications") 
      |  JVM1("JVM")
      |  App1 --- JVM1
      |  JVM1 --- U1("Ubuntu")
      |  end
      |  subgraph "Server 2"
      |    App2("Applications")
      |    App2 --- JVM2("JVM") --- U2("Ubuntu")
      |  end
      |  subgraph "Server 3"
      |    App3("Applications")
      |    App3 --- Node3("Node.js") --- U3("Ubuntu")
      |  end
      |  subgraph "Server 4"
      |    App4("Applications")
      |    App4 --- Node4("Node.js") --- U4("Ubuntu")
      |  end
      |  subgraph "Server 5"
      |    App5("Applications")
      |    App5 --- PS5("PostgreSQL") --- U5("Ubuntu")
      |  end
      |  subgraph "Server 6"
      |    App6("Applications")
      |    App6 --- Node6("Node.js") --- U6("Ubuntu")
      |    App6 --- PS6("PostgreSQL") --- U6
      |  end
      |
      |""".stripMargin)),
  Common.markedF("""
    |Q: How many *copies* of Ubuntu Linux are we storing on that physical computer?
    |
    |Using just "virtual machines" with "hardware-assisted virtualisation", the answer might be *100*.
    |
    |At 30MB per Ubuntu image, that may be GigaBytes of extra storage you need, just to keep duplicate copies of the same operating system
    |
    |""".stripMargin)
  ))
  .veautifulSlide(<.div(
    Common.markedF("""
      |## Operating-system level Virtualisation
      |
      |What if we didn't really have a whole virtual machine, we just pretended we did?
      |
      |* Operating system is shared between the host and the "guest" instances, but made to look separate to the guests
      |
      |* Now we only need 1 copy of Ubuntu Linux for all 100 virtual servers.
      |
      |""".stripMargin),
    <.p(MermaidDiagram("""
      |graph TD
      |
      |  subgraph "OS level virtualisation"
      |    U("Ubuntu")
      |  end
      |
      |  subgraph "Server 1"
      |  App1("Applications") 
      |  JVM1("JVM")
      |  App1 --- JVM1
      |  JVM1 --- U
      |  end
      |  subgraph "Server 2"
      |    App2("Applications")
      |    App2 --- JVM2("JVM") --- U
      |  end
      |  subgraph "Server 3"
      |    App3("Applications")
      |    App3 --- Node3("Node.js") --- U
      |  end
      |  subgraph "Server 4"
      |    App4("Applications")
      |    App4 --- Node4("Node.js") --- U
      |  end
      |  subgraph "Server 5"
      |    App5("Applications")
      |    App5 --- PS5("PostgreSQL") --- U
      |  end
      |  subgraph "Server 6"
      |    App6("Applications")
      |    App6 --- Node6("Node.js") --- U
      |    App6 --- PS6("PostgreSQL") --- U
      |  end
      |
      |""".stripMargin)),
    Common.markedF("""
      |But we've still got a lot of identical copies of the JVM, Node.js and PostgreSQL
      |
      |We always want each of our servers to run its own *processes* (they're running different Java programs on their JVMs)
      |but can they at least share the files of the JVM itself? (`/usr/bin/java`, etc)?
      |
      |""".stripMargin)  
  ))
  .veautifulSlide(<.div(
    Common.markedF("""
      |## Containers
      |
      |Let's take that further. Let's define our file system using *layers* that let us share *any common underlying layers* between containers
      |
      |""".stripMargin),
    <.p(MermaidDiagram("""
      |graph TD
      |
      |  subgraph "OS layer"
      |    U("Ubuntu")
      |  end
      |
      |  subgraph "File system overlay layer"
      |    JVM("/usr/bin/java") --- U
      |    Node("/usr/bin/node") --- U
      |    PS("/usr/bin/pssql") --- U
      |  end
      |
      |  subgraph "Container 1"
      |  App1("Application 1") --- JVM 
      |  end
      |  subgraph "Container 2"
      |    App2("Applications") --- JVM
      |  end
      |  subgraph "Container 3"
      |    App3("Applications") --- Node
      |  end
      |  subgraph "Container 4"
      |    App4("Applications") --- Node
      |  end
      |  subgraph "Container 5"
      |    App5("Applications") --- PS
      |  end
      |  subgraph "Container 6"
      |    App6("Applications") --- Node
      |    App6 --- PS
      |  end
      |
      |""".stripMargin)),
    Common.markedF("""
      |Internally, each container thinks it is just loading its own files.
      |
      |Although each container will run its own Java *process*, at least they can share the *copy of Java on disk*.
      |This lets the amount of file storage we're dedicating to our images be much smaller.
      |
      |""".stripMargin),
  ))
  .markdownSlides("""    
    |## Docker
    |
    |[Docker](https://www.docker.com/) is software that helps you to construct, run, and deploy containers.
    |
    |If you're a developer, you might find this saves you space compared to using virtual machines. 
    |
    |A bit of terminology:
    |
    |* A **container image** is like a snapshot of a file system. When you first create a container
    |  from the image, this will be what's on its disk.
    |
    |* From a container image, you can start one or more **containers** (running machines)
    |
    |---
    |
    |## Docker Hub
    |
    |Docker Hub contains a collection of pre-built images that you can
    |
    |* Start containers from or,
    |
    |* Further refine to build your own container image
    |
    |This makes it relatively easy and efficient to start containers on your computer.
    |
    |But there are a few wrinkles...
    |
    |---
    |
    |## Processor Architectures
    |
    |Computers don't all use the same processors. e.g.:
    |
    |* My Mac Mini at home has an `ARM64` Apple M1 processor
    |
    |* My desktop at work has an `AMD64` (x86) Intel Core i7 processor
    |
    |Container images need to have been published for the *processor architecture* you want to run them on.
    |(Running an ARM image on an Intel x86 processor won't work).
    |
    |Many (but not all) images on Docker Hub have been published for *both* architectures
    |
    |---
    |
    |## Dockerfiles 
    |
    |A Docker container image is usually built from a `Dockerfile`. 
    |This is like its recipe - what to start from, followed by the steps to perform to make the image.
    |
    |The steps are usually `RUN` steps, executing commands on the machine. 
    |They can be *very* particular to the base image you're working with.
    |In the example below, `apt-get` is a particular command within Ubuntu Linux for installing software packages.
    |Different images would use different commands.
    |
    |```dockerfile
    |FROM openjdk:17-jdk-slim-bullseye
    |
    |# Install some basic development utilities
    |RUN apt-get update && apt-get install -y \ 
    |  curl git python3 zip 
    |  && rm -rf /var/lib/apt/lists/* \
    |  && curl -fsSL https://deb.nodesource.com/setup_17.x | bash \
    |  && apt-get install -y nodejs  
    |
    |# Use the Java-based launcher as Alpine Linux and Graal native compiled images of CS don't like
    |# Alpine Linux (due to musl instead of gcc)
    |RUN bash -c 'curl -fLo /usr/bin/cs https://git.io/coursier-cli' && \
    |    chmod u+x /usr/bin/cs
    |```
    |
    |---
    |
    |## Dockerfiles are *just* a recipe
    |
    |Dockerfiles just take the image you asked for, and run the commands you tell it to. 
    |They don't have any in-built knowledge about what programs do or don't work with what.
    |
    |e.g. 
    |
    |* If you try to run `apt-get` on a Fedora Linux image, it won't work. `apt-get` is a program
    |  on Ubuntu, not Fedora
    |
    |There can be more obscure differences between images - 
    |
    |Sometimes you'll get caught out where a program you want to install assumes a program or a library is present on the machine, 
    |but it's not present in all versions of Linux.
    |
    |Notably, "Alpine Linux" images are very small Linux images designed for Docker, but don't have a the `glibc` libraries that a lot
    |of programs just expect to be there (because it's on almost every other Linux iamge). 
    |
    |---
    |
    |## Build an image: `docker build`
    |
    |With Docker Desktop installed, from a directory containing a `Dockerfile`, 
    |
    |```sh
    |docker build . -t my_image_name
    |```
    |
    |`my_image_name` is a tag to give to the built image.
    |
    |---
    |
    |## Create a container from an image: `docker create`
    |
    |If we wanted to create a container (but not start it), we could say
    |
    |```
    |docker create my_image_name --name my_container_name
    |```
    |
    |We've then created a container (a potentially startable machine) from an image
    |
    |---
    |
    |## Start a container you've created: `docker start`
    |
    |To start our container, we could say
    |
    |```sh
    |docker start my_container_name -i
    |```
    |
    |This starts the container machine. 
    |
    |The `-i` flag I put at the end is for "interactive". After the container is started, you'll be connected to a terminal on the machine
    |
    |---
    |
    |## Stop a container: `docker stop`
    |
    |Reasonably self-explanatory:
    |
    |```sh
    |docker stop my_container_name
    |```
    |
    |---
    |
    |## Create and start a new container in one command: `docker run`
    |
    |To create a container from an image and start it in a single command, use `run`
    |
    |```sh
    |docker run my_image_name --name my_container_name -it
    |```
    |
    |This creates a container from an image and starts it
    |
    |**Note:** If you stop the container and want to restart the *same* container (rather than create a fresh one from the same image), 
    |use `docker start` (you've alread created it).
    |
    |---
    |
    |## Execute a command on a running container: `docker exec`
    |
    |If you've started a container, you can ask Docker to connect to it and execute a command in that container
    |
    |e.g. if you have an Ubuntu container running, and want to connect to a terminal in it, you could run
    |
    |```sh
    |docker exec my_container_name -ti bash
    |```
    |
    |---
    |
    |## Basics of a Dockerfile
    |
    |There's just two parts of that Dockerfile we'll worry about:
    |
    |* A base image to build from. This (pre-built) base image will often be downloaded from [Docker Hub](https://hub.docker.com/).
    |
    |  ```Dockerfile
    |  FROM openjdk:17-jdk-slim-bullseye
    |  ```
    |
    |* Commands that we `RUN` on the server.
    |
    |  ```Dockerfile
    |  RUN apt-get update && apt-get install -y \ 
    |    curl git python3 zip 
    |    && rm -rf /var/lib/apt/lists/* \
    |    && curl -fsSL https://deb.nodesource.com/setup_17.x | bash \
    |    && apt-get install -y nodejs 
    |  ```
    |
    |After each `RUN` command, Docker will save a new file system *layer*.
    |
    |---
    |
    |## Layered filing system
    |
    |To save space, Docker uses a layered filing system. Each layer (except the very last one) is *read-only*.
    |
    |So, if two Dockerfiles start
    |
    |```dockerfile
    |FROM openjdk:17-jdk-slim-bullseye
    |
    |# Install some basic development utilities
    |RUN apt-get update && apt-get install -y \ 
    |  curl git python3 zip 
    |  && rm -rf /var/lib/apt/lists/* \
    |  && curl -fsSL https://deb.nodesource.com/setup_17.x | bash \
    |  && apt-get install -y nodejs  
    |```
    |
    |Then **both** images will share the layers from those steps. 
    |
    |---
    |
    |### Docker compose
    |
    |* Docker lets us start a container, but we may want to start more than one. e.g.:
    |  
    |  - A container containing our webserver
    |
    |  - A container containing our database
    |
    |* We can define a `docker-compose.yml` file describing the various containers that our application needs.
    |
    |  ```yml
    |  services:
    |    web:
    |      image: awesome/webapp
    |      ports:
    |        - "8000:5000"
    |      links:
    |        - db
    |    db:
    |      image: postgres
    |  volumes:
    |    logvolume01: {}
    |  ```
    |
    |* Then the services we've configured can all be started at once with `docker-compose up`
    |""".stripMargin)
  .markdownSlide(Common.willCcBy).withClass("bottom")
  .renderSlides
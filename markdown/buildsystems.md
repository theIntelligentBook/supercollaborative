

## A programs get larger ...

* When there are relatively few files involved we can invoke a compiler directly

  ```sh
  javac -cp mylib.jar myapp/App.java  
  ```

  But when there are *dozens* of source files, *dozens* of libraries, lots of flags to set, that becomes unwieldy.
  And there are other things to do such as managing where we keep our libraries.

* So, it can save a lot of effort if there's a *short*, *simple* command to do everything involved in building the program. *Build systems* help us to do that.

* In this deck, there's a little historical tour of some of the ideas that went into build systems, but we'll also cover some of the ones we're likely to use.

---

## Make (1976) - Build targets

`make` is the traditional build system for the C programming language. It was inspired by a programmer wasting a morning trying to debug a bug that had already been fixed but not compiled.

* Usually, we have more than one thing we want to build.  
  *(e.g., modules, the executable, the documentation)*

* Let's call something we want to build a **build target**

* Some targets might depend on other targets  
  *(e.g., the executable depends on the modules)*

We want the build system to build the things that have changed, but not waste time re-building things it doesn't need to.

---

### Makefile

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc
CFLAGS = -g

all: helloworld

helloworld: helloworld.o
    $(CC) $(LDFLAGS) -o $@ $^

helloworld.o: helloworld.c
    $(CC) $(CFLAGS) -c -o $@ $^

clean: FRC
    rm helloworld *.o

FRC:
```

Let's break this down on the next slides...

---

### Makefile 

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc      # Means that when we say $(CC) we mean run gcc
CFLAGS = -g   # Sets flags that we'll pass to the compiler

all: helloworld

helloworld: helloworld.o
    $(CC) $(LDFLAGS) -o $@ $^

helloworld.o: helloworld.c
    $(CC) $(CFLAGS) -c -o $@ $^

clean: FRC
    rm helloworld *.o

FRC:
```

The top two lines set options for how we build the code later

---

### Makefile 

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc     
CFLAGS = -g  

all: helloworld   # "all" is a target. It depends on the helloworld executable

helloworld: helloworld.o
    $(CC) $(LDFLAGS) -o $@ $^

helloworld.o: helloworld.c
    $(CC) $(CFLAGS) -c -o $@ $^

clean: FRC
    rm helloworld *.o

FRC:
```

If we say `make all`, it will only rebuild the `helloworld` executable if some of the things it depends on have changed since it was last built.

---

### Makefile 

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc     
CFLAGS = -g  

all: helloworld   

helloworld: helloworld.o         # The executable depends on the .o file
    $(CC) $(LDFLAGS) -o $@ $^    # This is how to build it

helloworld.o: helloworld.c
    $(CC) $(CFLAGS) -c -o $@ $^

clean: FRC
    rm helloworld *.o

FRC:
```

The executable will only be rebuilt if the modules it is made from (here, just `helloworld.o`) have updated or need to be rebuilt

---

### Makefile 

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc     
CFLAGS = -g  

all: helloworld   

helloworld: helloworld.o         
    $(CC) $(LDFLAGS) -o $@ $^    

helloworld.o: helloworld.c       # The .o file depends on the C source file
    $(CC) $(CFLAGS) -c -o $@ $^  # This invokes the compiler

clean: FRC
    rm helloworld *.o

FRC:
```

Now Make knows how to build the code all the way from the C source to the executable. And it will only re-build the parts it needs to.

---

### Makefile 

A file called `Makefile` describes the build targets, what they depend on, and how to build them

```Makefile
CC = gcc     
CFLAGS = -g  

all: helloworld   

helloworld: helloworld.o         
    $(CC) $(LDFLAGS) -o $@ $^    

helloworld.o: helloworld.c       
    $(CC) $(CFLAGS) -c -o $@ $^ 

clean: FRC              # The "clean" target lets us clean up all generated files
    rm helloworld *.o   # This is how to do it

FRC:                    # It depends on an empty pseudo-target
```

---

## Software/module/library repositories

A great deal of the power of programming languages comes from the libraries that are available for them. 
Using a library someone else has written is often (but not always) much easier than writing it yourself.

* How to make it easy for developers to publish libraries, and to download libraries others have published?

* **A solution:** Keep a central (public) repository of libraries and provide a tool that can download packages from that library.

`Perl` was one of the first to offer this, via the [Comprehensive Perl Archive Network](https://www.cpan.org/) around 1995. It's still available.

Most programming languages now have something similar. E.g.

* [pypi.org](https://pypi.org/) for Python
* [Maven Central](https://search.maven.org/) for Java, Scala, Clojure, etc.
* [NPM](https://www.npmjs.com/) for JavaScript, TypeScript, etc.

---

## Maven (2001). Projects have structure

Maven is one of the build systems for Java (there are a few).
Maven recognised that programming projects are often very similar to each other. 

For example, most Java projects have a fairly similar structure:

```
src/
    main/
        java/
            mypackage/
                MyClass.java
        resources/
            mypackage/
                myimage.png
    test/
        java/
            mypackage/
                MyClassTest.java
        resources/
            mypackage/
                mytestimage.png
```

Maven assumes Java projects follow this convention (you only need to specify exceptions to convention), making build files shorter and more readable.

---

### pom.xml

Maven's build files are typically called `pom.xml` ("Project Object Model")

A minimal `pom.xml` for a *very simple* app might just give the `groupId`, `artifactId` and `version` of the app.

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
 
  <groupId>com.example.app</groupId>
  <artifactId>my-app</artifactId>
  <version>1.2-SNAPSHOT</version>
</project>
```

The rest would be assumed by convention from the fact it's a Java project.

---

### Dependency management in Maven

In times gone by, Java developers often used to download libraries manually

* How to ensure everyone on the team has the same version?

* Some teams checked libraries into version control, but this makes the version control repository grow large

* **A solution:** Declare the library in the build file, and let the build system fetch it for you from a repository (e.g. Maven Central)

    ```xml
    <dependency>
      <groupId>org.picocontainer</groupId>
      <artifactId>picocontainer</artifactId>
      <version>2.8</version>
    </dependency>
    ```

---

### Snapshot dependencies

Some of your dependencies might be on stable published versions of libraries. e.g. 

```xml
<dependency>
    <groupId>org.picocontainer</groupId>
    <artifactId>picocontainer</artifactId>
    <version>2.8</version>
</dependency>
```

However, others might be dependencies on libraries that *are still pre-release*. 
These are "SNAPSHOT" dependencies, and by convention the version number ends `-SNAPSHOT`. e.g.


```xml
<dependency>
    <groupId>org.picocontainer</groupId>
    <artifactId>picocontainer</artifactId>
    <version>2.8.1-SNAPSHOT</version>
</dependency>
```

This instructed Maven to treat them differently for caching:

* If Maven was asked for a stable version (not a snapshot), if it found it, it would cache it **indefinitely**. It assumes a published artifact *never changes*.

* If Maven was asked for a snapshot version, if it found it, it would cache it **for a day**. (Tomorrow, there might be a new snapshot)

---

### Transitive dependencies

* Your project might depend on a library

* That library might itself depend on *another* library.

**Transitive dependencies** are the dependencies that come from the libraries you're using.

---

### Build lifecycle

If the build system has to download your libraries before it can compile your code, that suggests there are some standard phases to building a project. e.g.:

1. Resolving dependencies for the compile phase
2. Downloading dependencies
3. Compiling the code
4. Resolving dependencies for any automated tests you want to run
5. Downloading test dependencies
6. Compiling the test code
7. Running the tests

(That's not an exhaustive list)

---

### Maven dependency scopes

Some libraries are used by your code in production. Other libraries (e.g. a test framework) are only used when you are *testing* your code. 

So, dependencies could have a **scope**. e.g.:

```xml
<dependency>
    <groupId>org.example.testing</groupId>
    <artifactId>example-testing-library</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

---

### Artifact repositories

Maven needs to know where to download the libraries from.
They typically have a separate URL for people to search using a UI, and a URL that Maven uses to download artifacts.

* Maven Central  
  search UI: https://search.maven.org  
  Maven URL: http://repo.maven.apache.org/maven2/  

However, you could also add *other* repositories to your build config (`pom.xml`). e.g.:

```xml
<repositories>
  <repository>
    <id>my-example-repo</id>
    <name>My Example Repo</name>
    <url>http://example.com/maven/</url>
  </repository>
</repositories>
```

---

### Private repositories and proxy repositories

If you're behind a web proxy that requires a username and password to make requests *out* to the internet, it can be painful to configure Maven to make those requests.

Some companies (and UNE) host a "proxy repository" - a repository inside their network that will mirror on demand libraries from well known public repositories.
Two particularly common products:

* Sonatype Nexus: https://www.sonatype.com/products/nexus-repository
* JFrog Artifactory: https://jfrog.com/artifactory/

---

### A downside with Maven: XML is not an easy read

<div style="max-height: 900px; overflow-y: scroll">

<pre>
&lt;?xml version="1.0" encoding="utf-8"?>
&lt;project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    &lt;modelVersion>4.0.0&lt;/modelVersion>
    &lt;artifactId>robocode.core&lt;/artifactId>
    &lt;name>Robocode Core&lt;/name>
    &lt;parent>
        &lt;groupId>com.example&lt;/groupId>
        &lt;artifactId>robocode&lt;/artifactId>
        &lt;version>2012.0.1-SNAPSHOT&lt;/version>
    &lt;/parent>
    &lt;dependencies>
        &lt;dependency>
            &lt;groupId>com.example&lt;/groupId>
            &lt;artifactId>robocode.api&lt;/artifactId>
            &lt;version>${project.version}&lt;/version>
        &lt;/dependency>
        &lt;!-- container -->
        &lt;dependency>
            &lt;groupId>org.picocontainer&lt;/groupId>
            &lt;artifactId>picocontainer&lt;/artifactId>
            &lt;version>2.8&lt;/version>
        &lt;/dependency>
        &lt;!-- test scoped -->
        &lt;dependency>
            &lt;groupId>junit&lt;/groupId>
            &lt;artifactId>junit&lt;/artifactId>
            &lt;version>4.9&lt;/version>
            &lt;scope>test&lt;/scope>
        &lt;/dependency>
    &lt;/dependencies>
    &lt;build>
        &lt;plugins>
            &lt;plugin>
                &lt;artifactId>maven-resources-plugin&lt;/artifactId>
                &lt;executions>
                    &lt;execution>
                        &lt;id>copy-resources&lt;/id>
                        &lt;phase>validate&lt;/phase>
                        &lt;goals>
                            &lt;goal>copy-resources&lt;/goal>
                        &lt;/goals>
                        &lt;configuration>
                            &lt;outputDirectory>${basedir}/target/classes&lt;/outputDirectory>
                            &lt;resources>
                                &lt;resource>
                                    &lt;directory>..&lt;/directory>
                                    &lt;filtering>false&lt;/filtering>
                                    &lt;includes>
                                        &lt;!-- actually this is bit more complicated than usual, because of quirks with IDEA -->
                                        &lt;include>versions.txt&lt;/include>
                                    &lt;/includes>
                                &lt;/resource>
                            &lt;/resources>
                        &lt;/configuration>
                    &lt;/execution>
                &lt;/executions>
            &lt;/plugin>
        &lt;/plugins>
    &lt;/build>
&lt;/project>
</pre>
</div>


---

## Gradle (2006). Buildfiles are programs

Gradle is a very flexible build system that - 

* Uses Groovy or Kotlin programming languages to define buildfiles. 

* Lets you define tasks and what they depend on

* Allows plug-ins to define conventions and tasks 
  *(e.g., the Java plugin expects a Maven-like file structure)*

It's buildfiles are, by convention, named 

* `build.gradle` for Groovy build files, or 
* `build.gradle.kts` for Kotlin build files

---

### build.gradle

```gradle
plugins {
    id 'java' 
    id 'application' // configures 'gradle run'
}

repositories {
    google() // This is mostly for Android, but it makes a good example
    maven { url "https://example.com/maven" }
}

dependencies {
    implementation 'com.google.guava:guava:27.1-jre'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.4.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.4.2'
}

application {
    mainClassName = 'example.App'
}

test {
    useJUnitPlatform()
}
```

---

### Plugins

```gradle
plugins {
    id 'java'
    id 'application'
}
```

The `java` plugin defines this is a Java project.  
This defines tasks such as `gradle compile`, `gradle test`, and `gradle build`

The `application` plugin defines the `gradle run` task. It requires the main class to be set:

```gradle
application {
    mainClassName = 'example.App'
}
```

---

### Repositories

Functions exist for some common repositories (e.g. `groovy()`).  
Otherwise, we can configure the URL of any maven repository.

```groovy
repositories {
  google() // This is mostly for Android, but it makes a good example
  maven { url "https://example.com/maven" }
}
```

Maven Central is included by default, so doesn't need to be mentioned.

---

### Dependencies

Dependencies are declared in their own block. (This code is from a generated sample)

```gradle
dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api 'org.apache.commons:commons-math3:3.6.1'

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation 'com.google.guava:guava:27.0.1-jre'

    // Use JUnit test framework
    testImplementation 'junit:junit:4.12'
}
```

Like Maven, Gradle lets you specify that some dependencies are scoped only to be included when compiling/running the tests. 

---

### Gradle dependency types

* Like Maven, Gradle has support for SNAPSHOT versions. However, it's terminology is a subtly different, calling them **Changing versions**.
  If you want to ask Gradle to re-check for a changing version (rather than use the version it might have cached for 24h), pass the
  `--refresh-dependencies` flag to your build. e.g. `gradle build --refresh-dependencies`

* **Dynamic dependencies** are where you specify that you want the latest in a range of versions. e.g.  

  ```java
  dependencies {
    implementation 'org.springframework:spring-web:5.+'
  }
  ```

  asks for the most recent 5.x release

* **Transitive dependencies** are the dependencies of your dependencies!  
  These can become numerous (but perhaps not quite as explosively numerous as in the JavaScript community)

---

### Multi-module builds

* Suppose our project is going to have a client, and it will have a server. That's two executables

* We probably also want some code that is common to both the client and the server. 

* Gradle allows us to define *multi-module projects*. 

    ```
    project/
        build.gradle
        client/
            build.gradle
            src/
        shared/
            build.gradle
            src/
        server/
            build.gradle
            src/
        settings.gradle
    ```

---

### Multi-module builds 

* In the top level `build.gradle` we can make declarations across projects. e.g.

    ```gradle
    subprojects {
        repositories {
            maven { url "https://hopper.une.edu.au/artifactory/libs-release/" }
        }
    }
    ```

* In `settings.gradle` we list the directories that are subprojects 

    ```gradle
    include "shared", "server", "client"
    ```


* The subprojects can then declare dependencies on each other. E.g.

    ```gradle
    dependencies {
        implementation project(':shared')
    }
    ```

---

### Building multi-project builds

Gradle will run tasks in the directory you are in and any subprojects lower.

* `gradle build` in the top-level project will compile all the subprojects

* `gradle build` in the `shared` project will just compile the `shared` project

But it will also always evaluate if it needs to build something else first. e.g.,

* `gradle build` in the `client` project will cause the `shared` project to be compiled if it needs to be.

---

### Gradle wrappers

Usually, we don't check binaries into version control. 

However, gradle has a convension whereby to make it easier for people working with your code to get started, 
you might check a "gradle wrapper" into version control.

This is, essentially a couple of scripts and a pre-built version of gradle so your team-mates don't have to install gradle.

So you might see tasks shown as

```sh
./gradlew build
```

instead of

```sh
gradle build
```

It's still a gradle command, it's just using the *gradle wrapper* that's been checked into the repository.

---

## Some other languages' build systems

There are a few other build systems you might encounter in this course.

* Scala projects typically use [sbt](https://scala-sbt.org)

* JavaScript projects sometimes have two parts to how they are built
   
  - a package manager (e.g. [yarn](https://yarnpkg.com/)) for dependency management, and
  - a build tool (e.g. [WebPack](https://webpack.js.org/)) for "bundling" your code (taking many large JS files and producing one smaller one)

We'll meet those when we meet them, but they are different syntaxes for the same concepts:

* Plugins
* Dependency management
* Build lifecycles 
* Custom tasks

---

## Building in the cloud. Bazel (2015)

The core idea:

* You may be working on a large code-base, but you're probably only editing a small part of the code.
  So, most of the code you are building is *identical* to what all your team-mates are building.

* Cloud data centres have faster connections to software repositories than your computer does.
  So a build tool in the cloud might be *faster* than a build tool on your computer.

* Especially if it can aggressively *cache* the output of every build step, so if you're building something
  your team mate's already built, it'll just give you the cached output from *their* build.

[Bazel](https://bazel.build/) is an open source implementation of Google's internal build tool "Blaze"

* Builds are tightly specified to be deterministic

* It can handle repositories containing projects in multiple languages ("monorepos")

... but it might be a bit much for your first project with a build system

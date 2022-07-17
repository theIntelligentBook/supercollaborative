## Sharing working changes

We'd like to share our code with our colleagues at regular intervals where it works.
(Or at least doesn't break our colleagues' work.) 

But how do we tell if it works?

* A set of automated tests is included in the project

* We run these tests before we share our changes ("don't break the build")

* The build system helps us manage and run the tests simply. e.g. we run
  ```sh
  gradle test
  ```
  to run the tests

We might also have some other more complicated tests we run periodically, but we want to have some fast 
automated tests because we'll be running them often!

---

## Testing is about information

* Suppose your project had one test that tests **everything**.

* Suppose it fails.

* Where's the bug?

Our automated tests are designed to try to give us as much information as possible on what works and what
doesn't work, as quickly as possible, so we can narrow down where any bugs are.

So we tend to test

* Piece by piece. (Narrow "unit tests" that will tell us if a class or method is broken)

* Group by group. (Sometimes two classes work, but they don't work *together*)

* And so on, from lots of small tests to fewer big tests

---

## Testing terminology

* **Unit tests** test a small element of code (e.g. a method or a class) in isolation.  

* **Integration tests** test whether different elements work together. e.g. testing a common
  operation that runs across a few different classes.

* **System tests** tests the whole application, usually including other systems that it operates with.
  e.g. Does the enrolment system correctly sync enrolled students into the learning management system?

The automated tests will usually consist of a large number of unit tests and a smaller number of integration tests.

System tests will be fewer in number again, and may be run less often.

---

## More testing terminology

* **Regression Tests** If we fix a bug, ideally we don't want to see the same bug again in the future.
  We might create an automated *regression test* to verify the bug hasn't recurred.

* **User Acceptance Tests (UAT)** If you're developing a system for a customer according to a contract,
  there may be a set of User Acceptance Tests to help the purchaser decide whether the vendor has
  delivered a system that is of sufficient quality to put it into production/use. This is probably manual.

* **Usability Tests** Try the software out with real people, to see the problems they encounter understanding
  how to use it. 

* **Functional tests** are those that verify that particular functionality works at all, whereas **Non-functional tests**
  verify quality aspects (e.g. does it run fast enough and meet performance criteria?)

There are many other kinds, but that'll do for now.
 
---

### Build systems and automated test suites

Most build systems (e.g. Gradle) offer support for including unit tests with your code. 

If your source code is in `src/main/java`, your unit tests for that module will probably be in `src/test/java`

Your project will probably contain some dependencies that are specifically to help it run the tests

```gradle
plugins {
    id 'java' 
    id 'application' 
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.4.2' // Note test scope
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.4.2' // Note test scope
}

test {
    useJUnitPlatform() // Configures the test framework to use
}
```

---

### Gradle tasks

The tests will be run as part of a number of different tasks, but generally

* `gradle test` runs the tests

* `gradle build` runs a larger build (including running the tests)

* `gradle test --tests SomeClass` will run all the tests defined in a particular class
  (in any package)

* `gradle test --tests SomeClass.someMethod` will run a specific test method in a class

---

## JUnit Jupiter

**JUnit Jupiter** (JUnit 5) is the most common unit testing framework for Java projects.

It has two artifacts your project would import:

* `junit-jupiter-api`, which is the API you call to write your tests. This is added to the
  `testImplementation` scope, so your test code is compiled against it.

* `junit-jupiter-engine`, which is the machinery that runs the tests. This is added to the
  `testRuntime` scope. (Your test code doesn't have to be *compiled* against it, but it has
   to be available when your tests are *run*.)

```gradle
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.4.2' // Note test scope
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.4.2' // Note test scope
}
```

---

Still being written...
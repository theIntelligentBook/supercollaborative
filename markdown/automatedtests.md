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

The test report (for JUnit 5) is typically generated in `build/reports/tests/test/index.html`

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

### A Simple JUnit 5 test

```java
package example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class FibonacciTest {
    @Test
    public void fib0() {
        assertEquals(0, fibonacci(0));
    }
}
```

Notes: 

* Tests are put in a class, typically ending "...Test" so that the runner can find them.
* Test methods are annotated `@Test`. (A class can contain multiple tests.)
* They contain a call to at least one assert method. Typically, these put the *expected* result first, then the call to test.  
  (You can optionally put a message afterwards, but if possible try to name the test so it's clear ayway)

---

### Different kinds of assertion

Most of the methods in [`org.junit.api.jupiter.Assertions`](https://junit.org/junit5/docs/5.8.1/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html) are well documented and self-explanatory, but here are a few notes:

* `assertEquals(expected, value)` tests for equality
* `assertEquals(double expected, double value, double delta)` is for floating point numbers.  
  Rounding and calculation errors can easily make a floating point result *slightly* different than you expected, so testing
  for exact equality is error prone. Instead we test that it is within an acceptable range (+/- delta) of the expected result.
* `assertArrayEquals(expected, actual)` checks the contents of arrays. `assertEquals` would just check the *reference to the array*.
* `assertIterableEquals(expected, actual)` checks the contents of iterable items (e.g. `List`s). 
* `assertThrows(expectedType, executable)` can be used to verify that some code (typically a lambda) throws a particular exception

---

### `assertArrayEquals`

This would fail:

```java
    @Test
    public void arrayOfFibs() {
        assertEquals(new int[] { 0, 1 }, new int[] { fibonacci(0), fibonacci(1) });
    }
```

Although the arrays have the same values, two arrays (even if they contain the same values) are not "equal":

```log
org.opentest4j.AssertionFailedError: expected: [I@28e8dde3[[0, 1]] but was: [I@6d23017e[[0, 1]]
```

Instead we need to use:

```java
    @Test
    public void arrayOfFibs() {
        assertArrayEquals(new int[] { 0, 1 }, new int[] { fibonacci(0), fibonacci(1) });
    }
```

---

### `assertThrows`

[`assertThrows`](https://junit.org/junit5/docs/5.8.1/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html#assertThrows(java.lang.Class,org.junit.jupiter.api.function.Executable)) has a method signature you might not be familiar with - it takes

* `Class<T>`, in this case `IllegalArgumentException.class`
* `Executable`, which is a *Single Abstract Method* (SAM) type. These are classes that have exactly one method left to implement, and
  Java lets us write them as a lambda expression (a single inline anonymous function). e.g. 
  `() -> fibonacci(-1)`

```java
    @Test
    public void fibMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            fibonacci(-1);
        });
    }
```

Scala programmers note: whereas Scala uses `=>` to define a lambda, Java uses `->`.

---

### Display names

We can make our test reports more readable by including a display name with our tests

```java
    @DisplayName("The fibonacci sequence is started with [0, 1]")
    @Test
    public void arrayOfFibs() {
        assertArrayEquals(new int[] { 0, 1 }, new int[] { fibonacci(0), fibonacci(1) });
    }
```


---

### Ignoring tests

Sometimes we need to turn a test off temporarily. We can do this with the `@Disabled` annotation:

```java
    @Disabled
    @Test
    public void arrayOfFibs() {
        assertArrayEquals(new int[] { 0, 1 }, new int[] { fibonacci(0), fibonacci(1) });
    }
```

---

### Assumptions

Assumptions can be used if you have tests that should be run in one environment, but skipped in another. 

This lets us do conditional execution of tests. The test gets *aborted* rather than *failed* if the assumption is not met.

```java
    @Test
    public void arrayOfFibs() {
        assumeEquals("TRUE", System.getProperty("RUN_LONG_TESTS"));
        // A contrived example that assumes an inefficient implementation of fibonacci
        assertEquals(1836311903, fibonacci(46));
    }
```

---

### Test set-up

Sometimes, we might have set-up tasks we need to run before the tests are run.  
e.g. if there's some test data we need to set up in a data structure.

JUnit lets us define this set up code in methods annotated

- `@BeforeAll` - if the method should be run once for a test class, before the set of the tests in that class are run (once per class).
- `@BeforeEach` - if the method should be run before each test method in the class (once per method).

Likewise, there are `@AfterAll` and `@AfterEach` annotations for "tear-down" methods.

---

## MUnit

There are a lot of different testing libraries for different languages. MUnit is a test framework for the Scala language that is
built on top of JUnit. This makes its concepts somewhat familiar.

e.g. 

```scala
  test("it should calculate Roman Numerals correctly") {
    assertEquals(roman(1), "I")
    assertEquals(roman(3), "III")
    assertEquals(roman(4), "IV")
    assertEquals(roman(10), "X")
    assertEquals(roman(104), "CIV")
    assertEquals(roman(1900), "MCM")
    assertEquals(roman(1988), "MCMLXXXVIII")
  }
```

Although, being Scala, it follows different syntax conventions, the concepts of test classes and assertions are similar.

---

## What tests to write?

So far, this has discussed the machinery of running tests. But what should we test?

There's often an infinite domain of possible inputs. e.g. do we test *every* date?

**Advice:** 

We're trying to catch bugs. So, the tests we write are going to be (to an extent) determined by the sort of bugs we think could happen.

One way of thinking about this is *equivalence classes*.

---

### Suppose we're testing birthdays...

Which of these values might we care particularly about testing? (There is more than one right answer.)

* 8 December 2008
* 19 August 2011
* 91 August 2011
* 31 July 2011
* 31 August 2011
* 29 February 2012
* 29 February 2011

Another way of thinking about it:

Tests are a specification by example of how your code should behave.






## Continuous Integration

Earlier, we met "continuous integration" as a practice: merging and combining your code as often as (practically) possible. 
However, there are also Continuous Integration servers, to help with this process.

* Everyone pushes and fetches from the same repository

* They run the tests before they push

But...

* A "Continuous Integration server" (like a robot butler) also pulls from the repository regularly and builds the tests

* It identifies when the build is broken

---

### Continuous Integration

Advice from [Martin Fowler](https://martinfowler.com/articles/continuousIntegration.html):

* Maintain a single source code repository

* Automate the build

* Make your build self-testing

* Everyone commits to the main-line every day

* Every commit should build the main-line on an integration machine

* Fix broken builds immediately

* Keep the build fast

* Test in a clone of the production environment

* Make it easy for anyone to get the latest executable

* Everyone can see what's happening

* Automate deployment

---

## Test Driven Development (TDD)

Test driven development is based on the idea of writing the tests first (as in Assessment 2).
The mnemonic for it is <span color="red">Red</span>, <span color="green">Green</span>, <span color="blue">Refactor</span>:

If we're asked for some new functionality:

* **Step 0:** *"Ticket please!"*, first we want to create an issue so we know someone else isn't already working on it. 
  That's not directly part of TDD, but let's encourage it

TDD proper:

* **Step 1**: Red. Create a unit tests that test if our functionality works. This test should **fail** (hence "red", the colour of failing tests).  
  (If it passes, clearly the code already did what we want to implement, so there's nothing to do.)

* **Step 2**: Green. Create code that will make the tests pass. Inelegant code is ok at this point, we just want it to work.

* **Step 3**: Refactor. Improve our code (including surrounding code) to make it more readable and maintainable.

In practice, I suspect most developers don't follow it strictly in order. Tests are code and code means calls to APIs. It's hard to do that without
already starting thinking what the structure of the APIs are going to be (i.e. you're already thinking about the internals of your intended code).

---

## A trouble with tests

Suppose I have a thousand unit tests, but they are all this:

```java
@Test
public void testNothing() {
  assertTrue(true);
}
```

Our automated tests are only as good as the tests we write:

* Broken code will the tests, if the tests never call it. Not what we want.

* Poor code can pass poor tests. Not what we want.

So how can we know if our tests are calling our code?

And how can we know if our tests are any good?

---

## Code coverage

One solution to finding out whether our tests are calling our code is to measure it:

* Instrument the JVM. i.e. insert hooks for when code is being called and log what is called

* Run the tests

* Take a look at the data, and see which lines were and weren't called.

There are **code coverage** plugins that can do this for us, e.g. [JaCoCo](https://www.jacoco.org/jacoco/) (short for "Java Code Coverage")

---

## Code coverage

There are a few different possible measures of code coverage:

* **Statement coverage**

  What proportion of statements in my code are exercised by my tests?
  
* **Branch coverage**

  What proportion of branches are covered in my tests? 
  
  An `if` statement has two branches; my tests might not try both branches
 
* **Path coverage**

  What proportion of possible paths through my code are covered in my tests?
  
  Two `if` statements in a method have 4 paths  
  Three `if` statements in a method have 8 paths.  
  etc.

---

## Example

This is a slightly contrived example

```java
public String describe(int i) {
  StringBuilder sb = new StringBuilder();
  if (i < 0) {
    sb.append("Negative ");
  } else {
    sb.append("Positive ");
  }

  if (Math.abs(i) < 100) {
    sb.append("its absolute it less than 100 ");
  }

  if (isPrime(Math.abs(i))) {
    sb.append("and its absolute is prime");
  }

  return sb.toString()
}
```

Consider which lines, branches, and paths are executed for these test values:

* `3`
* `-1000`
* `263`

---

## That sounds like work...

* May be an exam question on it...

* But generally, why do it manually when we can get a machine to do it for us?

---

## JaCoCo

* **Ja**va **Co**de **Co**verage

    ```gradle
    apply plugin: "jacoco"
    ```
    
    ```bash
    gradle jacocoTestReport
    ```

* https://docs.gradle.org/current/userguide/jacoco_plugin.html

---

## A trouble with test coverage

Suppose this is our code:

```java
public double calculateThrust(double x, double y) {
  double determinant = Complex.flugenFactor(x, y, FLUGEN_CONSTANT);
  double spoodle = Mindboggling.spoodle(x, y, NUM_ELEPHANTS, getWeatherInOslo());
  
  return Math.max(determinant, determinant / Math.log(spoodle));
}
```

And this is our test:

```java
@Test
public void testCalculateThrust() {
  double x = 50d;
  double y = 103d;
  double result = calculateThrust(x, y);
  
  assertEquals("1 wasn't 1", 1 == 1);
}
```

We've executed the code (so the code coverage will be non-zero), but we're still not testing anything useful at the end.

---

## Mutation testing

What if we test the tests?

* A test should fail if there's a bug in our code

* A random change to our code is highly likely to introduce a bug

* What if we had our test suite randomly change our code, and measured whether  the changes were caught by the tests?

(But it's probably a step too far for our project. It can be slow.)

---

## Practical issues writing tests

It can be a challenge isolating code in order to test it. For instance:

* Calls to external services. To run this code, we need an `enrolmentsSystem` and an `extensionsDatabase`:  
  ```java
  public boolean canStudentSubmit(Assignment assignment) {
    boolean enrolled = enrolmentsSystem.isEnrolled(assignment.author, assignment.subject);
    Date extension = extensionsDatabase.getExtension(assignment.author, assignment.subject, assignment.assessmentId));
    Date due = (extension != null && extension.laterThan(assignment.due)) ? extension : assigment.due;

    return enrolled && (Date.now() <= due);
  }
  ```

* Tightly interconnected structures. Suppose I wanted to test the button is enabled correctly:  
  ```java
  JButton castSpell = new JButton("Cast");
  JLabel message = new JLabel();
  if (player.spellPoints < selectedSpell.cost) {
    castSpell.setEnabled(false);
    message.setText("Insufficient spell points");
  }
  ```
  To create a button and a label, I need a panel, a window, I need to render it to the screen....

* User interfaces can be especially complex to test - the continuous integration server is "headless" (no screen)

---

### Test doubles

So that we don't have to instantiate the *whole system* just to test a little part of it, we often use **test doubles** 
for the parts we aren't testing.

e.g. rather than talk to the real enrolments sytem, in the unit test we might fake it.

There are a few different kinds of test double, with different levels of smarts:

* **Dummy objects**: passed around but never actually used. 

* **Fake objects**: have working implementations, but not suitable for production. 

    * eg, in-memory database.

* **Stubs**: provide canned answers to the calls made during the test.

* **Spies**:stubs that record some information from how they were called. 

    * eg, count how many times called

* **Mocks**: stubs that can verify what calls were made, and throw an exception if an unexpected call is made

---

### Mockito

* Popular Java Mocking library. [mockito.org](http://mockito.org/)

```java
import static org.mockito.Mockito.*;

// mock creation
List mockedList = mock(List.class);

// using mock object - it does not throw any "unexpected interaction" exception
mockedList.add("one");
mockedList.clear();

// selective, explicit, highly readable verification
verify(mockedList).add("one");
verify(mockedList).clear();
```

---

### Mockito when...

```java
// you can mock concrete classes, not only interfaces
LinkedList mockedList = mock(LinkedList.class);

// stubbing appears before the actual execution
when(mockedList.get(0)).thenReturn("first");

// the following prints "first"
System.out.println(mockedList.get(0));

// the following prints "null" because get(999) was not stubbed
System.out.println(mockedList.get(999));
```

---

### Hard to test code

If we want to use a test double in our tests, we need some way to connect it to the object we're testing.

This is very difficult to test:

```java
public class AssignmentVerifier() {
  private EnrolmentsSystem = new EnrolmentsSystem("https://myune.example.com/enrolmentsService");
}

public final class EnrolmentsSystem(String url) {
  // etc
} 
```

* We've got no way of getting a mock `EnrolmentsSystem` into an `AssignmentVerifier`.

* The `EnrolmentsSystem` is `final`. The mocking framework's going to have trouble making a valid subclass.


---

### Design for testability

We can alter how we write our code to make it easier to test. 

```java
public class AssignmentVerifier(EnrolmentsSystem es) {
  // ...
}

public interface EnrolmentsSystem {
  // ...
}

public class LiveEnrolmentsSystem(String url) implements EnrolmentsSystem {
  // code for our real implementation
}
```

In this code, it's easier for us to create a test double of the enrolments system (a fake one we'll use in the tests)
and set it up in the AssignmentVerifier.

---

### Design for testability

Inside the tests, we can ask Mockito to create a mock for us:

```java
@Test
@DisplayName("Students cannot submit to a class they aren't enrolled in")
public void testCannotSubmitToWrongClass() {
  EnrolmentSystem es = Mockito.mock(EnrolmentSystem.class);
  AssignmentVerifier av = new AssignmentVerifier(es);

  when(es.isEnrolled("s123456", "XYZ123")).thenReturn(false);

  Assignment a = new Assignment();
  a.setAuthor("s123456");
  a.setSubject("XYZ123");
  assertFalse(av.canStudentSubmit(a));
}
```

Sometimes, there might be a lot of outside references that we're seeking to mock.
In those cases, it can be more common to get them set by *setter* functions than include them in the constructor.

---

### Dependency injection

Sometimes we have a lot of dependencies we need to wire up to our classes. 

To help with these situations, there are *Dependency Injection* (DI) tools, (e.g. Google Guice) that will look for dependencies
and wire them up for us.

In Java, typically, this requires:

* `@Inject` annotations on constructors whose parameters should be injected by the DI tool

* `@Provides` annotations on objects that are available to be injected into them

[Example from Google Guice](https://github.com/google/guice/wiki/GettingStarted)

However it does bring the trade-off that although your "wiring up" code ends up shorter, you have an extra concept and 
external dependency in your code.

(For a shorter student project this might not be worthwhile)

---

### Dependency injection in Scala

Scala (another JVM lanuage) effectively has dependency injection written into the language:

We can declare that a parameter should be picked up from the compiler's current context with
a `using` parameter

```scala
class AssignmentVerifier(using es:EnrolmentsService) {
  // etc.
}
```

This will then be wired to whichever value is `given` at the call site:

```scala
given TestEnrolmentsService(/* etc */)
val av = AssignmentVerifier()
```

---

## Making UI code testable

Testing UI code is a common difficulty - 

* Getting the UI to appear involves starting up an application including its front end

* The testing machine usually doesn't have a screen or graphics (it's in "headless" mode)

* If your code has popped up a dialog, it's not easy for the test framework to get a reference to an element in that dialog
  to test its state 

Fortunately, there are some techniques from web toolkits that can make it easier: declarative user interfaces

---

### Traditional UI code can be hard to test

This is traditional code that's a little difficult to test

```java
JPanel panel = new JPanel(new BorderLayout());
Widget myWidget = new Widget();
JButton button = new JButton("Do something");
panel.add(myWidget, BorderLayout.CENTER);
panel.add(button, BorderLayout.SOUTH);
frame.add(panel);
frame.setVisible(true);
```

There isn't a structure here to test (at least, not one that's accessible to JUnit) and it's not really possible to mock the underlying UI kit.

---

### Declarative UI code

Suppose instead we produced a declarative *description* of our UI state, represented as record classes

```java
// Representing a UI as a data structure
public Widget widgetDialog() {
  return new Panel(
    new VBox(
      new MyWidget(),
      new Button("Do something")
    )
  )
}
```

Now we have a data structure in our hands

* At runtime, we could pass this data structure to a rendering framework

* At test-time, we could test the data structure we've produced

---

### Declarative UI code in the real world vs our project

Web frameworks, e.g. React, often use this kind of declarative UI.

* React components are plain ol' JavaScript objects
  ```jsx
  <HelloMessage name="Friend" />
  ```

  translates to something like 
  ```jsx
  React.createElement("HelloMessage", { name: "Friend" })
  ```

  and returns an ordinary JavaScript object

* React's rendering engine synchronises your declarative UI state with what's shown on the page

These are not as common in native toolkits or Java toolkits, but we might be able to mock something up in the project.
(e.g. rendering the UI from JSON data sent from the server to the client. The JSON data should be easier to test.)

---

### Behaviour driven development

*What if we wrote our tests in language the customer could understand? 
Then we could show them to the customer and get them to review them?*

* Behaviour driven development (BDD) was a movement for writing these kinds of (non-technical) human-readable tests

* Typically, domain-specific language for writing tests. eg, Cucumber, Specs2

```cucumber
Feature: Refund item

  Scenario: Jeff returns a faulty microwave
    Given Jeff has bought a microwave for $100
    And he has a receipt
    When he returns the microwave
    Then Jeff should be refunded $100
```


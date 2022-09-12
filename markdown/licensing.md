## Dependencies

Gradle and other build tools make it very easy to depend on libraries. Examples from some of my code:

```gradle
implementation group: 'org.apache.logging.log4j', name: 'log4j-api', version: '2.18.0'
```

```sbt
libraryDependencies ++= Seq(
  ("com.typesafe.akka" % "akka-actor" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" % "akka-actor-typed" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" % "akka-stream" % "2.6.14").cross(CrossVersion.for3Use2_13),
)
```

In that one line of dependency is wrapped up a *lot* of other people's work.

And potentially a lot more **transitive dependencies**

---

### Who pays open source programmers for their work?

Let's start with an example that hits close to home to me: a licence change in the Scala community

> Speaking to The Stack about the change, Lightbend founder and CEO Jonas Bonér says: “I think in the early days, in 2009-2010 it was reasonably okay to build a business around [consulting and support for open source]. But it’s been getting harder and harder because most of our customers they’re more confident at doing self-support. It’s a sort of screwed-up incentive process in a way because the better Akka is, the less we are likely to get paid for supporting it. It’s been so reliable; we’ve had customers running it in production for years without any hiccups. But how many of the large enterprises are contributing back? I’d say, it’s close to zero.

[Jonas Boner, CEO of Lightbend, via The Stack](https://thestack.technology/akka-license-change-lightbend-opensource/)

---

### Change of License from Apache 2.0 to Business Source Licence 1.1

> Over the years, Lightbend has steadily borne more of the support for Akka. With Akka now considered critical infrastructure for many large organizations, the Apache 2.0 model becomes increasingly risky when a small company solely carries the maintenance effort. Balancing the global demands of our corporate community while supporting these needs of a vast open source base is a tremendous weight to bear.

[Why We Are Changing the License for Akka](https://www.lightbend.com/blog/why-we-are-changing-the-license-for-akka), 7 September 2022

---

### Motivation - Tragedies of the commons

> Sadly, open source is prone to the infamous “Tragedy of the commons”, which shows that we are prone to act in our self-interest, contrary to the common good of all parties, abdicating responsibility if we assume others will take care of things for us. This situation is not sustainable and one in which everyone eventually loses.

A rough summary of a tragedy of the commons:

* It's in everyone's interest that someone does the work

* It's in nobody's interest to do the work

* Nobody does the work

---

### Not the only company doing it

> Couchbase changes source code license to BSL 1.1 
>
> Today we are announcing that Couchbase is changing our source code license from Apache 2.0 to the Business Source License version 1.1 (BSL 1.1). This license allows software providers like Couchbase to control how their source code is commercialized while still publishing the source code to the community. The BSL 1.1 was originally introduced by the founders of MySQL and MariaDB, and first applied to MariaDB products in 2013. It was updated and clarified in 2017 as version 1.1, after consultation and advice from Bruce Perens, co-founder of the Open Source Initiative (OSI).  

[couchbase.com](https://www.couchbase.com/blog/couchbase-adopts-bsl-license/)

---

### Reactions from users - "Morally wrong" to change away from OSS?

> It’s morally wrong to make the product popular, by advertising it as Open Source / Free Software, and then doing a reversal later. Don’t get me wrong, I am sympathetic to the issue that Open Source contributors aren’t getting paid. But in the Java community nobody wants to pay licenses for libraries. If that model ever worked, it was in other ecosystems, such as that of .NET, and that model has been dying there as well. Turns out, trying to monetize software libraries is a losing proposition.

[Alexandru Nedelcu, Monix developer](https://alexn.org/blog/2022/09/07/akka-is-moving-away-from-open-source/)

---

### Reactions from users - Depended on the licence not changing?

> I’ve been doing estimates for my company this week. Our bill would be at least $15 million annually before going through procurement for bulk pricing, for a library not a deployable service. One service might lightly touch akka http another might do one thing with Akka streams, yet it is charged $2k per vcpu if you’re touching this massive framework in any way. It makes no sense. This pricing model has to be the dumbest thing I’ve seen in software. It would take years for us to untangle our Akka use, rewriting entire services. The stuff I own we spent four years building.

[shadowofahelicopter, reddit](https://www.reddit.com/r/scala/comments/xa2gok/lightbend_commit_to_supporting_akka_26x_until/inrn8i2/)


---

### Reactions from users - relationship with community

> Do you respect your community?
>   
> Community is an interesting beast because unlike your customers, which have a legal agreement with you, a community is usually relationship-based. You do not owe them anything and they do not owe you anything either.  Participating in a community though, especially when done for years, comes with certain expectations. I help you by promoting the project, speaking at conferences, reporting bugs, contributing code, and improving documentation — and in return, you get to freely use it, improve it, and share your changes as governed by the open source license of the project.

[Peter Saitzev, percona.com](https://www.percona.com/blog/open-source-bait-and-switch-licensing-and-beyond/)


---

### Open source downstream projects

> We are stoked to announce The @guardian is now sponsoring the Play Framework 🎉 This is an important step, since now we have enough funding to finally onboard a full time dev🧑‍💻 starting this week already! 

Play framework has a dependency on Akka. There is a Play-specific additional grant in the licence

> If you develop an application using a version of Play Framework that utilizes binary versions of akka-streams and its dependencies, you may use such binary versions of akka-streams and its dependencies in the development of your application only as they are incorporated into Play Framework and solely to implement the functionality provided by Play Framework; provided that, they are only used in the following way: Connecting to a Play Framework websocket and/or Play Framework request/response bodies for server and play-ws client. -- [Akka BSL Licence 1.1, Sept 2022](https://www.lightbend.com/akka/license)

But what about any other OSS project with Akka as a dependency?

---

### What if the language removed their version of the feature, to support your library?

> Starting with Scala 2.11.0, the Scala Actors library is deprecated. Already in Scala 2.10.0 the default actor library is Akka. To ease the migration from Scala Actors to Akka we are providing the Actor Migration Kit (AMK). 

[scala-lang.org, 2017](https://docs.scala-lang.org/overviews/core/actors-migration-guide.html)

Does this create any expectation that your library will continue to be free? If so, for how long?

---

### Maven central

> Though they declare that Maven Central is “OSS Repository Hosting”, they don’t require that the libs hosted on there are FOSS. Sometimes we find that a non-free lib was pulled from Maven Central and have to disable lots of affected versions of published apps.

["Maven Central is not as free as it looks", F-Droid](https://f-droid.org/2022/07/22/maven-central.html)

Potentially applies to any online repository of libraries.

---

### Who interprets the licence?

> The Licensor hereby grants you the right to copy, modify, create derivative works, redistribute, and make non-production use of the Licensed Work. The Licensor may make an Additional Use Grant, above, permitting limited production use. [BSL 1.1](https://www.lightbend.com/akka/license)

* What is "non-production" use?

> "...but it has a limitation on use in production (meaning in any capacity that is meant to make money)" [MariaDB Fixes it's Business Source License With My Help, Releases MaxScale 2.1 Database Routing Proxy, Bruce Perens](https://perens.com/2017/02/14/bsl-1-1/)

Bruce Perens is an influential person...

> Bruce Perens is one of the founders of the Open Source movement in software, and was the person to announce “Open Source” to the world. He created the Open Source Definition, the set of legal requirements for Open Source licensing which still stands today. [Bruce Perens](https://perens.com/about-bruce-perens/)

...but not an employee of Lightbend, nor your employer's audit team. 

---

### Who is it reasonable to charge?

> Production use of the software requires a commercial license from Lightbend. The commercial license will be available at no charge for early-stage companies (less than US $25 million in annual revenue). By enabling early-stage companies to use Akka in production for free, we hope to continue to foster the innovation synonymous with the startup adoption of Akka.

But your employer receiving $25m of revenue doesn't mean *your project* has $25m of revenue. 

For instance, Hypothetical University might earn $400m from teaching and research, not from selling akka-based product.

* Should they be able to use it in a side-project?

* Should they be able to use it for core infrastructure?

* If they teach *you* Akka, is it reasonable that projects you write can only use it free if your employer is small enough?

---

### Where's this heading...

I'm not introducing this to state **any** rights or wrongs. I'm confident Lightbend have no intention to charge me or you for our use of Akka [and in any case, the version we've all been using is Apache-licensed]. But -

* When we depend on other people's code, like it or not, we are affected by potential changes in their licenses. 

* There are academic papers on the economics of open source publishing. e.g. [Kort & Zaccour, 2011. When Should a Firm Open Source its Source Code -- A Strategic Analysis](https://doi.org/10.1111/J.1937-5956.2011.01233.X)

* There are ethical questions about whether society appropriately values and rewards software developers' work / the amount of free work computer scientists are expected to perform to maintain open source libraries

* There are also ethical questions about the "social contract" you have with the community around your product. 

---

### What licences are in your code-base?

One question we can partially answer with technology is *what are you depending on*?

The Gradle Licence Report can hunt through your dependencies, gathering the licences of the libraries you have used.

https://github.com/jk1/Gradle-License-Report

...Note that there is a certain cynicism even here, though...

> This plugin will resolve all your dependencies, and then scour them for anything that looks like relevant licensing information. **The theory is to automatically generate a report that you can hand to corporate IP lawyers in order to keep them busy.**  
> [emphasis added]

---

### Bigger stakes...

Some very famous and very large lawsuits have taken place over copyright in Open Source Code.

* Sun released a version of the Java Virtual Machine under the GPL. Oracle later acquired Sun

* Google use Java in Android, but their offering was *not* under the GPL.

On August 13, 2010, Oracle sued Google for copyright and patent infringement 

> Google's win over Oracle: unlicensed use of code considered fair use
>
> The United States Supreme Court has held that Google's use of the Java API owned by Oracle in Google's Android platform did not amount to copyright infringement.
>
> Google was able to rely on the doctrine of fair use to avoid liability, including because its use of the Java API amounted to a transformative use in that it applied the API in a new software environment. (ashurst.com, 22 Jun 2021)[https://www.ashurst.com/en/news-and-insights/insights/googles-win-over-oracle-unlicensed-use-of-code-considered-fair-use]

The legal brouhaha went on for nearly 11 years!






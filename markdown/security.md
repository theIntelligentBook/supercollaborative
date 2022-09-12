## Security

One of the non-functional requirements that developers often have some responsibility for is security. Let's discuss this in three ways:

* The security of our practices (people hacking *us*)

* The security of our code (faults in our code)

* The security of our dependencies (faults in other people's code we use)

---

### Developers and security

Unfortunately, software development teams themselves can be a target for intrusion:

> Ever have one of those weeks? This has just not been the best couple of days for me or for Valve.
>
> Yes, the source code that has been posted is the HL-2 source code.
>
> Here is what we know:
>
> 1) Starting around 9/11 of this year, someone other than me was accessing my email account. This has been determined by looking at traffic on our email server versus my travel schedule.
>
> ...
>
> 4) Around 9/19 someone made a copy of the HL-2 source tree.

[Gabe Newell, Valve](https://archive.arstechnica.com/news/posts/1065132052.html)

---

### A problem...

As well as being regular users (e.g. of email), developers also have very complex machinery -

* Jenkins has access to our code via build tokens. What if a token leaks?

* So does GitLab, Artifactory, ... many other systems that might be left insecure or on outdated versions. (Certainly possible some of ours are)

* We have development and test environments (with full versions of our code and often access to our networks) that might not be as tightly monitored as production

* We have unusual interactions between pieces of software. e.g. 

  - For security reasons, UNE runs an authenticated web proxy (so it knows who made outbound web requests)
  - But that means you need to set your password in some places
  - If you start IntelliJ with the sbt plugin installed, it'll pass your proxy password as an argument to sbt to help it download components...
  - ...making your password accessible to other machine users.

  Major security ticket raised with JetBrains in 2020.

---

### Official recommendations

Security recommendations struggle to cover all of the cases. Security flaws often come from the *bugs* in your processes, not the *features*

E.g., on the Australian Cyber Security Centre's "Guidelines for Software Development" pages

https://www.cyber.gov.au/acsc/view-all-content/advice/guidelines-software-development

we can see advice such as

* Segregating development, testing and production environments, and associated data, can limit the spread of malicious code and minimises the likelihood of faulty code being introduced into a production environment.

but we're unlikely to see something about the combination of an IDE plugin, an external build tool, and a web proxy.

---

### A key issue...

One particular problem, however, is the number of places where *your password* rather than an authentication key is used.

If UNE's systems let you generate a unique authentication token *other than your password* to use with the web proxy, it'd be lower risk.

Even today in the software world, token-based security is often a soft recommendation; perhaps it should be a much stronger recommendation...

> If you prefer not to save your actual [Sonatype] username and password in GitHub Actions settings below, generate your user tokens:
> 
> login to https://s01.oss.sonatype.org/ (or https://oss.sonatype.org/ if your Sonatype account was created before February 2021),
> click your username in the top right, then profiles,
> in the tab that was opened, click on the top left dropdown, and select "User Token",
> click "Access User Token", and save the name and password parts of the token somewhere safe.

[sbt-ci-releases plugin documentation](https://github.com/sbt/sbt-ci-release)

---

### A key issue in your code

* 2012: "Just use bcrypt"

* 2022: "Just don't use passwords?"

In the early 2010s, there was a lot of criticism of software start-ups for using weak password hashing. The problem being that if your database leaked, then if the passwords weren't securely salted and hashed using an *inefficient* enough method, it would be viable for someone to crack the passwords. And most users re-use passwords on other sites.

  - [Have I Been Pwned](https://haveibeenpwned.com), site for checking if one of your passwords has been broken

In 2022 the advice is probably reversed. Regardless of how secure *your* password hashing is, if your users have re-used a password, you're vulnerable to how weak the protection is on *every other site where they used that password*.

It may be better avoiding using (just) passwords at all...

  - Multi-Factor Authentication
  - Authorisation tokens (e.g. OAuth, JSON Web Tokens)
  - One-Time Passwords

---

### What other personal data are we collecting

Data protection regulations now require our software to be careful with our users' data

Advice (similar to non-functional requirements) from the tech community ...

* Privacy by design & default

* Consent & notification

* Pseudonymization by default

* Encryption of data

* Right to be forgotten

* Data breach reporting

* Right to portability

[VentureBeat](https://venturebeat.com/datadecisionmakers/tips-and-guidelines-for-making-software-applications-gdpr-compliant/)

---

### The security of our own code

> Secure-by-design principles and secure programming practices, supported by agile software development practices and threat modelling, are an important part of application development as they can assist with the identification and mitigation of at risk software components and risky programming practices.

https://www.cyber.gov.au/acsc/view-all-content/advice/guidelines-software-development

---

### Secure Design Principles

According to OWASP

1. Minimise attack surface area
2. Establish secure defaults
3. The principle of Least privilege
4. The principle of Defence in depth
5. Fail securely
6. Don't trust services
7. Separation of duties
8. Avoid security by obscurity
9. Keep security simple
10. Fix security issues correctly

https://github.com/OWASP/DevGuide/blob/master/02-Design/01-Principles%20of%20Security%20Engineering.md

---

### National Cyber Security Center (UK) Secure Design Principles

* Establish the context 

  Determine all the elements which compose your system, so your defensive measures will have no blind spots.

* Making compromise difficult
  
  An attacker can only target the parts of a system they can reach. Make your system as difficult to penetrate as possible

* Making disruption difficult

  Design a system that is resilient to denial of service attacks and usage spikes

* Making compromise detection easier

  Design your system so you can spot suspicious activity as it happens and take necessary action

* Reducing the impact of compromise

  If an attacker succeeds in gaining a foothold, they will then move to exploit your system. Make this as difficult as possible

https://www.ncsc.gov.uk/collection/cyber-security-design-principles

---

### Observations...

* These are non-functional requirements that rely on review

* A lot of vulnerabilities are due to bugs and oversights

  - [XKCD's explanation of HeartBleed](https://xkcd.com/1354/), a bug in OpenSSL

* In some places detection can be automated 

  - e.g. detecting "code smells" and anti-patterns - See SonarCloud for an example of this 
  
  - but it's difficult to catch them all automatically

* OWASP maintains a [top 10](https://owasp.org/www-project-top-ten/) site for web security risks. 

* OWASP also maintains an [Application Security Verification Standard](https://owasp.org/www-project-application-security-verification-standard/)  
  
  - a 71-page checklist of security aspects of software. e.g. "Verify that the build pipeline warns of out-of-date or insecure components
and takes appropriate actions."

* In CS research, there's been work on *proving* that code is secure (or at least, free from certain kinds of insecurity), however
  this has not always been financially viable.

---

### Security from our dependencies

If we depend on a lot of other people's code, we can find we are depending on the *security* of a lot of other people's code.

A case study: [When Log4J turned out to be insecure](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)

* Log4J is perhaps the single most popular logging library for Java. It is in the top **0.003%** of downloads from Maven Central

* It's a dependency in more than 7,000 other open source projects

* It's even running on Mars (in the Ingenuity helicopter)

* From version 2.0-beta9 (September 2013) to version 2.15.0-rc2 (December 2021), it had an undiscovered critical vulnerability allowing remote code execution.

---

### Not just the Java ecosystem...

> More than 1,000 pieces of malware have been removed from the NPM repository following an investigation into the presence of malicious JavaScript packages.

[techtarget.com, 2022](https://www.techtarget.com/searchsecurity/news/252512799/More-than-1000-malware-packages-found-in-NPM-repository)

---

### Automated analysis of dependencies 

There are now automated tools in the developer ecosystem that help mitigate these issues.

* Dependabot - automatically scanning your repository for outdated dependencies.  
  https://github.com/dependabot

* Scala Steward - Another bot designed specifically for the Scala ecosystem
  https://github.com/scala-steward-org/scala-steward

* `npm audit` - Command that can be run on NPM projects to get a report of known vulnerabilities
  https://docs.npmjs.com/auditing-package-dependencies-for-security-vulnerabilities

* Docker Scan - tool built into docker to detect vulnerable software in your containers (using Snyk)
  https://docs.docker.com/engine/scan/


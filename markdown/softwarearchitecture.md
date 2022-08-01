## Software Architecture

As well as implementing our functionality, we usually want our software to meet *non-functional requirements*.
E.g.:
  * Handling enough concurrent requests
  * Performing well enough


We also usually want it to have certain *quality attributes*.
E.g.:

  * Maintainability
  * Extensibility

When we're trying to implement the needed *functionality*, meeting the *non-functional requirements*, with the required *quality attributes*, we need to consider the achitecture of the code.

**The architecture of your program helps you reason about your program**

---

## Simple code quality attributes

Generally, we want code to have *high cohesion* and *low coupling*.

![](http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuU8gI4pEJanFLL1oL5Aevb9G0ABapABa7A18X18oBr89YHEb154QeQ2h40cXTHMYW8n828Eh5iba9v39I9e3KCmDH3Ot26fk0D3SG0Yjgn04P0I26M1pmLO4S74vfEQb0Bq00000)

**Cohesion** - how much an item within a module relates to other things within the module

**Coupling** - how much a module depends on the internal details of another module

---

## Simple code quality attributes

So, for example, this doesn't look as clean:

![](http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuU8gI4pEJanFLL1oL5Aevb9G0ABapABa7A18X18oBr89YHEb154QeQ2h40cXTHMYkBXgaGnq0Xc8f2S0nRX0PEE2eCO508qBXD2w2a5Wuo91p00ki4WKX8hW2e9GN0wfUIb0Nm00)

That's easy to say. The harder part is *doing it*.

---

## Conway's Law 

An observation about teams:

> Organizations which design systems ... are constrained to
> produce designs which are copies of the communication
> structures of these organizations

Perhaps it's a good idea if teams *also* have high cohesion and low coupling

![](http://www.plantuml.com/plantuml/svg/NSvD2i8m4CNn_PpYaNtkqDQF4tY18GurE2sIf0iHxousaKxT_kOD_BjSE9LbKg87XpkF0dSHdj0xS0RZHGI4c88ANAzZ56TWa5JsSf0GzVaL0jOzFEfi0uyw8zIJ9Nh_fmnhoh1FEV9D8piZ9qwpv6Bfd7WEabjDVO57MnhoQ5F2xsfmRTf2wnDH9_DrcVG3)

---

## Software Architecture Styles and Patterns

Software engineers learn from the solutions they have seen before. 
We tend to call these software achitecture styles or patterns.

What's a "style" vs what's a "pattern"?

* Textbooks often discuss styles as being more general and patterns as more solution-focused

* Realistically, though it comes from the origin of the terminology


---

## Design Patterns & Christopher Alexander

![The Timeless way of Building](https://upload.wikimedia.org/wikipedia/en/b/bf/The_Timeless_Way_of_Building.jpg)
![A Pattern Language](https://upload.wikimedia.org/wikipedia/en/e/e6/A_Pattern_Language.jpg)

In the 1970s, Christopher Alexander wrote books examining how different communities (villages, etc) grew and identifying
common solutions that people had come up with to problems. He documented these as *design patterns*

Example: *South-Facing Outdoors*

* "People use open space if it is sunny, and do
not use it if it isn't, in all but desert climates"

* "Always place buildings to the north* of the
outdoor spaces that go with them, and keep
the outdoor spaces to the south*. Never leave
a deep band of shade between the building
and the sunny part of the outdoors"

* Related patterns: Half-Hidden Garden,
Positive Outdoor Space, Wings of Light,
Indoor Sunlight, North* Face, Sunny Place

---

### Design Patterns in Software

Christopher Alexander described a pattern as essentially a triple:

* A context
* A problem
* A solution

This same description of patterns in code became popular in software with the *Gang of Four* book:

![Design Patterns (Gang of Four book)](https://upload.wikimedia.org/wikipedia/en/7/78/Design_Patterns_cover.jpg)

That book talks about patterns in (lower-level, early 2000s) Java code, but the terminology of a pattern being a context, a problem, and a solution has been re-used in other contexts (including for software architecture).

---

## Software Architecture Styles

We're not always going to be presenting particular contexts and problems, just common ways that software tends to be designed,
so let's use the term *software architecture styles* for these.


---

## Layered Architectures

Each layer in a layered architecture deals with a different level of abstraction

![](https://developer.ibm.com/developer/tutorials/l-virtual-filesystem-switch/images/figure2.gif)

image: https://developer.ibm.com/tutorials/l-virtual-filesystem-switch/

Typically, for each layer, there is more than one way the layer below could be implemented.

---

## Layered Architectures

![](https://upload.wikimedia.org/wikipedia/commons/3/3b/UDP_encapsulation.svg)

image: Colin Burnett

---

## TCP over ...?

```
Network Working Group                                        D. Waitzman
Request for Comments: 1149                                       BBN STC
                                                            1 April 1990

   A Standard for the Transmission of IP Datagrams on Avian Carriers

Status of this Memo

   This memo describes an experimental method for the encapsulation of
   IP datagrams in avian carriers.  This specification is primarily
   useful in Metropolitan Area Networks.  This is an experimental, not
   recommended standard.  Distribution of this memo is unlimited.

Overview and Rational

   Avian carriers can provide high delay, low throughput, and low
   altitude service.  The connection topology is limited to a single
   point-to-point path for each carrier, used with standard carriers,
   but many carriers can be used without significant interference with
   each other, outside of early spring.  This is because of the 3D ether
   space available to the carriers, in contrast to the 1D ether used by
   IEEE802.3.  The carriers have an intrinsic collision avoidance
   system, which increases availability.  Unlike some network
   technologies, such as packet radio, communication is not limited to
   line-of-sight distance.  Connection oriented service is available in
   some cities, usually based upon a central hub topology.

Frame Format

   The IP datagram is printed, on a small scroll of paper, in
   hexadecimal, with each octet separated by whitestuff and blackstuff.
   The scroll of paper is wrapped around one leg of the avian carrier.
   A band of duct tape is used to secure the datagram's edges.  The
   bandwidth is limited to the leg length.  The MTU is variable, and
   paradoxically, generally increases with increased carrier age.  A
   typical MTU is 256 milligrams.  Some datagram padding may be needed.

   Upon receipt, the duct tape is removed and the paper copy of the
   datagram is optically scanned into a electronically transmittable
   form.

Discussion

   Multiple types of service can be provided with a prioritized pecking
   order.  An additional property is built-in worm detection and
   eradication.  Because IP only guarantees best effort delivery, loss
   of a carrier can be tolerated.  With time, the carriers are self-

```

---

## n-Tier applications

An image of an application

![Three layers](http://www.plantuml.com/plantuml/svg/TP2n2eCm48RtFCNXr4Ne8oXrTt3eK3gSoL52R0ovQvVITwynbO3Wzil_VvzS6XQCydfEAg1s-22ehZJ2PRBafOAFKW2t_7K7P71QgQyguuxOoHoE0RC3R3ySZ-rkt7eJPskE0Sn08GQARuhRiWrXiXh5pXvuRV8Hl9Ifxr0GK6jYNekkxLUVGS4HETttJycOVkGqrATpdHIcH5iYbhZOSjuagY5lbhV-0G00)

While we might not think of replacing the server, we might think of replacing the database.

*"Use Apache Derby in development, PostgreSQL in production"*

---

## Pipe and Filter 

Pipe and filter architectures are useful whenever there can be successive stages of processing.

![Pipe and filter](http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuU8gIaqkISnBpqbLK4fEB55II2nM0DB8mkb5gGLWyNH3T645tJA8Z16oJ4vgSJ5O6CJWuW8Qfw1h1zIjm0N489OHa6K4P44LEAJcfG3T0W00)

```sh
cat out.log | grep ERROR | grep -v network | less
```


---

## Related - streams and filters

It is quite common to have a stream of events reaching a bus. You might, however, want to be able to listen to a filtered stream

* *"Just give me the messages from Australian senders"*

or to transform them in some way

* *"Now translate those messages into French for me"*

---

## Enterprise service bus

![Service bus](http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuU9AIIn9J4eiJbLGyaqjBavCJrKeB4qjJLLII2nMACejLAZcgkNYYlRDJodDILLmZ5MmqRK3YIF4d41Yw8BEo8900gmD9kaIgy35vP2Qbm9q0000)

* Different processes within a company listen to a shared message queue. 

* Each process might listen to a filtered version of the stream. e.g., the provisioning service only cares about "user created" events


---

## Broker architectures

A *broker* receives messages and passes them on to services that are available. e.g., maitre d at a restaurant finding you an available table.

![](http://www.plantuml.com/plantuml/svg/SoWkIImgAStDuU9ISix9JCqhKUBYoijFILLmAihFJYs2SiBpYu0SXSHYXKHqWIHqWMGkBeX92ZQwkdOmSo0KH2WHXPU4p0FfTaZDIm6w2000)

Common uses in tech - 

* Load-balancing requests between many servers

* Automated fail-over if a server becomes unavailable

* Task-runners. e.g., if a continuous integration server has many projects to build, it might have several worker machines that it can set to work doing the building.

---




package supercollaborative.versioncontrol.gitsim

class TestLayout extends munit.FunSuite {

  test("TemporalTopological puts the current commit at the beginning") {
    val g = Git.init.commit("A", "A", 1).commit("B", "B", 2)
    val h = g.head.commit
    assertEquals(h, temporalTopological(Seq(h)).head)
  }

  test("Lays out simple graph in a line") {
    val g = Git.init.commit("A", "A", 1).commit("B", "B", 2)
    val c2 = g.head.commit
    val c1 = g.head.commit.parents(0)
    assertEquals(Seq((c2, 0), (c1, 0), (Commit.Empty, 0)),  layoutRefs(Seq(g.head)))
  }

}

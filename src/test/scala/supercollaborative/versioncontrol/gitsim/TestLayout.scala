package supercollaborative.versioncontrol.gitsim

class TestLayout extends munit.FunSuite {

  test("TemporalTopological puts the current commit at the end") {
    val g = Git.init.commit("A", "A", 1).commit("B", "B", 2)
    val h = g.head.commit
    assertEquals(h, temporalTopological(Seq(h)).last)
  }

}

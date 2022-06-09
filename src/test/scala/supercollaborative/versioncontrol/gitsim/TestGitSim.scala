package supercollaborative.versioncontrol.gitsim

class TestGitSim extends munit.FunSuite {

  test("commit contains branch parent") {
    val g = Git.init.commit("A", "A", 1)
    val g2 = g.commit("B", "B", 2)

    assertEquals(g2.head.commit.parents, Seq(g.head.commit))
  }

  test("commit after switch is on correct branch") {
    val g = Git.init
      .commit("A", "A", 1)
      .commit("B", "B", 2)
      .branch("feature")
      .switch("feature")
      .commit("C", "C", 2)
      .switch("main")
      .commit("D", "D", 2)

    

    assertEquals(g.head.commit.parents.map(_.comment), Seq("B"))
  }

}
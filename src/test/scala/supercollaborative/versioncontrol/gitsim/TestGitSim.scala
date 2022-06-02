package supercollaborative.versioncontrol.gitsim

class TestGitSim extends munit.FunSuite {

  test("commit contains branch parent") {
    val g = Git.init.commit("A", "A", 1)
    val g2 = g.commit("B", "B", 2)

    assertEquals(g2.head.commit.parents, Seq(g.head.commit))
  }

}
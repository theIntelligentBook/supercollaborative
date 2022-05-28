package supercollaborative.versioncontrol.gitsim

class DiffTest extends munit.FunSuite {

  test("lcs") {
    assertEquals(longestCommonSubsequence("ello", "ollow").mkString, "llo")
    assertEquals(longestCommonSubsequence("hello", "hollow").mkString, "hllo")
    assertEquals(longestCommonSubsequence("hello", "hollo").mkString, "hllo")
    assertEquals(longestCommonSubsequence("ello", "hollow").mkString, "llo")
    assertEquals(longestCommonSubsequence("hellohello", "hollow").mkString, "hollo")
  }

}
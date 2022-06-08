package supercollaborative.versioncontrol.gitsim

class TestDiff extends munit.FunSuite {

  test("lcs") {
    assertEquals(longestCommonSubsequence("AE_L", "EL").mkString, "EL")
    assertEquals(longestCommonSubsequence("ello", "ollow").mkString, "llo")
    assertEquals(longestCommonSubsequence("hello", "hollow").mkString, "hllo")
    assertEquals(longestCommonSubsequence("hello", "hollo").mkString, "hllo")
    assertEquals(longestCommonSubsequence("ello", "hollow").mkString, "llo")
    assertEquals(longestCommonSubsequence("hellohello", "hollow").mkString, "hollo")
    assertEquals(longestCommonSubsequence("AND_LAID_HIM_ON_THE_GREEN", "AND_LADY_MOND_GREEN").mkString, "AND_LAD_MON_GREEN")
  }

}
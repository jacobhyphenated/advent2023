package com.jacobhyphenated.advent2023.day24

import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class TestDay24 {

  val input = """
    19, 13, 30 @ -2,  1, -2
    18, 19, 22 @ -1, -1, -2
    20, 25, 34 @ -2, -2, -4
    12, 31, 28 @ -1, -2, -1
    20, 19, 15 @  1, -5, -3
  """.trimIndent().lines()

  @Test
  fun testPart1() {
    val day = Day24()
    val lines = day.parseInput(input)
    assertEquals(2, day.countIntersectionsInRange(lines, 7, 27))
  }

  @Test
  @Ignore
  fun testPart2() {
    val day = Day24()
    assertEquals(47L, day.part2(input))
  }

}
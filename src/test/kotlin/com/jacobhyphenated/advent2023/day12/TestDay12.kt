package com.jacobhyphenated.advent2023.day12

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay12 {

  @Test
  fun testArrangementValid() {
    val day = Day12()
    assertEquals(false, day.isArrangementInvalid(".###.##.#...".toCharArray().toList(), listOf(3,2,1)) )
  }

  @Test
  fun testPart1() {
    val input = """
      ???.### 1,1,3
      .??..??...?##. 1,1,3
      ?#?#?#?#?#?#?#? 1,3,1,6
      ????.#...#... 4,1,1
      ????.######..#####. 1,6,5
      ?###???????? 3,2,1
    """.trimIndent()
    val day = Day12()
    assertEquals(21, day.part1(day.parseInput(input)))
  }

  @Test
  fun testPart2() {
    val day = Day12()
    assertEquals(1, day.part2(day.parseInput("???.### 1,1,3")))
    assertEquals(16, day.part2(day.parseInput("????.#...#... 4,1,1")))
    assertEquals(506250, day.part2(day.parseInput("?###???????? 3,2,1")))
  }
}
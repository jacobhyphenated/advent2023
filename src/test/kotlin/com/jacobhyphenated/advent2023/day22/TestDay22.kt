package com.jacobhyphenated.advent2023.day22

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay22 {

  private val testInput = """
    1,0,1~1,2,1
    0,0,2~2,0,2
    0,2,3~2,2,3
    0,0,4~0,2,4
    2,0,5~2,2,5
    0,1,6~2,1,6
    1,1,8~1,1,9
  """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day22()
    val input = day.parseInput(testInput)
    assertEquals(5, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day22()
    val input = day.parseInput(testInput)
    assertEquals(7, day.part2(input))
  }
}
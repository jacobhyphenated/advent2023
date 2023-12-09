package com.jacobhyphenated.advent2023.day9

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay9 {

  private val input = """
    0 3 6 9 12 15
    1 3 6 10 15 21
    10 13 16 21 30 45
   """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day9()
    assertEquals(114, day.part1(day.parseInput(input)))
  }

  @Test
  fun testPart2() {
    val day = Day9()
    assertEquals(2, day.part2(day.parseInput(input)))
  }
}
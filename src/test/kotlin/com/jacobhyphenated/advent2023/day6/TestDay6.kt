package com.jacobhyphenated.advent2023.day6

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay6 {

  private val testInput = """
    Time:      7  15   30
    Distance:  9  40  200
  """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day6()
    val input = day.parseInput(testInput)
    assertEquals(288L, day.part1(input))
  }
}
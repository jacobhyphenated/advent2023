package com.jacobhyphenated.advent2023.day7

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay7 {

  private val testInput = """
    32T3K 765
    T55J5 684
    KK677 28
    KTJJT 220
    QQQJA 483
  """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day7()
    val input = day.parseInput(testInput)
    assertEquals(6440, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day7()
    val input = day.parseInput(testInput)
    assertEquals(5905, day.part2(input))
  }
}
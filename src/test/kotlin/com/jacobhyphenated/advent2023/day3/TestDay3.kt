package com.jacobhyphenated.advent2023.day3

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay3 {
  private val input = """
    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...${'$'}.*....
    .664.598..
  """.trimIndent().lines().map { it.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day3 = Day3()
    assertEquals(4361, day3.part1(input))
  }

  @Test
  fun testPart2() {
    val day3 = Day3()
    assertEquals(467835, day3.part2(input))
  }
}
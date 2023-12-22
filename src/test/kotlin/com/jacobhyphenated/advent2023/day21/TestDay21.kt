package com.jacobhyphenated.advent2023.day21

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay21 {

  private val testInput = """
    ...........
    .....###.#.
    .###.##..#.
    ..#.#...#..
    ....#.#....
    .##..S####.
    .##..#...#.
    .......##..
    .##.#.####.
    .##..##.##.
    ...........
  """.trimIndent().lines().map { it.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day = Day21()
    assertEquals(16, day.countPossibleLocations(testInput, 6))
  }

  @Test
  fun testPart2() {
    val day = Day21()
    assertEquals(16, day.countPossibleLocations(testInput, 50))
  }
}
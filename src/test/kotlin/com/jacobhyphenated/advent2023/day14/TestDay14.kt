package com.jacobhyphenated.advent2023.day14

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay14 {

  private val input = """
    O....#....
    O.OO#....#
    .....##...
    OO.#O....O
    .O.....O#.
    O.#..O.#.#
    ..O..#O..O
    .......O..
    #....###..
    #OO..#....
  """.trimIndent().lines().map { line -> line.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day = Day14()
    assertEquals(136, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day14()
    assertEquals(64, day.part2(input))
  }
}
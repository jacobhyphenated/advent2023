package com.jacobhyphenated.advent2023.day13

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay13 {

  val input = """
    #.##..##.
    ..#.##.#.
    ##......#
    ##......#
    ..#.##.#.
    ..##..##.
    #.#.##.#.

    #...##..#
    #....#..#
    ..##..###
    #####.##.
    #####.##.
    ..##..###
    #....#..#
  """.trimIndent().split("\n\n").map { rocks ->
    rocks.lines().map { it.toCharArray().toList() }
  }

  @Test
  fun testPart1() {
    val day = Day13()
    assertEquals(405, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day13()
    assertEquals(400, day.part2(input))
  }

  @Test
  fun testPart2Limited() {
    val longInput ="""
      ...#.#.##
      .#####.##
      .#.##.#..
      .#.##.#..
      .#####.##
      ...#.#.##
      ###..##..
      ####.####
      #..#.#.#.
      ##...#...
      ...###...
      #####....
      #..##..##
    """.trimIndent().split("\n\n").map { rocks ->
      rocks.lines().map { it.toCharArray().toList() }
    }
    val day = Day13()
    assertEquals(8, day.part2(longInput))
  }
}
package com.jacobhyphenated.advent2023.day23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay23 {

  val input = """
    #.#####################
    #.......#########...###
    #######.#########.#.###
    ###.....#.>.>.###.#.###
    ###v#####.#v#.###.#.###
    ###.>...#.#.#.....#...#
    ###v###.#.#.#########.#
    ###...#.#.#.......#...#
    #####.#.#.#######.#.###
    #.....#.#.#.......#...#
    #.#####.#.#.#########v#
    #.#...#...#...###...>.#
    #.#.#v#######v###.###v#
    #...#.>.#...>.>.#.###.#
    #####v#.#.###v#.#.###.#
    #.....#...#...#.#.#...#
    #.#########.###.#.#.###
    #...###...#...#...#.###
    ###.###.#.###v#####v###
    #...#...#.#.>.>.#.>.###
    #.###.###.#.###.#.#v###
    #.....###...###...#...#
    #####################.#
  """.trimIndent().lines().map { it.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day = Day23()
    assertEquals(94, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day23()
    assertEquals(154, day.part2(input))
  }
}
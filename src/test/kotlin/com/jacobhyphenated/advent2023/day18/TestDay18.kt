package com.jacobhyphenated.advent2023.day18

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay18 {
  private val inputString = """
    R 6 (#70c710)
    D 5 (#0dc571)
    L 2 (#5713f0)
    D 2 (#d2c081)
    R 2 (#59c680)
    D 2 (#411b91)
    L 5 (#8ceee2)
    U 2 (#caa173)
    L 1 (#1b58a2)
    U 2 (#caa171)
    R 2 (#7807d2)
    U 3 (#a77fa3)
    L 2 (#015232)
    U 2 (#7a21e3)
  """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day18()
    val input = day.parseInput(inputString)
    assertEquals(62, day.part1(input))
  }

  @Test
  fun testPart2() {
    val day = Day18()
    val input = day.parseInput(inputString)
    assertEquals(952408144115, day.part2(input))
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day18().run()
}
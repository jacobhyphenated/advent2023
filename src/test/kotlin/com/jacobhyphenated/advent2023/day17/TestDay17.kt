package com.jacobhyphenated.advent2023.day17

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay17 {

  private val testInput = """
    2413432311323
    3215453535623
    3255245654254
    3446585845452
    4546657867536
    1438598798454
    4457876987766
    3637877979653
    4654967986887
    4564679986453
    1224686865563
    2546548887735
    4322674655533
  """.trimIndent().lines().map { line -> line.toCharArray().map { it.digitToInt() } }

  @Test
  fun testPart1() {
    val day = Day17()
    assertEquals(102, day.part1(testInput))
  }

  @Test
  fun testPart2() {
    val day = Day17()
    assertEquals(94, day.part2(testInput))
  }

  @Test
  fun testPart2Hard() {
    val input = """
      111111111111
      999999999991
      999999999991
      999999999991
      999999999991
    """.trimIndent().lines().map { line -> line.toCharArray().map { it.digitToInt() } }
    val day = Day17()
    assertEquals(71, day.part2(input))
  }
}
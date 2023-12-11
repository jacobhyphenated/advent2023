package com.jacobhyphenated.advent2023.day11

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay11 {
  private val input = """
      ...#......
      .......#..
      #.........
      ..........
      ......#...
      .#........
      .........#
      ..........
      .......#..
      #...#.....
    """.trimIndent().lines().map { it.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day = Day11()
    assertEquals(374, day.part1(input))
  }

  @Test
  fun testCalcDistanceAccountingForExpansion() {
    val blankRows = input.indices.filter { row -> input[row].all { it == '.' } }
    val blankCols = input[0].indices.filter { col -> input.indices.all { row -> input[row][col] == '.' } }
    val g1 = Pair(0,3)
    val g7 = Pair(8, 7)
    val day = Day11()
    assertEquals(15, day.calcDistanceAccountingForExpansion(g1, g7, blankRows, blankCols, 2))
  }
}
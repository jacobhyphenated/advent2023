package com.jacobhyphenated.advent2023.day16

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay16 {

  private val testInput = """
    .|...\....
    |.-.\.....
    .....|-...
    ........|.
    ..........
    .........\
    ..../.\\..
    .-.-/..|..
    .|....-|.\
    ..//.|....
  """.trimIndent().lines().map { it.toCharArray().toList() }

  @Test
  fun testPart1() {
    val day = Day16()
    assertEquals(46, day.part1(testInput))
  }

  @Test
  fun testPart2() {
    val day = Day16()
    assertEquals(51, day.part2(testInput))
  }
}
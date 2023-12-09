package com.jacobhyphenated.advent2023.day8

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay8 {

  @Test
  fun testPart1() {
    val input = """
      LLR

      AAA = (BBB, BBB)
      BBB = (AAA, ZZZ)
      ZZZ = (ZZZ, ZZZ)
    """.trimIndent()
    val day = Day8()
    assertEquals(6, day.part1(day.parseInput(input)))
  }

  @Test
  fun testPart2() {
    val input = """
      LR

      11A = (11B, XXX)
      11B = (XXX, 11Z)
      11Z = (11B, XXX)
      22A = (22B, XXX)
      22B = (22C, 22C)
      22C = (22Z, 22Z)
      22Z = (22B, 22B)
      XXX = (XXX, XXX)
    """.trimIndent()
    val day = Day8()
    assertEquals(6, day.part2(day.parseInput(input)))
  }

  @Test
  fun testLcm() {
    assertEquals(24L, lcm(listOf(1, 2, 8, 3)))
    assertEquals(252L, lcm(listOf(2, 7, 3, 9, 4)))
  }
}
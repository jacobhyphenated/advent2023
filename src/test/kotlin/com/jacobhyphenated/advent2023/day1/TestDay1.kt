package com.jacobhyphenated.advent2023.day1
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay1 {

  @Test
  fun testPart1() {
    val input = """
      1abc2
      pqr3stu8vwx
      a1b2c3d4e5f
      treb7uchet
    """.trimIndent().lines()

    val day1 = Day1()
    assertEquals(142, day1.part1(input))
  }

  @Test
  fun testPart2() {
    val input = """
      two1nine
      eightwothree
      abcone2threexyz
      xtwone3four
      4nineeightseven2
      zoneight234
      7pqrstsixteen
    """.trimIndent().lines()
    val day1 = Day1()
    assertEquals(281, day1.part2(input))
  }
}
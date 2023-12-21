package com.jacobhyphenated.advent2023.day20

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay20 {
  private val input ="""
    broadcaster -> a
    %a -> inv, con
    &inv -> b
    %b -> con
    &con -> output
  """.trimIndent()

  @Test
  fun testPart1() {
    val day = Day20()
    assertEquals(11687500, day.part1(input))
  }
}
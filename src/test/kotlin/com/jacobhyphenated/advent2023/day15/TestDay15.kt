package com.jacobhyphenated.advent2023.day15

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay15 {

  private val testInput = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"

  @Test
  fun testPart1() {
    val day = Day15()
    assertEquals(1320, day.part1(testInput))
  }

  @Test
  fun testPart2() {
    val day = Day15()
    assertEquals(145, day.part2(testInput))
  }
}
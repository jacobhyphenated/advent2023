package com.jacobhyphenated.advent2023.day10

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestDay10 {

  @Test
  fun testPart1() {
    val input = """
      ..F7.
      .FJ|.
      SJ.L7
      |F--J
      LJ...
    """.trimIndent().lines().map { it.toCharArray().asList() }

    val day = Day10()
    assertEquals(8, day.part1(input))
  }

  @Test
  fun testPart2Simple() {
    val input ="""
      ...........
      .S-------7.
      .|F-----7|.
      .||.....||.
      .||.....||.
      .|L-7.F-J|.
      .|..|.|..|.
      .L--J.L--J.
      ...........
    """.trimIndent().lines().map { it.toCharArray().asList() }
    val day = Day10()
    assertEquals(4, day.part2(input))
  }

  @Test
  fun testPart2() {
    val input = """
      FF7FSF7F7F7F7F7F---7
      L|LJ||||||||||||F--J
      FL-7LJLJ||||||LJL-77
      F--JF--7||LJLJIF7FJ-
      L---JF-JLJIIIIFJLJJ7
      |F|F-JF---7IIIL7L|7|
      |FFJF7L7F-JF7IIL---7
      7-L-JL7||F7|L7F-7F7|
      L.L7LFJ|||||FJL7||LJ
      L7JLJL-JLJLJL--JLJ.L
    """.trimIndent().lines().map { it.toCharArray().asList() }
    val day = Day10()
    assertEquals(10, day.part2(input))
  }
}
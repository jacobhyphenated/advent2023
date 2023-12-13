package com.jacobhyphenated.advent2023.day13

import com.jacobhyphenated.advent2023.Day
import kotlin.math.min

/**
 * Day 13: Point of Incidence
 *
 * The puzzle input is a grid of rocks and empty spaces.
 * But a mirror reflects the rocks. The reelection point can be
 * along a row or a column. If the reflection goes off the edge of the rock formation,
 * a row/col might not have a corresponding reflected row/col (reflections do not need to be in the middle)
 */
class Day13: Day<List<List<List<Char>>>> {
  override fun getInput(): List<List<List<Char>>> {
    return readInputFile("13").split("\n\n").map { rocks ->
      rocks.lines().map { it.toCharArray().toList() }
    }
  }

  /**
   * Part 1: count the rows above a horizontal reflection and multiply by 100
   * or count the number of columns to the left of a vertical reflection line.
   * return the sum for each rock formation
   */
  override fun part1(input: List<List<List<Char>>>): Int {
    return input.sumOf { calculateRockMirrorReflection(it)!! }
  }

  /**
   * Part 2: There is actually a smudge on the mirror.
   * A single space is actually a reverse character.
   *
   * Find the new score created by the only possible smudged space that creates a new reflection.
   * The old reflection may or may not still be valid, but only use the new reflection
   */
  override fun part2(input: List<List<List<Char>>>): Int {
    return input.sumOf { findSmudge(it) }
  }

  /**
   * Brute force. Try each and every space in the grid.
   * Change the value and see if that gives a new reflection point
   */
  private fun findSmudge(rocks: List<List<Char>>): Int {
    val originalScore = calculateRockMirrorReflection(rocks)
    for (r in rocks.indices) {
      for (c in rocks[r].indices) {
        val smudge = rocks.toMutableList().map { it.toMutableList() }
        smudge[r][c] = when(smudge[r][c]) {
          '.' -> '#'
          '#' -> '.'
          else -> throw IllegalArgumentException("weird rock")
        }
        val score = calculateRockMirrorReflection(smudge, originalScore!!)
        if (score != null && score != originalScore) {
          return score
        }
      }
    }
    println("NO SMUDGE")
    println(rocks.joinToString("\n"){ it.joinToString("") })
    return 0
  }

  /**
   * Find the reflection point. First go down the rows, then go down the columns.
   * Stop once we've found a valid reflection
   *
   * @param exclude is used for part 2. If we find a reflection with the same score as [exclude]
   * then continue searching until we find a different reflection
   */
  private fun calculateRockMirrorReflection(rocks: List<List<Char>>, exclude: Int = 0): Int? {
    reflectionPoint@ for (r in (1 until rocks.size)) {
      val toEnd = rocks.size - 1 - r
      val toBeginning = r - 1
      for (distance in 0..min(toEnd, toBeginning)) {
        if (rocks[r - 1 - distance] != rocks[r + distance]) {
          continue@reflectionPoint
        }
      }
      if(r * 100 != exclude) {
        return r * 100
      }
    }
    reflectionPointC@ for (c in 1 until rocks[0].size) {
      val toEnd = rocks[0].size - 1 - c
      val toBeginning = c - 1
      for (distance in 0 .. min(toEnd, toBeginning)) {
        if (rocks.map { row -> row[c - 1 - distance] } != rocks.map { row -> row[c + distance] }){
          continue@reflectionPointC
        }
      }
      if (c != exclude) {
        return c
      }
    }
    return null
  }

  override fun warmup(input: List<List<List<Char>>>) {
    part2(input)
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day13().run()
}
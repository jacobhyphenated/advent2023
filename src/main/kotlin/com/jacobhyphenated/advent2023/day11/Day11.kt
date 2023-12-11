package com.jacobhyphenated.advent2023.day11

import com.jacobhyphenated.advent2023.Day
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Day 11: Cosmic Expansion
 *
 * The puzzle input is a representation of observed galaxies in space.
 * However, all rows with no galaxies and all columns with no galaxies undergo expansion and are larger than they appear
 */
class Day11: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("11").lines().map { it.toCharArray().toList() }
  }

  /**
   * Part 1: Each fully blank row is actually 2 blank rows. Same with columns.
   *
   * Given that, what is the total distance between every galaxy?
   * Distance is calculated by moving only up/down, left/right (manhattan distance)
   *
   * Calculate the distance between galaxies in any order, but don't repeat
   */
  override fun part1(input: List<List<Char>>): Int {
    // first, find the blank rows and columns that will undergo "expansion"
    val blankRows = input.indices.filter { row -> input[row].all { it == '.' } }
    val blankCols = input[0].indices.filter { col -> input.indices.all { row -> input[row][col] == '.' } }

    // build a new "universe" by doubling the blank rows and columns
    val expandedUniverse = input.flatMapIndexed { r, row ->
      val expanded = row.flatMapIndexed{ c, value -> if (c in blankCols) { listOf(value, '.') } else { listOf(value) } }
      if (r in blankRows) {
        listOf(expanded, expanded)
      } else {
        listOf(expanded)
      }
    }

    // now find the location of each galaxy in the expanded universe
    val galaxies = expandedUniverse.indices
      .flatMap { r -> expandedUniverse[r].indices.map { c ->
        if(expandedUniverse[r][c] == '#') {
          Pair(r,c)
        } else {
          null
        }
      }}.filterNotNull()

    var sum = 0
    for (i in galaxies.indices) {
      for (j in i until galaxies.size) {
        sum += calcManhattanDistance(galaxies[i], galaxies[j])
      }
    }
    return sum
  }

  /**
   * Part 2: Rather than doubling the blank rows and columns, each one expands by 1 Million.
   */
  override fun part2(input: List<List<Char>>): Long {
    // For this, we don't worry about modifying the universe. Keep the original shape
    val blankRows = input.indices.filter { row -> input[row].all { it == '.' } }
    val blankCols = input[0].indices.filter { col -> input.indices.all { row -> input[row][col] == '.' } }
    val galaxies = input.indices
      .flatMap { r -> input[r].indices.map { c ->
        if(input[r][c] == '#') {
          Pair(r,c)
        } else {
          null
        }
      }}.filterNotNull()

    var sum = 0L
    for (i in galaxies.indices) {
      for (j in i until galaxies.size) {
        sum += calcDistanceAccountingForExpansion(galaxies[i], galaxies[j], blankRows, blankCols, 1_000_000)
      }
    }
    return sum
  }

  /** for part 2, we modify our manhattan distance algorithm
   * if a step crosses an expansion row or column, count that as [expansion] units instead of 1
   * where expansion is 1,000,000 for part 2
   */
  fun calcDistanceAccountingForExpansion(p1: Pair<Int,Int>,
                                                 p2: Pair<Int,Int>,
                                                 blankRows: List<Int>,
                                                 blankCols: List<Int>,
                                                 expansion: Long): Long {
    val (r1, c1) = p1
    val (r2, c2) = p2

    val rowRange = min(r1, r2) .. max(r1, r2)
    val colRange = min(c1, c2) .. max(c1, c2)
    val expansionOverlap = rowRange.count { it in blankRows } + colRange.count { it in blankCols }

    // just don't forget to subtract expansionOverlap to prevent double counting
    return calcManhattanDistance(p1, p2).toLong() + expansionOverlap.toLong() * expansion - expansionOverlap
  }

  override fun warmup(input: List<List<Char>>) {
    part2(input)
  }
}

private fun calcManhattanDistance(p1: Pair<Int,Int>, p2: Pair<Int,Int>): Int {
  val (x1,y1) = p1
  val (x2, y2) = p2
  return (x1 - x2).absoluteValue + (y1 - y2).absoluteValue
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day11().run()
}
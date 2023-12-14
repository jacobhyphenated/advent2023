package com.jacobhyphenated.advent2023.day14

import com.jacobhyphenated.advent2023.Day

/**
 * Day 14: Parabolic Reflector Dish
 *
 * A platform has a bunch of rocks on it. There are controls to tilt the platform in any direction.
 * Round rocks ('O') will roll while square rocks ('#') will not move.
 *
 * The weight on the north pillar is calculated by looking at how many rocks are close to the north edge.
 * A rock on the farthest south edge is 0. One row up is 1, etc.
 */
class Day14: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("14").lines().map { it.toCharArray().toList() }
  }

  /**
   * Part 1: Tilt the platform north. All rocks move as far as they are able in the north direction.
   * What is the weight on the north pillar?
   */
  override fun part1(input: List<List<Char>>): Int {
    val modified = moveRocksNorth(input)
    return modified.flatMapIndexed{ i, row ->
      row.map { c -> if (c == 'O') { modified.size - i } else { 0 } }
    }.sum()
  }

  /**
   * Part 2: A full cycle tilts north, west, south, then east.
   * Do 1 billion (1_000_000_000) cycles. What is the weight on the north pillar?
   */
  override fun part2(input: List<List<Char>>): Int {
    var rocks = input
    val previousRocks = mutableMapOf(input to -1)

    val cycle = listOf(this::moveRocksNorth, this::moveRocksWest, this::moveRocksSouth, this::moveRocksEast)
    var i = 0
    var foundPattern = false
    while( i < 1_000_000_000) {
      rocks = cycle.fold(rocks){ r, tilt -> tilt(r) }
      if (!foundPattern && rocks in previousRocks) {
        foundPattern = true
        val patternDiff = i - previousRocks.getValue(rocks)
        val patternAdjust = (1_000_000_000 - i ) % patternDiff
        i = 1_000_000_000 - patternAdjust + 1
        continue
      }
      previousRocks[rocks] = i
      i++
    }

    return rocks.flatMapIndexed{ index, row ->
      row.map { c -> if (c == 'O') { rocks.size - index } else { 0 } }
    }.sum()
  }

  /**
   * Tilt all rocks north. I really want to come up with a nice functional programming solution
   * that doesn't require each of these to look so similar.
   *
   * Start at the top most row, we can greedily adjust each rock we find.
   */
  private fun moveRocksNorth(grid: List<List<Char>>): List<List<Char>> {
    val modified = grid.toMutableList().map { it.toMutableList() }
    for (r in modified.indices) {
      for (c in modified[r].indices) {
        if (modified[r][c] == 'O') {
          var rowIndex = r
          // go back up the rows until we find an occupied space
          for (checkRowIndex in r - 1 downTo 0) {
            if (modified[checkRowIndex][c] != '.') {
              break
            }
            rowIndex = checkRowIndex
          }
          modified[rowIndex][c] = 'O'
          if(r != rowIndex) {
            modified[r][c] = '.'
          }
        }
      }
    }
    return modified
  }
  private fun moveRocksSouth(grid: List<List<Char>>): List<List<Char>> {
    val modified = grid.toMutableList().map { it.toMutableList() }
    for (r in modified.size - 1 downTo 0) {
      for (c in modified[r].indices) {
        if (modified[r][c] == 'O') {
          var rowIndex = r
          for (checkRowIndex in r + 1 until modified.size) {
            if (modified[checkRowIndex][c] != '.') {
              break
            }
            rowIndex = checkRowIndex
          }
          modified[rowIndex][c] = 'O'
          if(r != rowIndex) {
            modified[r][c] = '.'
          }
        }
      }
    }
    return modified
  }

  private fun moveRocksWest(grid: List<List<Char>>): List<List<Char>> {
    val modified = grid.toMutableList().map { it.toMutableList() }
    for (c in modified[0].indices) {
      for (r in modified.indices) {
        if (modified[r][c] == 'O') {
          var colIndex = c
          for (checkColIndex in c - 1 downTo 0) {
            if (modified[r][checkColIndex] != '.') {
              break
            }
            colIndex = checkColIndex
          }
          modified[r][colIndex] = 'O'
          if(c != colIndex) {
            modified[r][c] = '.'
          }
        }
      }
    }
    return modified
  }

  private fun moveRocksEast(grid: List<List<Char>>): List<List<Char>> {
    val modified = grid.toMutableList().map { it.toMutableList() }
    for (c in modified[0].size - 1 downTo 0) {
      for (r in modified.indices) {
        if (modified[r][c] == 'O') {
          var colIndex = c
          for (checkColIndex in c + 1 until modified.size) {
            if (modified[r][checkColIndex] != '.') {
              break
            }
            colIndex = checkColIndex
          }
          modified[r][colIndex] = 'O'
          if(c != colIndex) {
            modified[r][c] = '.'
          }
        }
      }
    }
    return modified
  }

  override fun warmup(input: List<List<Char>>) {
    part1(input)
  }
}
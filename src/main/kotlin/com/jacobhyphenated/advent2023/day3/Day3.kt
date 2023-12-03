package com.jacobhyphenated.advent2023.day3

import com.jacobhyphenated.advent2023.Day
import com.jacobhyphenated.advent2023.product

/**
 * Day 3: Gear Ratios
 *
 * A 2d array contains numbers, periods, and symbols.
 * Continuous numbers for a single integer, periods are empty spaces.
 *
 * A Part number is any number that is adjacent (including diagonally) to a symbol
 * example:
 * 467..114..
 * ...*......
 * ..35..633.
 * ......#...
 *
 * 114 is not a part number, the others are
 */
class Day3: Day<List<List<Char>>> {

  private val digits = ('0' .. '9').toSet()

  override fun getInput(): List<List<Char>> {
    return readInputFile("3").lines().map { it.toCharArray().toList() }
  }

  /**
   * Part 1: What is the sum of all the part numbers?
   *
   * loop through the grid, when we encounter digits, construct the integer number
   * while check to see if a symbol is adjacent to any digit within the number
   *
   */
  override fun part1(input: List<List<Char>>): Int {
    var partNumbers = 0
    for (row in input.indices) {
      var buffer = ""
      var adjacentToSymbol = false
      for (col in input[row].indices) {
        if (input[row][col] !in digits) {
          if (buffer != "" && adjacentToSymbol) {
            partNumbers += buffer.toInt()
          }
          buffer = ""
          adjacentToSymbol = false
        } else {
          buffer += input[row][col]
          adjacentToSymbol = adjacentToSymbol || hasAdjacentSymbol(input, row, col)
        }
      }
      // don't forget to check the buffer at the end of the row, in case the last character was a digit
      if (buffer != "" && adjacentToSymbol) {
        partNumbers += buffer.toInt()
      }
    }
    return partNumbers
  }

  /**
   * Part 2: A gear as a * symbol with exactly 2 adjacent numbers. The gear ratio is the product of those two numbers
   * Add up the gear ratios for all gears in the input
   */
  override fun part2(input: List<List<Char>>): Int {
    var gearRatio = 0
    for (row in input.indices) {
      for (col in input[row].indices) {
        if (input[row][col] == '*') {
          findGearNumbers(input, row, col)?.let { positions ->
            gearRatio += positions
              .map { (r,c) -> fullNumberAtPosition(input, r, c) }
              .product()
          }
        }
      }
    }
    return gearRatio
  }

  /**
   * Helper function to find all adjacent positions including diagonals
   * @param input the grid/puzzle input
   * @param row the baseline row we are looking around
   * @param col the baseline column we are looking around
   *
   * @return a list of positions (row, col) as a pair that are adjacent to the input row and column
   */
  private fun findAdjacentPositions(input: List<List<Char>>, row: Int, col: Int): List<Pair<Int,Int>> {
    val adjacent = mutableListOf<Pair<Int,Int>>()
    for (r in (row - 1).coerceAtLeast(0) .. (row + 1).coerceAtMost(input.size - 1)) {
      for (c in (col - 1).coerceAtLeast(0) .. (col + 1).coerceAtMost(input[r].size - 1)) {
        if (r == row && c == col) {
          continue
        }
        adjacent.add(Pair(r,c))
      }
    }
    return adjacent
  }

  /**
   * Check all the adjacent positions to see if any are a symbol. Return false otherwise
   */
  private fun hasAdjacentSymbol(input: List<List<Char>>, row: Int, col:Int): Boolean {
    return findAdjacentPositions(input, row, col).any { (r,c) ->  input[r][c] != '.' && input[r][c] !in digits }
  }

  /**
   * Gears are * symbols with exactly two adjacent numbers. The input row/col represent a * symbol.
   * Return the position (row,col) of the adjacent numbers if there are exactly two, null otherwise
   */
  private fun findGearNumbers(input: List<List<Char>>, row: Int, col:Int): List<Pair<Int,Int>>? {
    val adjacent = findAdjacentPositions(input, row, col)
    var lastNumberPosition: Pair<Int,Int>? = null
    // track the last adjacent digit to prevent duplicate entries for the same integer
    val adjacentNumbers = mutableListOf<Pair<Int, Int>>()
    for ((r,c) in adjacent) {
      if (input[r][c] in digits) {
        // if the digit also has a digit in the preceding column (that is adjacent to this symbol), it's a duplicate
        if (lastNumberPosition == null || lastNumberPosition.first != r || lastNumberPosition.second != c - 1) {
          adjacentNumbers.add(Pair(r,c))
        }
        lastNumberPosition = Pair(r,c)
      }
    }
    return if (adjacentNumbers.size == 2) { adjacentNumbers } else { null }
  }

  /**
   * The input row/col represents a single digit in the integer.
   * The integer may be composed of additional digits both before and after this digit.
   * Find and return the full integer
   */
  private fun fullNumberAtPosition(input: List<List<Char>>, row: Int, col: Int): Int {
    var startIndex = col
    while (startIndex >= 0 && input[row][startIndex] in digits) {
      startIndex--
    }
    var endIndex = col
    while (endIndex < input[row].size && input[row][endIndex] in digits) {
      endIndex++
    }
    return (startIndex + 1 until endIndex).map { input[row][it] }.joinToString("").toInt()
  }

  override fun warmup(input: List<List<Char>>) {
    part1(input)
    part2(input)
  }
}
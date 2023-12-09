package com.jacobhyphenated.advent2023.day9

import com.jacobhyphenated.advent2023.Day

/**
 * Day 9: Mirage Maintenance
 *
 * Each line of the puzzle input is a series of readings from an instrument.
 */
class Day9: Day<List<List<Int>>> {
  override fun getInput(): List<List<Int>> {
    return parseInput(readInputFile("9"))
  }

  /**
   * Part 1: Extrapolate the next number in the sequence
   *
   * Do this by finding the difference of each of the values.
   * If the difference of all values is not 0, repeat this process
   * example:
   * 1   3   6  10  15  21
   *  2   3   4   5   6
   *    1   1   1   1
   *      0   0   0
   *
   * To extrapolate the next number, add the last value of each of the difference lines
   *  1   3   6  10  15  21  28
   *    2   3   4   5   6   7
   *      1   1   1   1   1
   *        0   0   0   0
   *
   * Giving the value of 28. Add this value for each line of the puzzle input
   */
  override fun part1(input: List<List<Int>>): Any {
    return input.sumOf { measure ->
      val differences = mutableListOf(measure)
      var currentDiff = measure
      while (currentDiff.any { it != 0 }) {
        currentDiff = currentDiff.windowed(2).map { (a, b) -> b - a }
        differences.add(currentDiff)
      }
      differences.sumOf { it.last() }
    }
  }

  /**
   * Part 2: Now extrapolate the first value.
   * Instead of adding the last values fill in a 0 at the beginning of the final difference list,
   * and calculate the value in the previous list necessary to result in the below value:
   * 5  10  13  16  21  30  45
   *   5   3   3   5   9  15
   *    -2   0   2   4   6
   *       2   2   2   2
   *        0   0   0
   */
  override fun part2(input: List<List<Int>>): Any {
    return input.sumOf { measure ->
      val differences = mutableListOf(measure)
      var currentDiff = measure
      while (currentDiff.any { it != 0 }) {
        currentDiff = currentDiff.windowed(2).map { (a, b) -> b - a }
        differences.add(currentDiff)
      }
      differences.map { it.first() }
        .reversed()
        .reduce { a,b -> b - a }
    }
  }

  fun parseInput(input: String): List<List<Int>> {
    return input.lines().map { line -> line.split(" ").map { it.toInt() } }
  }

  override fun warmup(input: List<List<Int>>) {
    part1(input)
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day9().run()
}
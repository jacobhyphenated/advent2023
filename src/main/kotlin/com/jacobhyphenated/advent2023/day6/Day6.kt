package com.jacobhyphenated.advent2023.day6

import com.jacobhyphenated.advent2023.Day
import com.jacobhyphenated.advent2023.product

/**
 * Day 6: Wait For It
 *
 * You are participating in a boat race. The boats are charged by pressing a button for a length of time.
 * The longer the button is pressed, the faster the boat travels, but the boat doesn't move while pressed.
 */
class Day6: Day<List<Pair<Long,Long>>> {
  override fun getInput(): List<Pair<Long, Long>> {
    return parseInput(readInputFile("6"))
  }

  /**
   * Part 1: The puzzle has a set of numbers that represent the total time of the race, and the record distance.
   * There are several races represented.
   * Find how many solutions exist for beating the record time, and multiply those numbers for each race.
   */
  override fun part1(input: List<Pair<Long, Long>>): Long {
    return input.map { race ->
      val (time, distanceRecord) = race
      findWinningTimes(time, distanceRecord)
    }.product()
  }

  /**
   * Part 2: The puzzle is not several races, only one race. Remove the spaces between numbers of the puzzle input
   */
  override fun part2(input: List<Pair<Long, Long>>): Long {
    val (time, distanceRecord) = input.reduce { p1, p2 ->
      Pair("${p1.first}${p2.first}".toLong(), "${p1.second}${p2.second}".toLong())
    }
    return findWinningTimes(time, distanceRecord)
  }

  /**
   * Distance is a normal distribution with the longest distance achieved by pressing the button for 1/2 the total time
   * If we find the number of solutions that exceed the distance record on one side of the distribution,
   * we can double that value for the total number of solutions.
   *
   * Note: A binary search is faster. But since this solution solves part 2 in 10ms, I didn't bother.
   */
  private fun findWinningTimes(time: Long, distanceRecord: Long): Long {
    val half = time / 2
    var current = half
    while (distanceFromTime(time, current) > distanceRecord) {
      current --
    }
    val winningTimes = (half - current) * 2
    // Even numbers have an exact middle point - remove the duplicate count
    return if (time % 2 == 0L) { winningTimes - 1 } else { winningTimes }
  }

  private fun distanceFromTime(totalTime: Long, timeHoldingButton: Long): Long {
    return timeHoldingButton * (totalTime - timeHoldingButton)
  }

  fun parseInput(input: String): List<Pair<Long, Long>> {
    val intLines = input.lines().map { line ->
      line.split(":")[1]
        .trim()
        .split("\\s+".toRegex())
        .map { it.toLong() }
    }
    return (intLines[0].indices).map { Pair(intLines[0][it], intLines[1][it]) }
  }

  override fun warmup(input: List<Pair<Long, Long>>) {
    part2(input)
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day6().run()
}
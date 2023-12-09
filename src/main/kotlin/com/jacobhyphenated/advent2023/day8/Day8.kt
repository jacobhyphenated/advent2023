package com.jacobhyphenated.advent2023.day8

import com.jacobhyphenated.advent2023.Day

typealias Direction = Map<String, Pair<String,String>>

/**
 * Day 8: Haunted Wasteland
 *
 * The puzzle input consists of two parts. The first part is a set of instructions indicating which
 * direction to take (R = right, L = left). Those instructions repeat forever.
 * The second part maps a starting position to two possible ending positions (a left and a right)
 */
class Day8: Day<Pair<String, Direction>> {
  override fun getInput(): Pair<String, Direction> {
    return parseInput(readInputFile("8"))
  }

  /**
   * Part1: The starting position is AAA and the ending position is ZZZ
   * Following the instructions, how many steps does it take to get to the end?
   */
  override fun part1(input: Pair<String, Direction>): Int {
    val (instructionString, directions) = input
    val instructionGenerator = InfiniteGenerator(instructionString.toCharArray().toList())
    var current = "AAA"
    while (current != "ZZZ") {
      current = nextPosition(instructionGenerator, current, directions)
    }
    return instructionGenerator.num
  }

  /**
   * Part 2: There are multiple starting points (every position that ends with A)
   * and multiple ending points (every position that ends with Z)
   *
   * Starting at all possible starting locations at the same time, how many steps until
   * each position has reached and end point at the same time?
   */
  override fun part2(input: Pair<String, Direction>): Long {
    val (instructionString, directions) = input
    val currentPositions = directions.keys.filter { it.endsWith("A") }.toMutableList()

    // We need to look for where the pattern repeats.
    val lengthOffsets = currentPositions.map { start ->
      val instructionGenerator = InfiniteGenerator(instructionString.toCharArray().toList())
      val patternStart = mutableMapOf<String, Int>()
      var current = start
      do {
        patternStart[current] = instructionGenerator.num
        // the repetition must be a multiple of the instruction length
        // run through all instructions and find where the starting position has been seen before
        repeat(instructionString.length) {
          current = nextPosition(instructionGenerator, current, directions)
        }
      } while (current !in patternStart)
      // The pattern length is how many instructions happen before the pattern repeats itself
      val patterLength = instructionGenerator.num - patternStart.getValue(current)
      // the offset is some number of instructions from the beginning that are not included in the pattern
      val patternOffset = patternStart.getValue(current)
      Pair(patterLength.toLong(), patternOffset.toLong())
    }
    // I think in this puzzle input, the pattern offsets are all the same, so this might not be necessary
    val offset = lcm(lengthOffsets.map { it.second })

    // find the indexes in each pattern that are end destinations
    val endIndexes = currentPositions.mapIndexed { index, start ->
      var current = start
      val instructionGenerator = InfiniteGenerator(instructionString.toCharArray().toList())
      (0 until offset).forEach { _ ->
        current = nextPosition(instructionGenerator, current, directions)
      }
      val patternLength = lengthOffsets[index].first
      val endIndex = mutableListOf<Long>()
      repeat(patternLength.toInt()) {
        if (current.endsWith("Z")) {
          endIndex.add(it.toLong())
        }
        current = nextPosition(instructionGenerator, current, directions)
      }
      endIndex.last()
    }

    // we know the end index and the length of each pattern. Now we find where they line up.
    // compare 2 patterns at a time, and build on top of them.
    return endIndexes.mapIndexed { i, endIndex ->
      val patternLength = lengthOffsets[i].first
      Pair(endIndex, patternLength)
    }.reduce { (end1, length1), (end2, length2) ->
      var patternEnd = end1
      // note: as we reduce, length1 will be much larger than length2
      while (patternEnd % length2 != end2 ) {
        patternEnd += length1
      }
      // the new end index is the first intersection of the two end points
      // the new length is the LCM of the two lengths
      Pair(patternEnd, lcm(listOf(length1, length2)))
    }.first + offset
  }

  fun parseInput(input: String): Pair<String, Direction> {
    val (instructions, directionList) = input.split("\n\n")
    val directionMap = directionList.lines().map { line ->
      val (key, lrPair) = line.split(" = ")
      val (left, right) = lrPair.removeSuffix(")").removePrefix("(").split(", ")
      Pair(key, Pair(left, right))
    }.associate { (key, pair) -> key to pair }.toMap()
    return Pair(instructions, directionMap)
  }

  private fun nextPosition(generator: InfiniteGenerator<Char>, current: String, directions: Direction): String {
    return when(val instruction = generator.next()) {
      'L' -> directions.getValue(current).first
      'R' -> directions.getValue(current).second
      else -> throw IllegalArgumentException("Invalid instruction $instruction")
    }
  }

  override fun warmup(input: Pair<String, Direction>) {
    part1(input)
  }
}

class InfiniteGenerator<T>(private val order: List<T>) {
  var num = 0
  fun next(): T {
    return order[num % order.size].also { num++ }
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day8().run()
}
package com.jacobhyphenated.advent2023.day5

import com.jacobhyphenated.advent2023.Day

/**
 * Day 5: If You Give A Seed A Fertilizer
 *
 * The puzzle input is an almanac that describes the relationship between different gardening components
 * Seeds map to soil map to fertilizer etc until the end destination is location.
 *
 * Each source to destination conversion has a list of maps that look like this:
 * 50 98 2
 * 98 is the source, 2 is the range, and 50 is the destination.
 * From the above 98 maps to 50 and 99 maps to 51 - the range of 2 extends the source destination map for 2 numbers
 */
class Day5: Day<Pair<List<Long>, List<SourceDestinationMap>>> {
  override fun getInput(): Pair<List<Long>, List<SourceDestinationMap>> {
    return parseInput(readInputFile("5"))
  }

  /**
   * Part 1: Given the starting seed numbers, traverse the list of destination maps to convert the seed to location.
   * Return the minimum location value
   *
   * because the "almanac" maps are in order, we can use the result of the previous mapping as the input to the next
   */
  override fun part1(input: Pair<List<Long>, List<SourceDestinationMap>>): Long {
    val (seeds, almanac) = input
    return seeds.minOf { seedNumber ->
      almanac.fold(seedNumber) { source, destinationMap -> destinationMap[source]}
    }
  }

  /**
   * Part 2: The seed input actually represents pairs of seed and range
   * so "79 14 55 13" is actually 79-92, 55-67
   *
   * The puzzle input ranges are too large for a brute force approach. Instead, we need to track the
   * source ranges as groups, in this case, as Pairs of (sourceStart, range).
   */
  override fun part2(input: Pair<List<Long>, List<SourceDestinationMap>>): Long {
    val (seedsRangeList, almanac) = input
    val sourcePairs = seedsRangeList.windowed(2, 2).map { (seed, range) -> Pair(seed, range) }
    return almanac.fold(sourcePairs) { pairs, destinationMap ->
      pairs.flatMap { (source, range) -> destinationMap.getRanges(source, range) }
    }.minOf { (location, _) -> location }
  }

  fun parseInput(input: String): Pair<List<Long>, List<SourceDestinationMap>> {
    val steps = input.split("\n\n")
    val seeds = steps[0].split(":")[1].trim().split(" ").map { it.toLong() }
    val almanacMappings = steps.subList(1, steps.size) .map { step ->
      val stepLines = step.lines()
      val (source, destination) = stepLines[0].split(" ")[0].split("-to-")
      val rangeLines = stepLines.subList(1, stepLines.size)
        .map { line -> line.split(" ").map { it.toLong() } }
      SourceDestinationMap(source, destination, rangeLines)
    }
    return Pair(seeds, almanacMappings)
  }
}

/**
 * Data helper class that does the source destination conversions.
 * The source and destination names are not actually used because the puzzle is in order
 *
 * each range map represents a throuple of (destination, source, range) from the puzzle input
 * a source-to-destination map can have any number or range maps
 */
data class SourceDestinationMap(val sourceName: String,
                                val destinationName: String,
                                private val rangeMaps: List<List<Long>>) {

  /**
   * The operator fun overrides get such that we can do y = destinationMap\[source]
   *
   * Take a single source value and return the resulting destination.
   * If the source value is not included in a range map, return the source value
   */
  operator fun get(source: Long): Long {
    return rangeMaps.firstOrNull { (_, sourceStart, range) ->
      source >= sourceStart && source <sourceStart + range
    }?.let { (destinationStart, sourceStart) ->  source - sourceStart + destinationStart }
      ?: source
  }

  /**
   * To map entire ranges, we consider the range of the [rangeMaps] and the range of the ([s1], [sourceRange]) pair.
   * These two ranges can intersect in 4 possible ways (or not intersect at all).
   * In cases where there are ports of the source range that do not intersect with the map,
   * split that off and create a new Pair() to represent that unmapped range (and try to map it if possible).
   *
   * To visualize how the ranges can intersect
   * ```
   *     |***********\........|  \                    e1 < e2 && e1 > s2     s1 < s2
   *     |***********\...........\**|                 e2 < e1 && e2 > s1     s1 < s2
   *     \           |...........\**|                 e2 < e1 && e2 < s1
   *     \           |...........|  \                 e1 < e2 && e1 > s2
   *     s2          s1         e1  e2
   * ```
   *
   *  The source range (s1, e1) is between the | characters
   *  The map range (s2, e2) is between the \ characters
   *  "." - represent spaces with overlap
   *  "*" - represent additional unmapped range from the source range
   *
   * @param s1 the start of the source range we are mapping
   * @param sourceRange the range that describes the length from [s1]
   *
   * @return a list of Pair ranges as this single range may be broken up into several smaller ranges
   */
  fun getRanges(s1: Long, sourceRange: Long): List<Pair<Long,Long>> {
    val e1 = s1 + sourceRange - 1
    for ((destinationStart, s2, range) in rangeMaps) {
      val e2 = s2 + range - 1
      if ((e1 <= e2) && (e1 >= s2)) {
        return if (s1 >= s2) {
          val offset = s1 - s2
          listOf(Pair(destinationStart + offset, sourceRange))
        } else {
          val offset = s2 - s1
          getRanges(s1, offset) + Pair(destinationStart, sourceRange - offset)
        }
      } else if ((e2 <= e1) && (e2 >= s1)) {
        val endExcess = getRanges(e2 + 1 , e1 - e2 )
        return if (s1 < s2) {
          val beginningExcess = getRanges(s1, s2 - s1)
          endExcess + beginningExcess + Pair(destinationStart, range)
        } else {
          val offset = s1 - s2
          endExcess + Pair(destinationStart + offset, e2 - s1)
        }
      }
    }
    return listOf(Pair(s1, sourceRange))
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day5().run()
}
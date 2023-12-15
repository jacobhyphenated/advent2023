package com.jacobhyphenated.advent2023.day12

import com.jacobhyphenated.advent2023.Day
import com.jacobhyphenated.advent2023.product

class Day12: Day<List<Pair<List<Char>, List<Int>>>> {
  override fun getInput(): List<Pair<List<Char>, List<Int>>> {
    return parseInput(readInputFile("12"))
  }

  override fun part1(input: List<Pair<List<Char>, List<Int>>>): Int {
    return input.sumOf { (arrangement, groupSize) -> countValidArrangements(arrangement, groupSize) }
  }

  override fun part2(input: List<Pair<List<Char>, List<Int>>>): Long {
    // expand input
    return input.sumOf { (arrangement, groupSize) ->
      val expandedArrangement = List(5) { arrangement.joinToString("") }.joinToString("?")
      val expandedSizes = List(5) { groupSize }.flatten()

      // this solves the test input relatively fast, but it's not good enough for the puzzle input
      countValidArrangements(expandedArrangement.toCharArray().toList(), expandedSizes).toLong().also { println(it) }

    }

    // go through characters in arrangement one at a time
    // track contiguous possible
    // track contiguous working
    // firstPivot = first non # char where continuousPossible >= groupSize[0]
    // lastPivot
    //    max is arrangement.size - groupSize[1:].sum() - 1
    //    first . character where contiguousWorking > 0

    // if firstPivot == null || lastPivot == null, return 0
    // loop firstPivot .. lastPivot
    //    if arrangment[it] == '#' continue
    //    calc(arrangement[0:it], groupSize[0]) * recurse(arrangement[it+1:], groupSize[1:])

    // memo recurse function inputs
    // can this result in duplicate combos around pivot points?

  }

  private fun countValidArrangements(arrangement: List<Char>, groupSizes: List<Int>): Int {
    if (isArrangementInvalid(arrangement, groupSizes)) {
      return 0
    }
    val unknownIndex = arrangement.indexOfFirst { it == '?' }
    if (unknownIndex == -1) {
      return 1
    }
    val a1 = arrangement.mapIndexed { i, c -> if (i == unknownIndex) { '#' } else { c } }
    val a2 = arrangement.mapIndexed { i, c -> if (i == unknownIndex) { '.' } else { c } }
    return countValidArrangements(a1, groupSizes) + countValidArrangements(a2, groupSizes)
  }

  fun isArrangementInvalid(arrangement: List<Char>, groupSizes: List<Int>): Boolean {
    val buffer = mutableListOf<Char>()
    var groupIndex = 0
    for (i in arrangement.indices) {
      if (groupIndex > groupSizes.size) {
        return true
      }
      if (groupIndex == groupSizes.size && arrangement.subList(i, arrangement.size).any { it == '#' }) {
        return true
      }
      if (arrangement[i] == '?') {
        return false
      }
      else if (arrangement[i] == '#') {
        buffer.add(arrangement[i])
      }
      else if (arrangement[i] == '.') {
        if (buffer.isNotEmpty()) {
          if (groupIndex >= groupSizes.size || buffer.size != groupSizes[groupIndex]) {
            return true
          }
          else {
            groupIndex++
            buffer.clear()
          }
        }
      }
    }
    if (buffer.isNotEmpty()) {
      if (groupIndex >= groupSizes.size || buffer.size != groupSizes[groupIndex]) {
        return true
      }
      else {
        groupIndex++
        buffer.clear()
      }
    }
    if (groupIndex != groupSizes.size) {
      return true
    }
    return false
  }

  fun allCombinations(prefix: List<Int>, rest: List<Int>, num: Int): List<List<Int>> {
    if (prefix.size == num) {
      return listOf(prefix)
    }
    val remainder = num - prefix.size
    return (0 .. rest.size - remainder).map {
      val combo = prefix + rest[it]
      allCombinations(combo, rest.subList(it+1), num)
    }.flatten()
  }
  private fun isReadyForCombos(groups: List<String>, sizes: List<Int>): Boolean {
    return groups.size == sizes.size && groups.indices.all { groups[it].length >= sizes[it] }
  }

  fun parseInput(input: String): List<Pair<List<Char>, List<Int>>> {
    return input.lines().map {line ->
      val (arrangementString, groupString) = line.split(" ").map { it.trim() }
      val groups = groupString.split(",").map { it.toInt() }
      val arrangement = arrangementString.toCharArray().toList()
      Pair(arrangement, groups)
    }
  }
}

fun<T> List<T>.subList(fromIndex: Int): List<T> {
  if (fromIndex >= size) {
    return listOf()
  }
  return subList(fromIndex, this.size)
}
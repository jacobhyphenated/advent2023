package com.jacobhyphenated.advent2023.day12

import com.jacobhyphenated.advent2023.Day

class Day12: Day<List<Pair<List<Char>, List<Int>>>> {
  override fun getInput(): List<Pair<List<Char>, List<Int>>> {
    return parseInput(readInputFile("12"))
  }

  override fun part1(input: List<Pair<List<Char>, List<Int>>>): Int {
    return input.sumOf { (arrangement, groupSize) -> countValidArrangements(arrangement, groupSize) }
  }

  override fun part2(input: List<Pair<List<Char>, List<Int>>>): Any {
    TODO("Not yet implemented")
    // expand input

    // split on \.+

    // if the number of split groups == groupSize.size
    //    find combos for splitGroup[i], groupSize[i]
    //    multiply all together

    // if number of splitGroups < groupsSize.size
    //     find all combinations where replacing a '?' with '.' creates the correct group size
    //     each group[i] must have size >= groupSizes[i]
    //     repeat steps above for each valid group size and sum together

    // if number of splitGroups > groupSize.size
    //     find all groups with only ?
    //     try every option where we remove one of the ? groups, sum the results


    // fun isReadyForCombos(groups: List<String>, sizes: List<Int>)
    //    groups.size == sizes.size &&
    //    indices.all { groups[i].length >= sizes[i]
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

  fun parseInput(input: String): List<Pair<List<Char>, List<Int>>> {
    return input.lines().map {line ->
      val (arrangementString, groupString) = line.split(" ").map { it.trim() }
      val groups = groupString.split(",").map { it.toInt() }
      val arrangement = arrangementString.toCharArray().toList()
      Pair(arrangement, groups)
    }
  }
}
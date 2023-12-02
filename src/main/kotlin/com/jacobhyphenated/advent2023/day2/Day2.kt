package com.jacobhyphenated.advent2023.day2

import com.jacobhyphenated.advent2023.Day

/**
 * Day 2: Cube Conundrum
 *
 * Each line of input represents a game with a bag with an unknown number of red, green, and blue cubes
 * Several times, reach into the bag for a sample of colors, then replace the cubes in the bag.
 */
class Day2: Day<List<String>> {
  override fun getInput(): List<String> {
    return readInputFile("2").lines()
  }

  /**
   * Part 1: Determine which games would have been possible
   * if the bag had been loaded with only 12 red cubes, 13 green cubes, and 14 blue cubes.
   * What is the sum of the IDs of those games?
   */
  override fun part1(input: List<String>): Int {
    val colorCheck = mapOf(Color.RED to 12, Color.GREEN to 13, Color.BLUE to 14)
    return input.mapIndexed { index, line ->
      val passCheck = maxColorsFromLine(line).all { (color, count) ->
        colorCheck.getValue(color) >= count
      }
      // use index + 1 for the game ID
      if (passCheck) { index + 1 } else { 0 }
    }.sum()
  }

  /**
   * Part 2: What is the fewest number of possible cubes of each color for each game?
   * Multiply these numbers to get the score for each game, then add up the scores.
   */
  override fun part2(input: List<String>): Int {
    return input.sumOf { line ->
      maxColorsFromLine(line).values.reduce { acc, count ->  acc * count }
    }
  }

  /**
   * Take the input string and parse it into a map
   * where the key is the color and the value is the minimum possible amount of that color in the bag
   */
  private fun maxColorsFromLine(line: String): Map<Color, Int> {
    val colorSamples = line.split(":")[1].trim().split(";")
    val maxColorMap = mutableMapOf(Color.RED to 0, Color.GREEN to 0, Color.BLUE to 0)
    colorSamples.forEach { sample ->
      sample.split(",")
        .map { it.trim().split(" ") }
        .forEach { (number, colorString) ->
          val color = Color.fromString(colorString)
          val colorCount = number.toInt()
          if (maxColorMap.getValue(color) < colorCount) {
            maxColorMap[color] = colorCount
          }
        }
    }
    return maxColorMap
  }

  override fun warmup(input: List<String>): Any {
    return maxColorsFromLine(input[0])
  }

}

enum class Color {
  RED,
  BLUE,
  GREEN;

  companion object {
    fun fromString(input: String): Color {
      return when(input) {
        "blue" -> Color.BLUE
        "red" -> Color.RED
        "green" -> Color.GREEN
        else -> throw IllegalArgumentException("Invalid color: $input")
      }
    }
  }
}
package com.jacobhyphenated.advent2023.day1

import com.jacobhyphenated.advent2023.Day

/**
 * Day 1: Trebuchet?!
 *
 * The puzzle input is a list of strings. The calibration score for each line is the first digit in the line
 * combined with the last digit in the line. Example: a1b2c3d4e5f is 15
 */
class Day1: Day<List<String>> {

  override fun getInput(): List<String> {
    return readInputFile("1").lines()
  }

  /**
   * Sum all the calibration scores
   *
   * Solve by filtering out all non-digit characters, then take the first and last digit
   */
  override fun part1(input: List<String>): Int {
    val digits = ('0'..'9').toSet()
    return input.map { it.toCharArray().filter { c -> c in digits } }
      .sumOf { "${it.first()}${it.last()}".toInt()  }
  }

  /**
   * Numbers written out long form count. So two1nine is 29
   */
  override fun part2(input: List<String>): Int {
    val digits = ('1'..'9').toList()
    val digitStrings = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val digitMap = digitStrings.zip(digits).toMap()

    // The problem is eightwothree is 83. So naively replacing "two" with "2" doesn't work.
    // We can use a regex to pull the first and last matches of the digit or digit string
    val matchFirstDigit = "\\d|one|two|three|four|five|six|seven|eight|nine".toRegex()

    // requires the .* at the beginning and end. Also requires the capture group. I don't know why
    val matchLastDigit = ".*(\\d|one|two|three|four|five|six|seven|eight|nine).*".toRegex()
    return input.sumOf { line ->
      val match1 = matchFirstDigit.find(line)!!
      val firstDigit = digitMap[match1.value] ?: match1.value
      val match2 = matchLastDigit.find(line)!!
      val lastDigit = digitMap[match2.groupValues.last()] ?: match2.groupValues.last()
      "$firstDigit$lastDigit".toInt()
    }
  }

  /**
   * Original solution without regex. Traverses the line from beginning and from end in reverse to find the last digit
   */
//  override fun part2(input: List<String>): Int {
//    val digits = ('0'..'9').toList()
//    val digitStrings = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
//    val digitMap = digitStrings.zip(digits).toMap()
//
//    // The problem is eightwothree is 83. So naively replacing "two" with "2" doesn't work.
//    // we need to traverse each string from the beginning, then from the end
//
//    // This helper lambda checks the current index for either a digit or string number or null if neither
//    val checkForDigit = { line: String, position: Int ->
//      var result: Char? = null
//      if (line[position] in digits) {
//         result = line[position]
//      }
//      for (digitString in digitStrings) {
//        if (line.substring(position).startsWith(digitString)) {
//          result = digitMap.getValue(digitString)
//          break
//        }
//      }
//      result
//    }
//
//    return input.sumOf { line ->
//      // traverse from the start to get the first number
//      var startIndex = 0
//      var startDigit: Char? = null
//      while (startIndex < line.length) {
//        startDigit = checkForDigit(line, startIndex)
//        if (startDigit != null) {
//          break
//        }
//        startIndex++
//      }
//
//      // traverse backwards from the end to get the last number
//      var endIndex = line.length - 1
//      var endDigit: Char? = null
//      while (endIndex >= 0) {
//        endDigit = checkForDigit(line, endIndex)
//        if (endDigit != null) {
//          break
//        }
//        endIndex--
//      }
//      println()
//      println(line)
//      println("$startDigit$endDigit")
//      "$startDigit$endDigit".toInt()
//    }
//  }

  override fun warmup(input: List<String>) {
    part1(input)
    part2(input)
  }
}
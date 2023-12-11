package com.jacobhyphenated.advent2023

import com.jacobhyphenated.advent2023.day1.Day1
import com.jacobhyphenated.advent2023.day10.Day10
import com.jacobhyphenated.advent2023.day2.Day2
import com.jacobhyphenated.advent2023.day3.Day3
import com.jacobhyphenated.advent2023.day4.Day4
import com.jacobhyphenated.advent2023.day5.Day5
import com.jacobhyphenated.advent2023.day6.Day6
import com.jacobhyphenated.advent2023.day7.Day7
import com.jacobhyphenated.advent2023.day8.Day8
import com.jacobhyphenated.advent2023.day9.Day9

fun main(args: Array<String>) {
  val days = mapOf(
    "1" to Day1(),
    "2" to Day2(),
    "3" to Day3(),
    "4" to Day4(),
    "5" to Day5(),
    "6" to Day6(),
    "7" to Day7(),
    "8" to Day8(),
    "9" to Day9(),
    "10" to Day10()
  )

  args.forEach { day ->
    println()
    println("day $day")
    days[day]?.run() ?: println("No implementation found")
  }
}
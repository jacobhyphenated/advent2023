package com.jacobhyphenated.advent2023

import com.jacobhyphenated.advent2023.day1.Day1
import com.jacobhyphenated.advent2023.day10.Day10
import com.jacobhyphenated.advent2023.day11.Day11
import com.jacobhyphenated.advent2023.day12.Day12
import com.jacobhyphenated.advent2023.day13.Day13
import com.jacobhyphenated.advent2023.day14.Day14
import com.jacobhyphenated.advent2023.day15.Day15
import com.jacobhyphenated.advent2023.day16.Day16
import com.jacobhyphenated.advent2023.day17.Day17
import com.jacobhyphenated.advent2023.day18.Day18
import com.jacobhyphenated.advent2023.day19.Day19
import com.jacobhyphenated.advent2023.day2.Day2
import com.jacobhyphenated.advent2023.day20.Day20
import com.jacobhyphenated.advent2023.day21.Day21
import com.jacobhyphenated.advent2023.day22.Day22
import com.jacobhyphenated.advent2023.day23.Day23
import com.jacobhyphenated.advent2023.day24.Day24
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
    "10" to Day10(),
    "11" to Day11(),
    "12" to Day12(),
    "13" to Day13(),
    "14" to Day14(),
    "15" to Day15(),
    "16" to Day16(),
    "17" to Day17(),
    "18" to Day18(),
    "19" to Day19(),
    "20" to Day20(),
    "21" to Day21(),
    "22" to Day22(),
    "23" to Day23(),
    "24" to Day24()
  )

  args.forEach { day ->
    println()
    println("day $day")
    days[day]?.run() ?: println("No implementation found")
  }
}
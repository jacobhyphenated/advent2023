package com.jacobhyphenated.advent2023

import com.jacobhyphenated.advent2023.day1.Day1
import java.util.*

fun main(args: Array<String>) {
  val days = mapOf(
    "1" to Day1()
  )

  args.forEach { day ->
    println()
    println("day $day")
    days[day.lowercase(Locale.getDefault())]?.run() ?: println("No implementation found")
  }
}
package com.jacobhyphenated.advent2023

import kotlin.math.pow

fun Collection<Int>.product(): Int {
  return reduce { acc, next -> acc * next }
}

fun Collection<Long>.product(): Long {
  return reduce { acc, next -> acc * next }
}

infix fun Int.pow(exponent: Int): Int = toDouble().pow(exponent).toInt()

infix fun Long.pow(exponent: Int): Long = toDouble().pow(exponent).toLong()

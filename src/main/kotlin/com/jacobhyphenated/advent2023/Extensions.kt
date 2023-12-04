package com.jacobhyphenated.advent2023

import kotlin.math.pow

fun Collection<Int>.product(): Int {
  return reduce { acc, next -> acc * next }
}

@Suppress("DANGEROUS_CHARACTERS")
infix fun Int.`**`(exponent: Int): Int = toDouble().pow(exponent).toInt()

package com.jacobhyphenated.advent2023

fun Collection<Int>.product(): Int {
  return reduce { acc, next -> acc * next }
}
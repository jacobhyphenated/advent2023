package com.jacobhyphenated.advent2023.day8

fun gcd(num1: Long, num2: Long): Long {
  if (num2 == 0L) {
    return num1
  }
  return gcd(num2, num1 % num2)
}

fun lcm(numbers: List<Long>): Long {
  var lcm = numbers[0]
  for (i in 1 until numbers.size) {
    val num1 = lcm
    val num2 = numbers[i]
    val gcdValue = gcd(num1, num2)
    lcm = (lcm * num2) / gcdValue
  }
  return lcm
}
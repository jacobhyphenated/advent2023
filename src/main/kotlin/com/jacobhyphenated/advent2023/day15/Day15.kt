package com.jacobhyphenated.advent2023.day15

import com.jacobhyphenated.advent2023.Day

/**
 * Day 15: Lens Library
 *
 * The puzzle input is a set of instruction for focusing lenses.
 * Use a custom ascii hash algorithm as part of the setup process
 */
class Day15: Day<String> {
  override fun getInput(): String {
    return readInputFile("15")
  }

  /**
   * Part 1: Run the hash algorithm on each comma separated instruction and add the results
   */
  override fun part1(input: String): Int {
    return input.split(",").sumOf { asciiHash(it) }
  }

  /**
   * Part 2: Each instruction is has either a '-' or an '=' (ex: rn=1 cm-)
   *
   * There are 256 boxes (0 - 255). Use the hash algorithm on the lens component of the instruction.
   * The '-' operation removes that lens from the box.
   * The '=' instruction adds that lens with the supplied focus value OR modifies the focus value if
   * the lens is already in the box. If modifying, do not change the order of the lenses
   *
   * Calculate the score for each lens as:
   * box number (1 indexed) * position in box (1 indexed) * focus value
   */
  override fun part2(input: String): Int {
    val boxes = mutableMapOf<Int, MutableList<Pair<String,Int>>>()
    (0 until 256).forEach { boxes[it] = mutableListOf() }

    input.split(",").forEach {instruction ->
      if (instruction.endsWith("-")){
        val lens = instruction.split("-")[0]
        val hash = asciiHash(lens)
        val box = boxes.getValue(hash)
        val index = box.indexOfFirst { (l, _) -> l == lens }
        if (index != -1) {
          box.removeAt(index)
        }
      } else {
        val (lens, focus) = instruction.split("=")
        val hash = asciiHash(lens)
        val box = boxes.getValue(hash)
        val index = box.indexOfFirst { (l, _) -> l == lens }
        if (index != -1) {
          box[index] = Pair(lens, focus.toInt())
        } else{
          box.add(Pair(lens, focus.toInt()))
        }
      }
    }

    return (0 until 256).sumOf { index ->
      boxes.getValue(index)
        .mapIndexed{ i, (_, focus) -> (index+1) * (i+1) * focus }
        .sum()
    }

  }

  /**
   * Hash function:
   * start at 0
   * Add the ascii code of the next character
   * Multiply by 17 then mod 256
   * Repeat for each character
   */
  private fun asciiHash(input: String): Int {
    return input.toCharArray().fold(0){ value, c ->
      var output = value + c.code
      output *= 17
      output % 256
    }
  }

  override fun warmup(input: String) {
    part2(input)
  }
}


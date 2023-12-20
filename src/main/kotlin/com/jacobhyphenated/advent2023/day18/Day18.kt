package com.jacobhyphenated.advent2023.day18

import com.jacobhyphenated.advent2023.Day
import kotlin.math.absoluteValue

/**
 * Day 18: Lavaduct Lagoon
 *
 * Workers need to dig a lagoon to hold excess lava. The puzzle input is instructions on how to dig
 * Each instruction has a direction, a value, and a color
 * By following the directions, the workers will dig out a border or the lagoon and return to the starting location
 */
class Day18: Day<List<Instruction>> {
  override fun getInput(): List<Instruction> {
    return parseInput(readInputFile("18"))
  }

  /**
   * Part 1: By following the instructions, we can see the border of the lagoon.
   * Calculate the number of interior spaces within this border and return the total area of the lagoon.
   *
   * Algorithm. This is a modified ray tracing algorithm (modified because the general algorithm doesn't
   * handle co-linear edges). Example, is the '?' an interior space?
   *   ...####?..
   *
   * Solve this by looking at how the edge is formed:
   *                        ......#
   * ...####?  exterior     ...####?  Interior
   * ...#..#                ...#
   */
  override fun part1(input: List<Instruction>): Int {
    var current = Pair(0,0)
    // build a set of all points along the border
    val digBorder = mutableSetOf(current)
    for (instruction in input) {
      val (row, col) = current
      val line = when (instruction.direction) {
        Direction.DOWN -> (row + 1 .. row + instruction.value).map { Pair(it, col) }
        Direction.UP -> (row - 1 downTo  row - instruction.value).map { Pair(it, col) }
        Direction.RIGHT -> (col + 1 .. col + instruction.value).map { Pair(row, it) }
        Direction.LEFT -> (col - 1 downTo col - instruction.value).map { Pair(row, it) }
      }
      digBorder.addAll(line)
      current = line.last()
    }
    // find the bounding box of the dig site
    val minX = digBorder.minOf { (_, c) -> c }
    val maxX = digBorder.maxOf { (_, c) -> c }
    val minY = digBorder.minOf { (r, _) -> r }
    val maxY = digBorder.maxOf { (r, _) -> r }

    // A ray starts at the far left. When it encounters a border point,
    // subsequent open spaces are interior until the next border point is reached.
    // repeat for the length of each row
    var interiorSpaces = 0
    for (r in minY .. maxY) {
      var isInterior = false
      var borderStart: Int? = null
      for (c in minX .. maxX) {
        val isBorder = Pair(r,c) in digBorder
        if (isBorder) {
          if (borderStart == null) {
            borderStart = c
          }
        }
        else {
          if (borderStart != null && borderStart == c - 1) {
            isInterior = !isInterior
          }
          // the tricky part here is how to handle a border "line"
          else if (borderStart != null && borderStart != c - 1) {
            val startUp = Pair(r - 1, borderStart) in digBorder
            val endUp = Pair(r - 1, c - 1) in digBorder
            // If the border "line" comes from different directions, flip the interior flag
            // otherwise treat the line as an empty space, rather than a border
            if (startUp != endUp) {
              isInterior = !isInterior
            }
          }
          borderStart = null
          if (isInterior) {
            interiorSpaces++
          }
        }
      }
    }
    return interiorSpaces + digBorder.size
  }

  /**
   * Part 2: The color is actually the instructions
   * The first 5 digits of the hex value or the dig length.
   * The last digit represents direction (0: right, 1: Down, etc)
   *
   * The bounding box is in the hundreds of trillions. There is no way to make ray tracing from part 1 work.
   * Even calculating each point would be impossible.
   *
   * Instead, we solve this with weird math shit. I spent most of this part on wikipedia
   * https://en.wikipedia.org/wiki/Pick%27s_theorem
   * https://en.wikipedia.org/wiki/Shoelace_formula
   */
  override fun part2(input: List<Instruction>): Long {
    val instructions = input.map { (_, _, color) ->
      val value = color.substring(1, color.length - 1).toInt(16)
      val direction = when (color.last()) {
        '0' -> Direction.RIGHT
        '1' -> Direction.DOWN
        '2' -> Direction.LEFT
        '3' -> Direction.UP
        else -> throw IllegalArgumentException("Invalid direction")
      }
      Instruction(direction, value, color)
    }

    // build a list of vertices.
    // Note: our polygon is closed, does not intersect itself, and has no interior holes
    var current = Pair(0,0)
    val vertices = mutableListOf(current)
    instructions.forEach { (direction, value) ->
      val (row, col) = current
      current = when(direction) {
        Direction.DOWN -> Pair(row + value, col)
        Direction.UP -> Pair(row - value, col)
        Direction.RIGHT -> Pair(row, col + value)
        Direction.LEFT -> Pair(row, col - value)
      }
      vertices.add(current)
    }

    // This is the shoelace formula. I don't know why it works.
    var area: Long = 0
    for (i in 0 until vertices.size - 1) {
      val (x1, y1) = vertices[i]
      val (x2, y2) = vertices[i+1]
      area += x1.toLong() * y2.toLong() - y1.toLong() * x2.toLong()
    }
    area = area.absoluteValue / 2

    // but the area is not actually the full area. This is why we need Pick's theorem
    // A = i + b/2 - 1
    // where i is the interior spaces (from the shoelace formula) and b is the perimeter length
    val perimeter = instructions.sumOf { it.value }
    return area + perimeter / 2 + 1
  }

  fun parseInput(input: String): List<Instruction> {
    return input.lines().map { line ->
      val (dir, num, colorString) = line.split(" ")
      val color = colorString.removePrefix("(").removeSuffix(")")
      Instruction(Direction.fromString(dir), num.toInt(), color)
    }
  }
}

data class Instruction(val direction: Direction, val value: Int, val color: String)

enum class Direction {
  RIGHT, LEFT, UP, DOWN;

  companion object {
    fun fromString(input: String): Direction {
      return when (input) {
        "R" -> RIGHT
        "L" -> LEFT
        "U" -> UP
        "D" -> DOWN
        else -> throw IllegalArgumentException("Invalid direction $input")
      }
    }
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day18().run()
}
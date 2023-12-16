package com.jacobhyphenated.advent2023.day16

import com.jacobhyphenated.advent2023.Day

/**
 * Day 16: The Floor Will Be Lava
 *
 * A beam of light gets reflected and split throughout the puzzle input.
 * The beam travels one space at a time in a specific direction.
 *  '.' does nothing, the beam continues onward
 *  '-' if the beam is traveling north/south, split the beam into two heading west+east
 *  '|' if the beam is traveling east/west, split the beam into two heading north+south
 *  '/' reflect the beam 90 degrees
 *  '\' reflect the beam the opposite 90 degrees
 */
class Day16: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("16").lines().map { it.toCharArray().toList() }
  }

  /**
   * Part 1: The beam starts in the top left corner heading east.
   * If the beam pases through a space, then that space is energized.
   * How many spaces does this beam energize?
   */
  override fun part1(input: List<List<Char>>): Int {
    // start just off the edge of the grid, so we properly interact with the first space
    val startBeam = Beam(Pair(0,-1), Direction.EAST)
    return energizedSpacesFromStartingEdge(startBeam, input)
  }

  override fun part2(input: List<List<Char>>): Any {
    val startTop = input[0].indices.map { col -> Beam(Pair(-1, col), Direction.SOUTH) }
    val startBottom = input[0].indices.map { col -> Beam(Pair(input.size, col), Direction.NORTH) }
    val startLeft = input.indices.map { row -> Beam(Pair(row, -1), Direction.EAST) }
    val startRight = input.indices.map { row -> Beam(Pair(row, input[0].size), Direction.WEST) }

    return (startTop + startBottom + startLeft + startRight).maxOf { energizedSpacesFromStartingEdge(it, input) }
  }

  /**
   * Count the number of spaces the beam passes through
   */
  private fun energizedSpacesFromStartingEdge(startBeam: Beam, input: List<List<Char>>): Int {
    // keep track of both position and direction. If a beam repeats this, we can ignore it
    val visited = mutableSetOf<Beam>()
    var beams = listOf(startBeam)
    visited.add(startBeam)

    // go until we have no more beams to follow
    while (beams.isNotEmpty()) {
      beams = beams.flatMap { moveBeam(it, input) }
        .filter { it !in visited }
      visited.addAll(beams)
    }

    // reduce the visited set to just locations, and count them
    // note: subtract 1 because the first location is always off the edge of the grid
    return visited.map { it.location }.toSet().size - 1
  }

  /**
   * Determine the next location and direction of the given beam.
   * @return a list as the splitters can create multiple beams from a single beam
   */
  private fun moveBeam(beam: Beam, grid: List<List<Char>>): List<Beam> {
    val (currentRow, currentCol) = beam.location
    val newLocation = when(beam.direction) {
      Direction.EAST -> Pair(currentRow, currentCol + 1)
      Direction.WEST -> Pair(currentRow, currentCol - 1)
      Direction.NORTH -> Pair(currentRow - 1, currentCol)
      Direction.SOUTH -> Pair(currentRow + 1, currentCol)
    }
    val (row, col) = newLocation
    if (row < 0 || row >= grid.size || col < 0 || col >= grid[0].size) {
      return emptyList()
    }
    return when (grid[row][col]) {
      '.' -> listOf(Beam(newLocation, beam.direction))
      '-' -> when (beam.direction) {
        Direction.WEST, Direction.EAST -> listOf(Beam(newLocation, beam.direction))
        Direction.NORTH, Direction.SOUTH -> listOf(Beam(newLocation, Direction.EAST), Beam(newLocation, Direction.WEST))
      }
      '|' -> when (beam.direction) {
        Direction.NORTH, Direction.SOUTH -> listOf(Beam(newLocation, beam.direction))
        Direction.EAST, Direction.WEST -> listOf(Beam(newLocation, Direction.NORTH), Beam(newLocation, Direction.SOUTH))
      }
      '/' -> when (beam.direction) {
        Direction.NORTH -> listOf(Beam(newLocation, Direction.EAST))
        Direction.SOUTH -> listOf(Beam(newLocation, Direction.WEST))
        Direction.EAST -> listOf(Beam(newLocation, Direction.NORTH))
        Direction.WEST -> listOf(Beam(newLocation, Direction.SOUTH))
      }
      '\\' -> when (beam.direction) {
        Direction.NORTH -> listOf(Beam(newLocation, Direction.WEST))
        Direction.SOUTH -> listOf(Beam(newLocation, Direction.EAST))
        Direction.EAST -> listOf(Beam(newLocation, Direction.SOUTH))
        Direction.WEST -> listOf(Beam(newLocation, Direction.NORTH))
      }
      else -> throw IllegalArgumentException("Invalid character ${grid[row][col]}")
    }
  }

  override fun warmup(input: List<List<Char>>) {
    part1(input)
  }

}

data class Beam(val location: Pair<Int,Int>, val direction: Direction)

enum class Direction {
  NORTH, EAST, SOUTH, WEST
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day16().run()
}
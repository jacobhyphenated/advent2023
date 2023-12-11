package com.jacobhyphenated.advent2023.day10

import com.jacobhyphenated.advent2023.Day
import java.util.*

/**
 * Day 10: Pipe Maze
 *
 * The puzzle input describes a maze of pipes. Each character represents the shape of a pipe
 * with the '.' character meaning an empty spaces.
 *
 * Start at the S symbol, which connects to adjacent connecting pipes
 * Following the pipes from the S symbol form a loop. There are segments of pipe
 * throughout the grid that are not part of the pipe loop
 */
class Day10: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("10").lines().map { it.toCharArray().toList() }
  }

  /**
   * Part 1: What section of pipe loop is the furthest distance from the S position?
   *
   * Solve with a straightforward implementation of Dijkstra's algorithm to find the path cost
   * from S to all other positions in the loop
   */
  override fun part1(input: List<List<Char>>): Int {
    val distances = calculateDistances(input)
    return distances.values.max()
  }

  /**
   * Part 2: Count how many spaces are enclosed within the loop
   *
   * Note: You can move between sections of pipe that do not connect
   * example:
   * ..........
   * .S------7.
   * .|F----7|.
   * .||OOOO||.
   * .||OOOO||.
   * .|L-7F-J|.
   * .|II||II|.
   * .L--JL--J.
   * ..........
   *
   * The O spaces are open to the outside because you can move between the 7F, ||, and JL pipe segments
   * The above example contains only 4 interior spaces, marked as I
   *
   * Note 2: junk pipe segments not part of the pipe loop can count as interior spaces
   */
  override fun part2(input: List<List<Char>>): Int {
    // use the distance calculation from part 1 to build the full pipe loop
    val distances = calculateDistances(input)

    // account for moving between pipes by adding empty spaces around the previous grid spaces
    val expandedGrid = input.flatMap { row -> listOf(
        row.flatMap { c -> listOf(c, '.') },
        List(row.size * 2) { '.' }
    )}.toList()
    val pipeLoop = distances.keys.map { (r,c) -> Pair(r*2, c*2) }.toSet()

    // Connect the pipe loop spaces through the new empty spaces
    val expandedPipeLoop = pipeLoop.toMutableSet()
    pipeLoop.forEach { (r, c) ->
      directionsFromPosition(r, c, expandedGrid).map { direction ->
        val next = direction.nextPosition(r,c)
        val skipNext = direction.nextPosition(next.first, next.second)
        if (skipNext in pipeLoop
            && direction.opposite() in directionsFromPosition(skipNext.first, skipNext.second, expandedGrid)) {
          expandedPipeLoop.add(next)
        }
      }
    }

    // Now test each grid position (that is not part of the pipe loop) to see if it's interior or not
    val memoOpen = mutableSetOf<Pair<Int,Int>>()
    return input.indices.flatMap { row -> input[row].indices.map { col -> Pair(row * 2, col * 2) } }
      .filter { it !in expandedPipeLoop }
      .count { (row, col) -> !canAccessOutsideOfGrid(row, col, expandedGrid, expandedPipeLoop, memoOpen) }
  }

  // Use Dijkstra's algorithm to calculate the distance from the start to each other pipe node
  private fun calculateDistances(input: List<List<Char>>): Map<Pair<Int,Int>, Int> {
    val start = findStart(input)
    val distances: MutableMap<Pair<Int,Int>, Int> = mutableMapOf(start to 0)
    val (startRow, startCol) = start

    // we don't know what the start pipe looks like, so find each adjacent
    // location where the pipe connects to the start location
    val connectToStart = Direction.values().mapNotNull { direction ->
      val next = direction.nextPosition(startRow, startCol)
      if (next.first !in input.indices || next.second !in input[0].indices) {
        null
      } else if (direction.opposite() in directionsFromPosition(next.first, next.second, input)) {
        next
      } else {
        null
      }
    }

    // because the S is a weird pipe position, add the adjacent pipes to the queue rather than the start pipe
    val queue = PriorityQueue<PathCost> { a, b -> a.cost - b.cost }
    connectToStart.forEach {
      queue.add(PathCost(it, 1))
      distances[it] = 1
    }
    var current: PathCost

    do {
      current = queue.remove()
      // If we already found a less expensive way to reach this position
      if (current.cost > (distances[current.location] ?: Int.MAX_VALUE)) {
        continue
      }
      val (r,c) = current.location

      // From the current position, find connecting pipes
      findAdjacent(r, c, input).forEach {
        // cost is the number of steps taken, increases by 1 for each move
        val cost = distances.getValue(current.location) + 1
        // If the cost to this space is less than what was previously known, put this on the queue
        if (cost < (distances[it] ?: Int.MAX_VALUE)) {
          distances[it] = cost
          queue.add(PathCost(it, cost))
        }
      }
    } while (queue.size > 0)
    return distances
  }

  private fun findStart(grid: List<List<Char>>): Pair<Int,Int> {
    for (r in grid.indices) {
      for (c in grid[r].indices) {
        if (grid[r][c] == 'S') {
          return Pair(r,c)
        }
      }
    }
    throw IllegalStateException("Could not find start position")
  }

  // Finds pipe segments that connect to the current pipe segment
  // checks for connection points on the adjacent segment that line up with the current segment
  private fun findAdjacent(row: Int, col: Int, grid: List<List<Char>>): List<Pair<Int,Int>> {
    return directionsFromPosition(row, col, grid).mapNotNull { direction ->
      val (r, c) = direction.nextPosition(row, col)
      if (direction.opposite() in directionsFromPosition(r, c, grid)) {
        Pair(r, c)
      } else {
        null
      }
    }
  }

  private fun directionsFromPosition(row: Int, col: Int, grid: List<List<Char>>): List<Direction> {
    return when(grid[row][col]) {
      '.' -> emptyList()
      '|' -> listOf(Direction.NORTH, Direction.SOUTH)
      '-' -> listOf(Direction.EAST, Direction.WEST)
      'L' -> listOf(Direction.NORTH, Direction.EAST)
      'J' -> listOf(Direction.NORTH, Direction.WEST)
      '7' -> listOf(Direction.WEST, Direction.SOUTH)
      'F' -> listOf(Direction.EAST, Direction.SOUTH)
      'S' -> Direction.values().toList()
      else -> throw IllegalStateException("Unknown pipe character: ${grid[row][col]}")
    }
  }

  /**
   * Use a recurse search function with memoization to find if a grid position can reach outside the pipe loop
   *
   * @param row row position to test
   * @param col column position to test
   * @param grid the full expanded grid containing spaces between non-connected pipe segments
   * @param pipeLoop to full pipe loop with connections added to enclose the interior spaces
   * @param memoOpen a set used to keep track of spaces we've previously determined are not interior spaces
   * @param path the path so far in the recursive search
   */
  private fun canAccessOutsideOfGrid(row: Int,
                                     col: Int,
                                     grid: List<List<Char>>,
                                     pipeLoop: Set<Pair<Int,Int>>,
                                     memoOpen: MutableSet<Pair<Int, Int>>,
                                     path: MutableSet<Pair<Int,Int>> = mutableSetOf()
  ): Boolean {
    path.add(Pair(row, col))

    // this space is not interior, therefore any space on our path is not interior
    if (Pair(row, col) in memoOpen){
      memoOpen.addAll(path)
      return true
    }

    // this space is at the edge of our puzzle grid, and therefor is not interior
    if (row <= 0 || row >= grid.size || col <= 0 || col >= grid[row].size ) {
      memoOpen.addAll(path)
      return true
    }

    // search in each direction that does not contain a pipe loop space
    // if any direction leads outside the pipe loop, this space cannot be interior
    return Direction.values()
      .map { it.nextPosition(row, col) }
      .filter { it !in pipeLoop && it !in path }
      .any { (r,c) -> canAccessOutsideOfGrid(r, c, grid, pipeLoop, memoOpen, path) }
  }

  override fun warmup(input: List<List<Char>>) {
    part2(input)
  }
}

enum class Direction {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  fun opposite(): Direction {
    return when(this) {
      NORTH -> SOUTH
      EAST -> WEST
      SOUTH -> NORTH
      WEST -> EAST
    }
  }

  fun nextPosition(row: Int, col: Int): Pair<Int,Int> {
    return when (this) {
      NORTH -> Pair(row - 1, col)
      SOUTH -> Pair(row + 1, col)
      EAST -> Pair(row, col + 1)
      WEST -> Pair(row, col - 1)
    }
  }
}

data class PathCost(val location: Pair<Int,Int>, val cost: Int)

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day10().run()
}
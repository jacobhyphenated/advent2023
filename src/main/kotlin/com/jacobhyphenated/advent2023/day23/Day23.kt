package com.jacobhyphenated.advent2023.day23

import com.jacobhyphenated.advent2023.Day

class Day23: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("23").lines().map { it.toCharArray().toList() }
  }

  override fun part1(input: List<List<Char>>): Int {
    val startPosition = Pair(0, 1)
    val endPosition = Pair(input.size - 1, input[0].size - 2)
    return recurseToLongestPath(startPosition, setOf(), endPosition, input).maxOf { it.size }
  }

  // loop through all grid spaces to identify "switching" points
  //    a switching point is a non # character with 2 or more adjacent non # characters
  // use modified dijkstra to make a map from each switching point to all nearest switching points with associated cost
  // this allows construction of a graph with cost between each node
  // treat the start and end nodes as switching points
  // now attempt to calculate all paths from start to end through the graph to find the maximum path (recursive?)
  // or
  // A* implementation using a max heap - always check the furthest distance from the end position first
  override fun part2(input: List<List<Char>>): Int {
    val startPosition = Pair(0, 1)
    val endPosition = Pair(input.size - 1, input[0].size - 2)
    val initialState = State(setOf(), startPosition)
    val results = mutableListOf<Int>()
    val stack = mutableListOf(initialState)
    while (stack.isNotEmpty()) {
      val currentState = stack.removeLast()
      if (currentState.position == endPosition) {
        results.add(currentState.path.size)
        continue
      }
      val (row,col) = currentState.position
      findAdjacentNoSlopes(row, col, input)
        .filter { it !in currentState.path }
        .forEach { position ->
          val newPath = currentState.path.toMutableSet().also { it.add(position) }
          stack.add(State(newPath, position))
        }
    }
    return results.max()
  }

  private fun recurseToLongestPath(current: Pair<Int,Int>,
                                   path: Set<Pair<Int,Int>>,
                                   endPosition: Pair<Int,Int>,
                                   grid: List<List<Char>>): List<Set<Pair<Int,Int>>> {
    if (current == endPosition) {
      return listOf(path)
    }
    val (row, col) = current
    return findAdjacentWithSlopes(row, col, grid)
      .filter { it !in path }
      .flatMap { position ->
        val newPath = path.toMutableSet().also { it.add(position) }
        recurseToLongestPath(position, newPath, endPosition, grid)
      }
  }

  private fun findAdjacentWithSlopes(row: Int, col: Int, grid: List<List<Char>>): List<Pair<Int,Int>> {
    return listOfNotNull(
      checkValidNeighbor(Pair(row - 1, col), '^', grid),
      checkValidNeighbor(Pair(row + 1, col), 'v', grid),
      checkValidNeighbor(Pair(row, col - 1), '<', grid),
      checkValidNeighbor(Pair(row, col + 1), '>', grid)
    )
  }

  private fun checkValidNeighbor(coord: Pair<Int,Int>, slope: Char, grid: List<List<Char>>): Pair<Int,Int>? {
    val (r, c) = coord
    return if (r < 0 || r >= grid.size || c < 0 || c >= grid[r].size) {
      null
    } else if (grid[r][c] == '.' || grid[r][c] == slope) {
      coord
    } else {
      null
    }
  }

  private fun findAdjacentNoSlopes(row: Int, col: Int, grid: List<List<Char>>): List<Pair<Int,Int>> {
    val adjacent = mutableListOf<Pair<Int,Int>>()
    for (r in (row - 1).coerceAtLeast(0) .. (row + 1).coerceAtMost(grid.size - 1)) {
      if (r == row || grid[r][col] == '#') {
        continue
      }
      adjacent.add(Pair(r, col))
    }
    for (c in (col - 1).coerceAtLeast(0) .. (col + 1).coerceAtMost(grid[0].size - 1)) {
      if (c == col || grid[row][c] == '#'){
        continue
      }
      adjacent.add(Pair(row, c))
    }
    return adjacent
  }
}

data class State(val path: Set<Pair<Int,Int>>, val position: Pair<Int,Int>)
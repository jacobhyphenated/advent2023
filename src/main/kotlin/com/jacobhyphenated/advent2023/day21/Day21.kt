package com.jacobhyphenated.advent2023.day21

import com.jacobhyphenated.advent2023.Day

class Day21: Day<List<List<Char>>> {
  override fun getInput(): List<List<Char>> {
    return readInputFile("21").lines().map { it.toCharArray().toList() }
  }

  override fun part1(input: List<List<Char>>): Any {
    return countPossibleLocations(input, 64)
  }

  override fun part2(input: List<List<Char>>): Any {
    TODO("Not yet implemented")
  }

  fun countPossibleLocations(grid: List<List<Char>>, steps: Int): Int {
    val start = grid.flatMapIndexed { r, row -> List(row.size) { c -> Pair(r,c) } }
      .first { (r,c) -> grid[r][c] == 'S' }
    var current = setOf(start)
    repeat(steps) {
      current = current.flatMap { (row, col) ->
        findAdjacent(row, col, grid)
      }.toSet()
      println("$it ${current.size}")
    }

    return current.size
  }

  private fun findAdjacent(row: Int, col: Int, grid: List<List<Char>>): List<Pair<Int,Int>> {
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
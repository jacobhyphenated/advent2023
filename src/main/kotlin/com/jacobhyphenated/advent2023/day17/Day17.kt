package com.jacobhyphenated.advent2023.day17

import com.jacobhyphenated.advent2023.Day
import java.util.*

/**
 * Day 17: Clumsy Crucible
 *
 * The cart that carries the lava needs to travel from the top left corner
 * to the bottom right of the puzzle input. Each space has a cost.
 * Need to find path with the lowest overall cost.
 *
 * However, the cart has some limitations. It can only go a maximum of 3 consecutive spaces
 * in the same direction before needing to make a 90 degree turn. It can turn 90 degrees
 * at a time (so it cannot immediately go backwards).
 */
class Day17: Day<List<List<Int>>> {
  override fun getInput(): List<List<Int>> {
    return readInputFile("17").lines().map { it.toCharArray().map { c -> c.digitToInt() } }
  }

  /**
   * Part 1: Find the minimum possible cost to get from the start ot the end
   */
  override fun part1(input: List<List<Int>>): Int {
    return findBestPathToEnd(input)
  }

  /**
   * Part 2: Now use an "Ultra" cart. This cart must move at least 4 consecutive spaces before turning.
   * It cannot go more than 10 spaces without turning. Also, it cannot stop unless it's gone 4 consecutive spaces
   * and it must come to a stop at the bottom right corner
   */
  override fun part2(input: List<List<Int>>): Any {
    return findBestPathToEnd(input, true)
  }

  /**
   * Use the same basic logic for part1 and 2 except for the boolean [useUltraPath] for part 2 that
   * toggles on additional logic for the ultra cart.
   *
   * This is an implementation of Dijkstra's algorithm that specifically allows certain spaces to be
   * traversed by a longer path, since depending on the cart state, some adjacent spaces are only valid
   * options in certain circumstances.
   */
  private fun findBestPathToEnd(input: List<List<Int>>, useUltraPath: Boolean = false): Int {
    val distances: MutableMap<Pair<Int,Int>, Int> = mutableMapOf(Pair(0,0) to 0)
    val endLocation = Pair(input.size - 1, input[0].size - 1)

    // BFS where we always look next at the path with the least cost so far
    val queue = PriorityQueue<PathCost> { a, b -> a.cost - b.cost }

    // Use two separate start states (important for part2, negligible for part 1)
    val start = CartPath(Pair(0,0), Direction.EAST, 0)
    val start2 = CartPath(Pair(0,0), Direction.SOUTH, 0)
    val visited = mutableSetOf(start, start2)

    queue.add(PathCost(start, 0))
    queue.add(PathCost(start2, 0))

    var current: PathCost

    do {
      current = queue.remove()

      // If we already found a less expensive way to reach this position (with the same cart path states)
      if (current.cost > (distances[current.cartPath.location] ?: Int.MAX_VALUE) && current.cartPath in visited) {
        continue
      }

      // Maintains a set of cart path states which include location, direction, and consecutive spaces traveled
      visited.add(current.cartPath)

      // this is the least expensive path to the end location
      if (current.cartPath.location == endLocation){
        // but for ultra mode - the path must be going for at least 4 consecutive spaces in order to stop
        if (useUltraPath) {
          if (current.cartPath.consecutive >= 4){
            return current.cost
          }
        } else {
          return current.cost
        }
        continue
      }

      // From the current position, find connecting pipes
      val adjacent = if (useUltraPath) {
        findAdjacentUltra(current.cartPath, input)
      } else {
        findAdjacent(current.cartPath, input)
      }
      adjacent.forEach {
        val (r,c) = it.location
        val cost = current.cost + input[r][c]
        // If this is the least expensive way to reach this space, record that
        if (cost < (distances[it.location] ?: Int.MAX_VALUE)) {
          distances[it.location] = cost
        }
        queue.add(PathCost(it, cost))
      }
    } while (queue.size > 0)
    return 0
  }

  /**
   * For a standard cart, adjacent spaces are spaces at 90 degree angles,
   * and forward one space if the cart has not already traveled 3 consecutive spaces-
   */
  private fun findAdjacent(cartPath: CartPath, grid: List<List<Int>>): List<CartPath> {
    val cartPaths = mutableListOf<CartPath>()
    for (direction in cartPath.direction.turn()) {
      val nextLocation = nextGridSpace(cartPath.location, direction, grid)
      if (nextLocation != null){
        cartPaths.add(CartPath(nextLocation, direction, 1))
      }
    }
    if (cartPath.consecutive < 3) {
      val nextLocation = nextGridSpace(cartPath.location, cartPath.direction, grid)
      if (nextLocation != null){
        cartPaths.add(CartPath(nextLocation, cartPath.direction, cartPath.consecutive + 1))
      }
    }
    return cartPaths
  }

  /**
   * For an ultra cart, adjacent spaces are:
   *   one forward if the cart has not already traveled 10 consecutive spaces
   *   one at each 90-degree angle if the cart has already traveled at least 4 consecutive spaces
   */
  private fun findAdjacentUltra(cartPath: CartPath, grid: List<List<Int>>): List<CartPath> {
    val cartPaths = mutableListOf<CartPath>()
    if (cartPath.consecutive >= 4) {
      for (direction in cartPath.direction.turn()) {
        val nextLocation = nextGridSpace(cartPath.location, direction, grid)
        if (nextLocation != null){
          cartPaths.add(CartPath(nextLocation, direction, 1))
        }
      }
    }
    if (cartPath.consecutive < 10) {
      val nextLocation = nextGridSpace(cartPath.location, cartPath.direction, grid)
      if (nextLocation != null){
        cartPaths.add(CartPath(nextLocation, cartPath.direction, cartPath.consecutive + 1))
      }
    }
    return cartPaths
  }

  private fun nextGridSpace(location: Pair<Int,Int>, direction: Direction, grid: List<List<Int>>): Pair<Int,Int>? {
    val (row,col) = location
    val (nextRow, nextCol) = when(direction){
      Direction.NORTH -> Pair(row - 1, col)
      Direction.SOUTH -> Pair(row + 1, col)
      Direction.EAST -> Pair(row, col + 1)
      Direction.WEST -> Pair(row, col - 1)
    }
    return if (nextRow < 0 || nextRow >= grid.size || nextCol < 0 || nextCol >= grid[row].size) {
      null
    } else {
      Pair(nextRow, nextCol)
    }
  }

  override fun warmup(input: List<List<Int>>) {
    part1(input)
  }
}

data class CartPath(val location: Pair<Int,Int>, val direction: Direction, val consecutive: Int)

data class PathCost(val cartPath: CartPath, val cost: Int)

enum class Direction {
  NORTH, SOUTH, EAST, WEST;

  fun turn(): List<Direction> {
    return when(this) {
      NORTH, SOUTH -> listOf(EAST, WEST)
      EAST, WEST -> listOf(NORTH, SOUTH)
    }
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day17().run()
}
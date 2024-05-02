package com.jacobhyphenated.advent2023.day24

import com.jacobhyphenated.advent2023.Day
import java.math.BigDecimal

/**
 * Day 24: Never Tell Me The Odds
 *
 * The input is a list of hailstones with their starting position at t=0
 * and their velocity represented by (x,y,z) in 3d space.
 */
class Day24: Day<List<String>> {
  override fun getInput(): List<String> {
    return readInputFile("24").lines()
  }

  /**
   * Part 1
   *
   * Ignore the z position for now. Consider only the x and y position and direction. Ignore the time dimension.
   * Find the intersection where each hailstone lines meet (if it exists). Count the number of intersection
   * points where the hailstone is within an x and y range of 200,000,000,000,000 to 400,000,000,000,000
   */
  override fun part1(input: List<String>): Int {
    val lines = parseInput(input)
    return countIntersectionsInRange(lines, 200_000_000_000_000, 400_000_000_000_000)
  }

  override fun part2(input: List<String>): Long {
    val lines = parseInput3D(input)
    TODO("Not yet implemented")
    // There must be some lines that intersect all hailstones in 3d space.
    // There must be a specific line that intersects each hailstone with the correct t value

    // if we have a known slope and starting position, we can try different values of (dx,dy,dz) that have the same slope

    // x,y,z = (x0, y0, z0) + t(dz, dy, dz)

    // We know there are many x/y intersects in 2-4 hundred trillion range. Might be a good bounding box to consider

  }

  fun countIntersectionsInRange(lines: List<Line2D>, minRange: Long, maxRange: Long): Int {
    val min = minRange.toBigDecimal()
    val max = maxRange.toBigDecimal()
    var intersections = 0
    // compare every hail line
    for (i in 0 until lines.size - 1) {
      for (j in i + 1 until lines.size) {
        val intersectPoint = lines[i].intersectPoint(lines[j])
        intersectPoint?.let { (x, y) ->
          if (x >= min && x <= max && y >= min && y <= max) {
            intersections++
          }
        }
      }
    }
    return intersections
  }

  fun parseInput(input: List<String>): List<Line2D> {
    return input.map { line ->
      val (positions, changes) = line.split(" @ ")
      val (x, y) = positions.split(", ").map { it.trim().toLong() }
      val (dx, dy) = changes.split(", ").map { it.trim().toLong() }
      Line2D(x, y, dx, dy)
    }
  }

  fun parseInput3D(input: List<String>): List<Line3D> {
    return input.map { line ->
      val (positions, changes) = line.split(" @ ")
      val (x, y, z) = positions.split(", ").map { it.trim().toLong() }
      val (dx, dy, dz) = changes.split(", ").map { it.trim().toLong() }
      Line3D(x, y, z, dx, dy, dz)
    }
  }
}

/**
 * Helper class that tracks the x and y values of the hailstone line
 */
data class Line2D(val x: Long, val y: Long, val dx: Long, val dy: Long) {

  private val slope = calcSlope()
  private val offset = calcOffset()

  /**
   * Find the point where two lines intersect.
   * This takes into account that the hailstones are not true lines, but rather have a starting position and direction
   * Return null if no intersection exists
   */
  fun intersectPoint(other: Line2D): Pair<BigDecimal, BigDecimal>? {
    if (this.slope == other.slope) {
      return null
    }
    val xIntercept = (other.offset - this.offset) / (this.slope - other.slope)
    val y1 = this.getYAt(xIntercept)
    val y2 = other.getYAt(xIntercept)

    // If the y values don't equal, this is likely a problem with decimal precision
    if ((y2 - y1).abs() > BigDecimal.valueOf(.001)) {
      println("$y1 != $y2")
      return null
    }

    // The lines are directional, so the intercept point may not be within the actual direction of the line
    if (!this.lineContains(xIntercept, y1) || !other.lineContains(xIntercept, y1)) {
      return null
    }
    return Pair(xIntercept, y1)
  }

  private fun getYAt(x2: BigDecimal): BigDecimal {
    return slope * x2 + offset
  }

  private fun lineContains(x2: BigDecimal, y2: BigDecimal): Boolean {
    val validX =  (dx >= 0 && x2 >= x.toBigDecimal() ) || (dx < 0 && x2 < x.toBigDecimal())
    val validY = (dy >= 0 && y2 >= y.toBigDecimal()) || (dy < 0 && y2 < y.toBigDecimal())
    return validX && validY
  }

  private fun calcSlope(): BigDecimal {
    val x2 = x + dx
    val y2 = y + dy
    return (y2 - y).toBigDecimal().setScale(SCALE) / (x2 - x).toBigDecimal().setScale(SCALE)
  }

  private fun calcOffset(): BigDecimal {
    return y.toBigDecimal().setScale(SCALE) - slope * x.toBigDecimal().setScale(SCALE)
  }

  companion object {
    // because we are dealing with values in the range of 2E14, we go beyond 64 bit double precision
    // In fact, we need big decimal precision to 5 decimal places to get the correct answers
    private const val SCALE = 5
    // scale 2 = 19953
    // scale 3 = 20961
    // scale 4 = 20962
    // scale 5 = 20963
    // scale 6 = 20963
    // scale 10= 20963
  }
}

data class Line3D(val x: Long, val y: Long, val z: Long, val dx: Long, val dy: Long, val dz: Long) {

  fun atT(t: Long): Triple<Long, Long, Long> {
    val x2 = x + dx * t
    val y2 = y + dy * t
    val z2 = z + dz * t
    return Triple(x2, y2, z2)
  }

  fun checkIfIntersects(other: Line3D): Boolean {
    val tAtXIntersect = (this.x - other.x) / (other.dx - this.dx)
    if (tAtXIntersect < 0) {
      return false
    }
    val (x1, y1, z1) = this.atT(tAtXIntersect)
    val (x2, y2, z2) = other.atT(tAtXIntersect)
    return x1 == x2 && y1 == y2 && z1 == z2
  }
}

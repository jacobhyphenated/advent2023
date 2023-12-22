package com.jacobhyphenated.advent2023.day22

import com.jacobhyphenated.advent2023.Day
import kotlin.math.max
import kotlin.math.min

/**
 * Day 22: Sand Slabs
 *
 * Blocks of sand are falling and represented by the puzzle input.
 * Each line has two 3d coordinates that describe n number of 1 cube sections of sand that make up the block.
 *
 * The floor is at z = 0, so the lowest z value possible is 1, where a block comes to rest on the floor.
 * If a part of a block touches a part of another block, that block then comes to rest in that location.
 */
class Day22: Day<List<Block>> {
  override fun getInput(): List<Block> {
    return parseInput(readInputFile("22"))
  }

  // Runs in about 12 seconds
  /**
   * Part 1: A Block can be safely disintegrated if it is not the only block supporting some other block.
   * How many blocks can be safely disintegrated?
   *
   * This takes around 12 seconds to run with almost the entire time spent finding the settled brick locations.
   * Optimizing that part of the problem (which is re-used for part 2) will have a big performance impact
   */
  override fun part1(input: List<Block>): Int {
    val (settled, supportMap) = buildSupportMap(input)

    // supportMap describes what bricks are being held up by the key block.
    // now determine which blocks are holding up the key block
    // supportedBy can only be calculated once all blocks are settled
    val supportedBy = settled.associateWith { block ->
      val result = mutableSetOf<Block>()
      for ((k,v) in supportMap) {
        if (block in v) {
          result.add(k)
        }
      }
      result
    }

    var canBeRemoved = 0
    // a block can be removed if all blocks it supports are supported by at least one other block
    settled.forEach { block ->
      val blocksItSupports = supportMap[block] ?: emptySet()
      if(blocksItSupports.all { supportedBy.getValue(it).size > 1 }) {
        canBeRemoved++
      }
    }

    return canBeRemoved
  }

  /**
   * Part 2: If we disintegrate a block that is the only block holding another block up,
   * that block will fall, along with any blocks it's solely supporting, etc.
   *
   * Determine how many other blocks will fall when each block is disintegrated
   */
  override fun part2(input: List<Block>): Any {
    val (settled, supportMap) = buildSupportMap(input)
    val supportedBy = settled.associateWith { block ->
      val result = mutableSetOf<Block>()
      for ((k,v) in supportMap) {
        if (block in v) {
          result.add(k)
        }
      }
      result
    }
    var totalCollapse = 0
    settled.forEach { block ->
      // look only at blocks that don't have other supporting blocks
      val blocksItSupports = (supportMap[block] ?: emptySet())
        .filter { supportedBy.getValue(it).size == 1 }

      // count is a unique set of blocks that will fall if [block] is disintegrated
      val count = blocksItSupports.toMutableSet()
      var chain = blocksItSupports.toList()
      while (chain.isNotEmpty()) {
        chain = chain.mapNotNull { supportMap[it] }.flatten()
            // if a block has a supporting block that is not in our count set, then it won't fall
          .filter { supportedBy.getValue(it).all { b -> b in count } }
        count.addAll(chain)
      }
      totalCollapse += count.size
    }
    return totalCollapse
  }

  /**
   * Given the starting location of each block.
   * Return the final resting location of each block.
   * And return a map of a block to the other blocks it supports,
   * that is, blocks that it is underneath and touching.
   */
  private fun buildSupportMap(input: List<Block>): Pair<List<Block>, Map<Block, Set<Block>>> {
    val orderedBlocks = input.sortedBy { it.minZ }
    val settled = mutableListOf<Block>()
    val supportMap = mutableMapOf<Block, MutableSet<Block>>()
    for (block in orderedBlocks) {
      var current = block
      while(current.minZ > 1) {
        val next = current.oneZDown()
        val supports = settled.filter { it.cubes.intersect(next.cubes).isNotEmpty() }
        if (supports.isNotEmpty()) {
          supports.forEach { support ->
            supportMap.getOrPut(support) { mutableSetOf() }.add(current)
          }
          break
        }
        current = next
      }
      settled.add(current)
    }
    return Pair(settled, supportMap)
  }

  fun parseInput(input: String): List<Block> {
    return input.lines().map { line->
      val coords = line.split("~")
      val (x1,y1,z1) = coords[0].split(",").map { it.toInt() }
      val (x2,y2,z2) = coords[1].split(",").map { it.toInt() }
      Block.fromCoordinates(x1, y1, z1, x2, y2, z2)
    }
  }
}

data class Block(val cubes: Set<Triple<Int,Int,Int>>) {
  val minZ: Int = cubes.minOf { (_,_,z) -> z }

  fun oneZDown(): Block {
    return Block(cubes.map { (x,y,z) -> Triple(x,y,z-1) }.toSet())
  }

  companion object {
    fun fromCoordinates(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Block {
      if (x1 == x2 && y1 == y2){
        return Block((min(z1,z2) .. max(z1,z2)).map { Triple(x1,y1,it) }.toSet())
      }
      else if (x1 == x2 && z1 == z2) {
        return Block((min(y1,y2) .. max(y1,y2)).map { Triple(x1, it, z1) }.toSet())
      }
      else if (y1 == y2 && z1 == z2) {
        return Block((min(x1, x2) .. max(x1, x2)).map { Triple(it, y1, z1) }.toSet())
      }
      throw IllegalStateException("Failed to parse Block from coordinates")
    }
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day22().run()
}
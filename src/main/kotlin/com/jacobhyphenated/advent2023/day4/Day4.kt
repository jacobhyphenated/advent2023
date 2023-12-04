package com.jacobhyphenated.advent2023.day4

import com.jacobhyphenated.advent2023.`**`
import com.jacobhyphenated.advent2023.Day

/**
 * Day 4: Scratchcards
 *
 * Scratch cards have lists of winning numbers and a list of your numbers
 * You have a list of scratch cards as the puzzle input
 */
class Day4: Day<List<ScratchCard>> {
  override fun getInput(): List<ScratchCard> {
    return readInputFile("4").lines().map { parseInput(it) }
  }

  /**
   * Part 1
   * A puzzle score start at 1 for one matching number then doubles for each additional matching number
   * (4 matching numbers = 8)
   * Return the total score for all scratch cards
   *
   * Use set intersection to determine the number of matches. Then exponent math to quickly calculate score
   */
  override fun part1(input: List<ScratchCard>): Int {
    return input.sumOf { card ->
      val matchCount = card.matchCount()
      if(matchCount == 0) { 0 } else { 2 `**` (matchCount - 1) }
    }
  }

  /**
   * Part 2
   *
   * Winning a scratch card only means you get additional cards. You get one additional card
   * of each of the cards below the current card based on the number of matches.
   * So if card 1 matches 4 numbers, you get an additional copy of 2,3,4,5
   *
   * How many cards do you have in total?
   */
  override fun part2(input: List<ScratchCard>): Int {
    // keep a list of the number of copies for each card type (using the puzzle input index for card id)
    val cardCounts = MutableList(input.size) { 1 }
    for (cardIndex in input.indices) {
      val matchCount = input[cardIndex].matchCount()
      // multiple copies of this card yield multiple additional copies of subsequent cards
      val multiplier = cardCounts[cardIndex]
      (cardIndex + 1 .. cardIndex + matchCount).forEach { cardCounts[it] += multiplier }
    }
    return cardCounts.sum()
  }

  fun parseInput(line: String): ScratchCard {
    val (winningNumbers, yourNumbers) = line.split(":")[1].split("|").map { numberList ->
      numberList.trim().split("\\s+".toRegex()).map { it.toInt() }
    }
    return ScratchCard(winningNumbers.toSet(), yourNumbers.toSet())
  }

  override fun warmup(input: List<ScratchCard>) {
    part1(input)
  }
}

data class ScratchCard(val winningNumbers: Set<Int>, val yourNumbers: Set<Int>) {
  fun matchCount(): Int {
    return winningNumbers.intersect(yourNumbers).size
  }
}
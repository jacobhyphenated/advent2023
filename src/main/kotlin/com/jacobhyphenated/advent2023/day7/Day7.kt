package com.jacobhyphenated.advent2023.day7

import com.jacobhyphenated.advent2023.Day

/**
 * Day 7: Camel Cards
 *
 * Camel is like poker but with its own rules on what cards win.
 * The puzzle input is a list of hands with a bet for each hand.
 *
 * The total score takes the weakest hand and multiplies the bet by 1,
 * then the second-weakest hand by 2, and so on until the strongest hand is multiplied by the total number of hands.
 */
class Day7: Day<List<Pair<Hand, Int>>> {
  override fun getInput(): List<Pair<Hand, Int>> {
    return parseInput(readInputFile("7"))
  }

  /**
   * Part 1: Add up the hand scores
   *
   * Sort the Hands (the weakest first). Then multiply each bet by index+1
   */
  override fun part1(input: List<Pair<Hand, Int>>): Int {
    return input
      .sortedBy { it.first }.reversed()
      .mapIndexed { i, (_, bet) -> (i+1) * bet }
      .sum()
  }

  /**
   * Part 2: The J cards are actually Jokers.
   * Jokers take on the whatever value gives the hand the highest score.
   * However, for tiebreakers, a J is considered the weakest card.
   */
  override fun part2(input: List<Pair<Hand, Int>>): Int {
    input.forEach { (hand, _) -> hand.applyJokers() }
    return input
      .sortedBy { it.first }.reversed()
      .mapIndexed { i, (_, bet) -> (i+1) * bet }
      .sum()
  }

  fun parseInput(input: String): List<Pair<Hand,Int>> {
    return input.lines().map { line ->
      val (cardString, bet) = line.split(" ")
      val cards = cardString.toCharArray().map { Card.fromChar(it) }
      Pair(Hand(cards), bet.toInt())
    }
  }

  override fun warmup(input: List<Pair<Hand, Int>>) {
    part1(input)
  }
}

/**
 * Track the Camel/Poker Hand.
 *
 * For part 2, we transform the cards using [modifiedCards] where the Jokers take on different card values.
 * for part 1, [modifiedCards] == [cards].
 */
class Hand(private var cards: List<Card>): Comparable<Hand> {

  private var modifiedCards = cards
  // remember hand rank to avoid repeat calculations
  private var rank: HandRank? = null

  /**
   * Remap [cards] so Jacks become Jokers (Jokers have a lower value than Jacks)
   * Find the card that appears most frequently that is no a joker.
   * Have the Jokers take on that value and store that in [modifiedCards]
   *
   * This is a mutating function that changes the internal values of the Hand class.
   */
  fun applyJokers() {
    cards = cards.map { if (it == Card.JACK) { Card.JOKER } else { it } }
    val counts = cards.groupingBy { it }.eachCount()
    if (counts[Card.JOKER] == null) {
      return
    }
    val max = counts.maxBy { (card, count) -> if (card == Card.JOKER) { 0 } else { count } }.key
    modifiedCards = cards.map { if (it == Card.JOKER) { max } else { it } }
    rank = null
  }

  /**
   * Sort. Enums are sorted by ordinal - the order they appear in the enum
   * Sort based on rank first, then compare the value of the first card in each hand.
   * Then the second, and so forth
   */
  override fun compareTo(other: Hand): Int {
    val compareRanks = this.getRank().compareTo(other.getRank())
    if (compareRanks != 0) {
      return compareRanks
    }
    for (i in cards.indices) {
      val compareCard = cards[i].compareTo(other.cards[i])
      if (compareCard != 0) {
        return compareCard
      }
    }
    return 0
  }

  /**
   * Calculate the [HandRank] of the hand. This can be derived by counting haw many times a card repeats.
   */
  private fun getRank(): HandRank {
    if (rank != null) {
      return rank!!
    }
    // Create a Map of Card -> Number of times that card appears in this hand
    val counts = modifiedCards.groupingBy { it }.eachCount()
    return when (counts.size) {
      1 -> HandRank.FIVE_OF_A_KIND
      2 -> if (counts.values.any { it == 4 }) { HandRank.FOUR_OF_A_KIND } else { HandRank.FULL_HOUSE }
      3 -> if (counts.values.any { it == 3 }) { HandRank.THREE_OF_A_KIND } else { HandRank.TWO_PAIR }
      4 -> HandRank.ONE_PAIR
      5 -> HandRank.HIGH_CARD
      else -> throw IllegalStateException("A hand must have exactly 5 cards")
    }.also { rank = it }
  }

}

enum class HandRank {
  FIVE_OF_A_KIND,
  FOUR_OF_A_KIND,
  FULL_HOUSE,
  THREE_OF_A_KIND,
  TWO_PAIR,
  ONE_PAIR,
  HIGH_CARD
}

// note: Jokers are not mapped from strings. 'J' Is treated as a Jack for part1
// and transformed into a Joker during part 2
enum class Card {
  ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO, JOKER;

  companion object {
    private val cardMap = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
      .zip(Card.values())
      .toMap()

    fun fromChar(c: Char): Card {
      return cardMap[c] ?: throw IllegalArgumentException("Invalid Character: $c")
    }
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day7().run()
}
package com.jacobhyphenated.advent2023.day20

import com.jacobhyphenated.advent2023.Day
import com.jacobhyphenated.advent2023.day8.lcm

/**
 * Day 20: Pulse Propagation
 *
 * A bunch of modules are hooked together with wires. A pulse can be sent through the
 * wires that is either a low pulse or a high pulse. Different module types react to
 * different pulses in different ways
 *
 * The puzzle input describes the different types of modules and how they are wired together.
 * There is one broadcast module that takes an input pulse and sends the same type to its output modules.
 * There is one button. When pressed, it sends a low pulse to the broadcast module
 */
class Day20: Day<String> {
  override fun getInput(): String {
    return readInputFile("20")
  }

  /**
   * Part 1: If you press the button 1000 times,
   * return the number of low pulses * the number of high pulses
   */
  override fun part1(input: String): Int {
    val modules = parseInput(input)
    val button = modules.getValue("button")
    var highCount = 0
    var lowCount = 0
    val initialState = modules.values.map { it.exportState() }.toSet()
    var i = 1
    val totalButtonPresses = 1000
    while (i <= totalButtonPresses) {
      var pulses = button.sendPulse(PulseType.LOW)
      while (pulses.isNotEmpty()) {
        lowCount += pulses.count { it.type == PulseType.LOW }
        highCount += pulses.count { it.type == PulseType.HIGH }
        pulses = pulses.flatMap { pulse ->
          val target = modules.getValue(pulse.destination)
          target.receivePulse(pulse)
        }
      }

      // This pattern matching works for the test input, but not for the puzzle input.
      // The puzzle input loop is much much longer than 1000
      if (modules.values.map { it.exportState() }.toSet() == initialState) {
        val patternRepeats = totalButtonPresses / i
        lowCount *= patternRepeats
        highCount *= patternRepeats
        i = totalButtonPresses + 1 - (totalButtonPresses % i)
      }

      i++
    }
    return highCount * lowCount
  }

  /**
   * Part 2: There is a module called rx. How many button presses does it take for rx to receive a low pulse?
   *
   * This is dirty. We solve this puzzle, not the general puzzle.
   * rx has a single connection, to gf. gf is a conjunction module with 4 inputs.
   * When all those inputs are true (send a High pulse), then it emits a low pulse.
   *
   * Assume that each of these inputs repeats in a patter (true for this puzzle).
   * Find the number of button presses for each of those inputs to be at a High pulse state.
   * Then find the LCM. That's the puzzle answer.
   */
  override fun part2(input: String): Long {
    val modules = parseInput(input)
    val button = modules.getValue("button")
    var i = 1
    val intervalPeriod = MutableList<Int?>(4) { null }
    while (true) {
      var pulses = button.sendPulse(PulseType.LOW)
      while (pulses.isNotEmpty()) {
        pulses = pulses.flatMap { pulse ->
          // lol. This does not happen, and would take forever to get there
          if (pulse.destination == "rx" && pulse.type == PulseType.LOW){
            return i.toLong()
          }
          val target = modules.getValue(pulse.destination)
          target.receivePulse(pulse)
        }

        // Did one of the inputs to gf emit a High pulse? If so record the "i" number of button presses
        modules.getValue("gf").exportState().state.forEachIndexed { index, state ->
          if (state && intervalPeriod[index] == null) {
            intervalPeriod[index] = i
          }
        }
      }
      // We know how many button presses it takes for each input to gf to be in a High pulse state.
      // We can now calculate the final answer using LCM.
      if (intervalPeriod.all { it != null }) {
        return lcm(intervalPeriod.mapNotNull { it?.toLong() })
      }
      i++
    }
  }

  private fun parseInput(input: String): Map<String, Module> {
    val map = mutableMapOf<String, Module>()
    for (line in input.lines()) {
      val (source, destinationString) = line.split(" -> ")
      val destinations = destinationString.split(", ")
      if (source == "broadcaster") {
        map[source] = BroadcastModule(source, destinations)
      }
      else if (source.startsWith("%")) {
        val s = source.removePrefix("%")
        map[s] = FlipFlopModule(s, destinations)
      }
      else if (source.startsWith("&")) {
        val s = source.removePrefix("&")
        map[s] = ConjunctionModule(s, destinations)
      }
      else {
        throw IllegalStateException("Cannot parse $source")
      }
    }
    map["button"] = ButtonModule("button", listOf("broadcaster"))
    // set the conjunction module inputs once we know the full list of modules
    map.values.filterIsInstance<ConjunctionModule>().forEach { conjunctionModule ->
      val inputs = map.values.filter { conjunctionModule.name in it.outputs}.map { it.name }
      conjunctionModule.setInputs(inputs)
    }

    // Some modules receive events but never send them. Make sure they are represented in the map
    map.values.flatMap { it.outputs }.filter { it !in map }.forEach { output ->
      map[output] = BroadcastModule(output, emptyList())
    }
    return map
  }
}

/**
 * Generic module class that represents each type of module we'll find
 */
abstract class Module(val name: String, val outputs: List<String>) {
  abstract fun receivePulse(pulse: Pulse): List<Pulse>
  abstract fun exportState(): ModuleState

  fun sendPulse(type: PulseType): List<Pulse> {
    return outputs.map { Pulse(name, it, type) }
  }
}

/**
 * A Flip Flop module emits nothing when it receives a high pulse.
 * The module is either on or off
 * If it receives a low pulse, flip the module to it's opposite state.
 * Emit a high pulse if it is now on, and low if it is now off.
 */
class FlipFlopModule(name: String, outputs: List<String>): Module(name, outputs) {
  var on = false

  override fun receivePulse(pulse: Pulse): List<Pulse> {
    if (pulse.type == PulseType.HIGH) {
      return emptyList()
    }
    on = !on
    return if (on) {
      sendPulse(PulseType.HIGH)
    } else {
      sendPulse(PulseType.LOW)
    }
  }

  override fun exportState(): ModuleState {
    return ModuleState(name, listOf(on))
  }
}

/**
 * Conjunction modules track the last pulse they receive from each of their inputs
 * They start in a "low" state.
 * When the module receives a pulse, it updates its state.
 * If all inputs are in a High state, emit a low pulse. Otherwise emit a high pulse.
 */
class ConjunctionModule(name: String, outputs: List<String>): Module(name, outputs) {
  private val lastInput = mutableMapOf<String, PulseType>()
  private lateinit var inputs: List<String>

  fun setInputs(value: List<String>) {
    inputs = value
    inputs.forEach { lastInput[it] = PulseType.LOW }
  }

  override fun receivePulse(pulse: Pulse): List<Pulse> {
    lastInput[pulse.source] = pulse.type
    return if (lastInput.values.all { it == PulseType.HIGH } ) {
      sendPulse(PulseType.LOW)
    } else {
      sendPulse(PulseType.HIGH)
    }
  }

  override fun exportState(): ModuleState {
    return ModuleState(name, inputs.map { lastInput.getValue(it) == PulseType.HIGH })
  }
}

/**
 * Broadcast modules emit each pulse they receive to each of their output modules
 */
class BroadcastModule(name: String, outputs: List<String>): Module(name, outputs) {
  override fun receivePulse(pulse: Pulse): List<Pulse> {
    return sendPulse(pulse.type)
  }

  override fun exportState(): ModuleState {
    return ModuleState(name, listOf())
  }
}

class ButtonModule(name: String, outputs: List<String>): Module(name, outputs) {
  override fun receivePulse(pulse: Pulse): List<Pulse> {
    return emptyList()
  }

  override fun exportState(): ModuleState {
    return ModuleState(name, emptyList())
  }
}

enum class PulseType {
  HIGH, LOW
}

data class Pulse(val source: String, val destination: String, val type: PulseType)

data class ModuleState(val name: String, val state: List<Boolean>)

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day20().run()
}
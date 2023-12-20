package com.jacobhyphenated.advent2023.day19

import com.jacobhyphenated.advent2023.Day
import com.jacobhyphenated.advent2023.product

/**
 * Day 19: Aplenty
 *
 * There is an elaborate system for telling if a part is approved or rejected.
 * Each part has 4 categories (x, m, a, s)
 * Each workflow has a series of steps that test one of the category values
 * and either approve, reject, or send it to another workflow step
 */
class Day19:Day<Pair<List<Part>, Map<String, Workflow>>> {
  override fun getInput(): Pair<List<Part>, Map<String, Workflow>> {
    return parseInput(readInputFile("19"))
  }

  /**
   * Part 1: Find all the approved parts
   * and add each category value together
   */
  override fun part1(input: Pair<List<Part>, Map<String, Workflow>>): Int {
    val (parts, workflow) = input
    // start at the workflow named "in"
    val start = workflow.getValue("in")
    val acceptList = mutableListOf<Part>()
    for (part in parts) {
      var currentWorkflow = start
      while(true) {
        val nextWorkflow = currentWorkflow.execute(part)
        if (nextWorkflow == "R") {
          break
        } else if (nextWorkflow == "A") {
          acceptList.add(part)
          break
        }
        else {
          currentWorkflow = workflow.getValue(nextWorkflow)
        }
      }
    }
    return acceptList.sumOf { it.categories.values.sum() }
  }

  /**
   * Part 2: Ingore the parts
   * Each category can have a possible value between 1 and 4000 inclusive.
   * How many possible parts exist that could be approved?
   */
  override fun part2(input: Pair<List<Part>, Map<String, Workflow>>): Long {
    val (_, workflowMap) = input
    // Track a range of possible values for each of x, m, a, s
    val componentRanges = listOf("x", "m", "a", "s").associateWith { Pair(1L, 4000L) }
    val start = workflowMap.getValue("in")
    return possibleCombinations(componentRanges, start, workflowMap)
  }

  /**
   * Recursive function that goes through each workflow and narrows down the range of possible approved part values.
   * The upper bound of possible combos is 4000 * 4000 * 4000 * 4000
   * With each step, we can lower the possible combos for one of x, m, a, s category values.
   * For the subsequent steps, use the remaining range of that same category
   * such that each componentRange iteration has no overlap of potential category combinations
   */
  private fun possibleCombinations(componentRanges: Map<String, Pair<Long,Long>>,
                                   workflow: Workflow,
                                   workflowMap: Map<String, Workflow> ): Long {

    // start by copying the map, so we can mutate it safely
    val newRanges = componentRanges.toList().toMap().toMutableMap()
    var sum = 0L
    for (step in workflow.steps) {

      // This is the last step in the workflow. The category ranges will not change at this point
      if (step.operation == '=') {
        if (step.destination == "A") {
          sum += newRanges.values.map { (s, e) -> e - s + 1 }.product()
        }
        else if (step.destination != "R") {
          val destination = workflowMap.getValue(step.destination)
          sum += possibleCombinations(newRanges, destination, workflowMap)
        }
        continue
      }

      // Keep track of the start and end of the range, but also the inverse values
      // adjust based on whether the operation is < or >
      var (start, end) = newRanges.getValue(step.category)
      var elseStart = start
      var elseEnd = end
      if (step.operation == '<') {
        end = step.value.toLong() - 1
        elseStart = step.value.toLong()
      }else if (step.operation == '>') {
        start = step.value.toLong() + 1
        elseEnd = step.value.toLong()
      }

      // Make a new copy of the component range map to send through the recursive function
      // update this copy with the ranged that passed the operation test
      val sendRanges = newRanges.toList().toMap().toMutableMap()
      sendRanges[step.category] = Pair(start, end)
      // At 'A', this range combination is approved. Count the possible combinations
      if (step.destination == "A") {
        // don't forget to add 1 (4000 - 1 + 1 = 4000 combinations)
        sum += sendRanges.values.map { (s, e) -> e - s + 1 }.product()
      }
      else if (step.destination != "R") {
        // recurse to find the possible combinations from the next workflow
        val destination = workflowMap.getValue(step.destination)
        sum += possibleCombinations(sendRanges, destination, workflowMap)
      }
      // update the ranges for this workflow to be those not sent to the above workflow
      newRanges[step.category] = Pair(elseStart, elseEnd)
    }
    return sum
  }

  fun parseInput(input: String): Pair<List<Part>, Map<String, Workflow>> {
    val (workflowPart, partsPart) = input.split("\n\n")
    val parts = partsPart.lines().map { line ->
      val map = line.removePrefix("{").removeSuffix("}").split(",").associate {
        val (component, value) = it.split("=")
        component to value.toInt()
      }
      Part(map)
    }

    val workflowMap = mutableMapOf<String, Workflow>()
    workflowPart.lines().forEach { line ->
      val(workflowName, stepsString) = line.removeSuffix("}").split("{")
      val steps = stepsString.split(",").map {
        if (it.contains(":")) {
          val category = it.substring(0, 1)
          val operation = it.toCharArray()[1]
          val (value, destination) = it.substring(2).split(":")
          WorkflowStep(category, operation, value.toInt(), destination)
        } else {
          // default step, always navigates to destination
          WorkflowStep(it,  '=', 0, it)
        }
      }
      workflowMap[workflowName] = Workflow(workflowName, steps)
    }
    return Pair(parts, workflowMap)
  }
}

data class Part(val categories: Map<String, Int>)

data class WorkflowStep(val category: String, val operation: Char, val value: Int, val destination: String) {

  fun execute(part: Part): String? {
    val match = when(operation) {
      '<' -> part.categories.getValue(category) < value
      '>' -> part.categories.getValue(category) > value
      '=' -> true
      else -> throw IllegalStateException("Invalid operation $operation")
    }
    return if (match) { destination } else { null }
  }
}

data class Workflow(val name: String, val steps: List<WorkflowStep>) {

  fun execute(part: Part): String {
    for (step in steps) {
      val destination = step.execute(part)
      if (destination != null) {
        return destination
      }
    }
    throw IllegalStateException("No valid destination in workflow step $name for part $part")
  }
}

fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
  Day19().run()
}
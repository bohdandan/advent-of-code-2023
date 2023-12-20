package day19

import assert
import println
import readInput
import kotlin.math.min
import kotlin.math.max

fun main() {
    data class Rule(val name: String, val operation: Char?, val limit: Int?, val destination: String?) {
        constructor(name: String) : this(name, null, null, null)
    }

    data class Workflow(val name: String, val rules: List<Rule>)

    data class Part(val params: Map<Char, Int>)
    data class PartRanges(val ranges: MutableMap<Char, Pair<Int, Int>>) {
        fun sum() = ranges.values.fold(1L) { acc, range ->
            if (range.first > range.second) throw Exception("!!!!")
            acc * (range.first..range.second).count()
        }
        fun lessThan(key: Char, limit: Int) {
            ranges[key] = ranges[key]!!.first to min(limit, ranges[key]!!.second)
        }
        fun moreThan(key: Char, limit: Int) {
            ranges[key] = max(limit, ranges[key]!!.first) to ranges[key]!!.second
        }
        fun copy(): PartRanges = PartRanges("xmas".toCharArray().associateWith { ranges[it]!!.copy() }.toMutableMap())
    }

    fun parse(input: List<String>): Pair<List<Workflow>, List<Part>> {
        val workflows = input.takeWhile { it.isNotEmpty() }.map  { it->
            val name = it.takeWhile { it != '{' }
            val rules = it.substring(name.length + 1..<it.length - 1).split(",")
                .map {rule ->
                    val name = rule.takeWhile { !"<>".contains(it) }
                    var result = Rule(name)
                    if (rule.length > name.length) {
                        val sign = rule[name.length]
                        val parts = rule.substring(name.length + 1..<rule.length).split(":")
                        val limit = parts[0].toInt()
                        val destination = parts[1]
                        result = Rule(name, sign, limit, destination)
                    }
                    result
                }
            Workflow(name, rules)
        }

        val parts = input.takeLastWhile { it.isNotEmpty() }.map  { it->
            Part(it.substring(1..it.length - 2).split(",").associate {
                val (key, value) = it.split("=")
                key.first() to value.toInt()
            })
        }
        return workflows to parts
    }

    fun nextWorkflow(list: List<Workflow>, step: String, part: Part): String {
        if (listOf("A", "R").contains(step)) return step

        val workflow = list.find { it.name == step }!!
        for (rule in workflow.rules) {
            if (rule.operation == null) {
                return nextWorkflow(list, rule.name, part)
            }
            val partParamCategory = rule.name.first()
            if (rule.operation == '>') {
                if (part.params[partParamCategory]!! > rule.limit!!) {
                    return nextWorkflow(list, rule.destination!!, part)
                }
            } else {
                if (part.params[partParamCategory]!! < rule.limit!!) {
                    return nextWorkflow(list, rule.destination!!, part)
                }
            }
        }
        return ""
    }

    fun processOne(workflows: List<Workflow>, part: Part): Long {
        if (nextWorkflow(workflows, "in", part) == "R") return 0
        return part.params.values.sum().toLong()
    }

    fun process(input: Pair<List<Workflow>, List<Part>>): Long {
        return input.second.sumOf { processOne(input.first, it) }
    }

    fun nextWorkflow2(workflows: List<Workflow>, step: String, ranges: PartRanges): Long {
        if ("R" == step) return 0L
        if ("A" == step) return ranges.sum()

        val workflow = workflows.find { it.name == step }!!
        val invertedRange = ranges.copy()
        var result = 0L
        for (rule in workflow.rules) {
            if (rule.operation == null) {
                result += nextWorkflow2(workflows, rule.name, invertedRange)
                continue
            }
            val partParamCategory = rule.name.first()
            val nextRange = invertedRange.copy()
            result += if (rule.operation == '>') {
                nextRange.moreThan(partParamCategory, rule.limit!! + 1)
                invertedRange.lessThan(partParamCategory, rule.limit)
                nextWorkflow2(workflows, rule.destination!!, nextRange)
            } else {
                nextRange.lessThan(partParamCategory, rule.limit!! - 1)
                invertedRange.moreThan(partParamCategory, rule.limit)
                nextWorkflow2(workflows, rule.destination!!, nextRange)
            }
        }

        return result
    }

    fun processAllVariations(workflows: List<Workflow>): Long {
        val result = PartRanges("xmas".toCharArray().associateWith { (1 to 4000) }.toMutableMap())

        return nextWorkflow2(workflows, "in", result)
    }

    process(parse(readInput("day19/test1")))
        .assert(19114L)

    "Part 1:".println()
    process(parse(readInput("day19/input")))
        .assert(342650L)
        .println()

    processAllVariations(parse(readInput("day19/test1")).first)
        .assert(167409079868000L)
    "Part 2:".println()
    processAllVariations(parse(readInput("day19/input")).first)
        .assert(130303473508222L)
        .println()
}
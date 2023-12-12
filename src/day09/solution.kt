package day09

import println
import readInput

fun main() {

    fun diffList(numbers: List<Long>): List<Long> {
        return (0..<(numbers.size-1)).map {
            numbers[it + 1] - numbers[it]
        }.toList()
    }

    fun extrapolateLine(numbers: List<Long>): Long {
        var diffs = diffList(numbers)
        if (diffs.sum() == 0L) return numbers.last()
        return numbers.last() + extrapolateLine(diffs)
    }

    fun toList(input: String): List<Long> {
        return input.split(" ").map { number -> number.toLong() }
    }
    check(extrapolateLine(toList("0 3 6 9 12 15")) == 18L)
    check(readInput("day09/test1").map(::toList).map(::extrapolateLine).sum() == 114L)
    "Part 1:".println()
    readInput("day09/input").map(::toList)
        .map(::extrapolateLine)
        .sum().println()

    check(extrapolateLine(toList("10 13 16 21 30 45").reversed()) == 5L)

    "Part 2:".println()
    readInput("day09/input").map(::toList)
        .map { it.reversed() }
        .map(::extrapolateLine)
        .sum().println()
}

package day08

import println
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    class Puzzle {
        var commands: String
        var network: MutableMap<String, Pair<String, String>>
        constructor(input: List<String>) {
            commands = input[0]
            network = emptyMap<String, Pair<String, String>>().toMutableMap()
            (2..<input.size).forEach{it ->
                val regex = Regex("""(\w+) = \((\w*), (\w*)\)""")
                val matchResult = regex.find(input[it])!!
                network[matchResult.groupValues[1]] = Pair(matchResult.groupValues[2], matchResult.groupValues[3])
            }
        }

        fun goThroughNetwork(start: String, isEndNode: (String) -> Boolean): Long {
            var numberOfSteps = 0L
            var nextNode = start
            do {
                var commandIndex = (numberOfSteps % commands.length).toInt()
                var entry = network[nextNode]!!
                nextNode = if (commands[commandIndex] == 'L') entry.first else entry.second
                numberOfSteps++
            } while (!isEndNode(nextNode))
            return numberOfSteps
        }
        fun countMaxSteps(): Long {
            return goThroughNetwork("AAA") { str -> str == "ZZZ" }
        }

        fun lcm(number1: Long, number2: Long): Long {
            if (number1 == 0L || number2 == 0L) {
                return 0
            }
            val absNumber1 = abs(number1.toDouble()).toLong()
            val absNumber2 = abs(number2.toDouble()).toLong()
            val absHigherNumber = max(absNumber1.toDouble(), absNumber2.toDouble()).toLong()
            val absLowerNumber = min(absNumber1.toDouble(), absNumber2.toDouble()).toLong()
            var lcm = absHigherNumber
            while (lcm % absLowerNumber != 0L) {
                lcm += absHigherNumber
            }
            return lcm
        }

        fun countMaxStepsForParallelPath(): Long {
            var loopSizes = network.keys.filter { it.endsWith('A') }
                .map { goThroughNetwork(it) { str -> str.endsWith("Z")} }
                .toList()
            loopSizes.println()
            var lcm = 1L;
            loopSizes.forEach{loopSize ->
                lcm = lcm(lcm, loopSize)
            }
            return lcm;
        }
    }

    var testPuzzle = Puzzle(readInput("day08/test1"))
    check(testPuzzle.countMaxSteps() == 2L)

    var testPuzzle2 = Puzzle(readInput("day08/test2"))
    check(testPuzzle2.countMaxSteps() == 6L)

    var puzzle = Puzzle(readInput("day08/input"))
    puzzle.countMaxSteps().println()

    var testPuzzle3 = Puzzle(readInput("day08/test3"))
    check(testPuzzle3.countMaxStepsForParallelPath() == 6L)
    //            14,321,394,058,031
    puzzle.countMaxStepsForParallelPath().println()
}
package day03

import println
import readInput

fun main() {

    class Gear(val position: Pair<Int, Int>, val numbers: List<Int>)
    class EngineSchematic {
        val EMPTY_CHAR: Char = '.'
        var schema: List<List<Char>>
        var maxColumns = 0
        var maxRows = 0

        constructor(data: List<String>) {
            maxRows = data.size
            maxColumns = data[0].length
            schema = data.map { it.toList() }
        }

        fun getChar(x: Int, y: Int): Char {
            if (x < 0 || x >= maxColumns) return EMPTY_CHAR;
            if (y < 0 || y >= maxRows) return EMPTY_CHAR;
            return schema[y][x]
        }

        fun isEnginePart(char: Char): Boolean {
            if (char == EMPTY_CHAR) return false
            return char.isLetterOrDigit().not()
        }
        fun isGear(char: Char): Boolean {
            if (char == EMPTY_CHAR) return false
            return char.isLetterOrDigit().not()
        }

        fun getNumberNeighbourPositions(number: String, x: Int, y: Int): List<Pair<Int, Int>> {
            val startX = x - number.length
            val endX = x + 1
            val xCoordinatesRange = startX ..endX
            val neighbourPositions = xCoordinatesRange.asSequence().map { Pair(it, y - 1) }.toList().toMutableList()
            neighbourPositions += Pair(startX, y)
            neighbourPositions += Pair(endX, y)
            neighbourPositions += xCoordinatesRange.asSequence().map { Pair(it, y + 1) }.toList()
            return neighbourPositions;
        }
        fun checkIfEnginePart(number: String, x: Int, y: Int): Int {
            if (number.isEmpty()) return 0
            if (getNumberNeighbourPositions(number, x, y).any{isEnginePart(getChar(it.first, it.second))}) {
                return number.toInt()
            } else {
                return 0
            }
        }
        fun sumEngineParts(): Int {
            var sum = 0

            for ((y, row) in schema.withIndex()) {
                var number = ""
                for ((x, char) in row.withIndex()) {
                    if (char.isDigit()) {
                        number += char
                    } else {
                        sum += checkIfEnginePart(number, x - 1, y)
                        number = ""
                    }
                }
                sum += checkIfEnginePart(number, maxColumns - 1, y)
            }

            return sum
        }

        fun getNeighboringGears(number: String, x: Int, y: Int): List<Gear> {
            if (number.isEmpty()) return emptyList()

            return getNumberNeighbourPositions(number, x, y)
                .filter { isGear(getChar(it.first, it.second))}
                .map { Gear(it, listOf(number.toInt())) }
                .toList();
        }


        fun getGearRatio(): Int {
            var gearCandidates = emptyList<Gear>()

            for ((y, row) in schema.withIndex()) {
                var number = ""
                for ((x, char) in row.withIndex()) {
                    if (char.isDigit()) {
                        number += char
                    } else {
                        gearCandidates += getNeighboringGears(number, x - 1, y)
                        number = ""
                    }
                }
                gearCandidates += getNeighboringGears(number, maxColumns - 1, y)
            }

            val groupedByPosition = gearCandidates.groupBy { it.position }
            var result = 0
            groupedByPosition.forEach { (position, numbers) ->
                if (numbers.size == 2) {
                    result += numbers[0].numbers[0] * numbers[1].numbers[0]
                }

            }
            return result
        }
    }

    val testInput1 = readInput("day03/test1")
    var testEngineSchematic = EngineSchematic(testInput1);
    check(testEngineSchematic.sumEngineParts() == 4361)
    check(testEngineSchematic.getGearRatio() == 467835)

    val input = readInput("day03/input")
    var engineSchematic = EngineSchematic(input);
    engineSchematic.sumEngineParts().println()
    engineSchematic.getGearRatio().println()
}

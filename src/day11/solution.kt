package day11

import println
import readInput

fun main() {
    class Galaxy(val id: Int, val x: Int, val y: Int)
    class GalaxyGame() {
        lateinit var galaxies: List<Galaxy>
        val GALAXY = '#'
        constructor(input: List<String>, emptySpaceMultiplier: Int = 2) : this() {
            this.galaxies = calculateGalaxyCoordinates(input.map { it.toCharArray() }, emptySpaceMultiplier)
        }
        fun calculateGalaxyCoordinates(input: List<CharArray>, emptySpaceMultiplier: Int): List<Galaxy> {
            val emptyRows = input.mapIndexed { index, row -> if (row.contains(GALAXY)) -1 else index }
                .filter { it >= 0 }
                .toList()
            val emptyColumns = input[0].indices.map { column ->
                if (input.indices.any { row -> input[row][column] == GALAXY}) - 1 else column
            }.filter { it >= 0 }
                .toList()

            var id = 1
            val galaxiesList = mutableListOf<Galaxy>()
            input.forEachIndexed { rowNumber, row ->
                row.forEachIndexed { columnNumber, char ->
                    if (char == GALAXY) {
                        var x = columnNumber + emptyColumns.count { it < columnNumber } * (emptySpaceMultiplier - 1)
                        var y = rowNumber + emptyRows.count {it < rowNumber} * (emptySpaceMultiplier - 1)
                        galaxiesList += Galaxy(id++, x, y)
                    }
                }
            }
            return galaxiesList
        }

        fun calculateManhattanDistance(galaxy1: Galaxy, galaxy2: Galaxy): Int {
            return Math.abs(galaxy2.x - galaxy1.x) + Math.abs(galaxy2.y - galaxy1.y)
        }
        fun getSumOfShortestDistances(): Long {
            var sumOfShortestPaths = 0L
            galaxies.forEachIndexed {index, galaxy1 ->
                ((index + 1)..<galaxies.size).asSequence().forEach { index2 ->
                    var distance = calculateManhattanDistance(galaxy1, galaxies[index2])
                    sumOfShortestPaths += distance
                }
            }

            return sumOfShortestPaths
        }
    }


    var testGalaxyGame = GalaxyGame(readInput("day11/test1"))
    check(testGalaxyGame.getSumOfShortestDistances() == 374L)

    var galaxyGame = GalaxyGame(readInput("day11/input"))
    galaxyGame.getSumOfShortestDistances().println()

    var testGalaxyGame2 = GalaxyGame(readInput("day11/test1"),10)
    check(testGalaxyGame2.getSumOfShortestDistances() == 1030L)

    var testGalaxyGame3 = GalaxyGame(readInput("day11/test1"),100)
    check(testGalaxyGame3.getSumOfShortestDistances() == 8410L)

    var galaxyGame2 = GalaxyGame(readInput("day11/input"), 1000000)
    galaxyGame2.getSumOfShortestDistances().println()
}
package day14

import println
import readInput

fun main() {
    val ROLLING_ROCK = 'O'
    val SQAURE_ROCK = '#'
    val EMPTY = '.'

    fun cacheKey(map: Array<Array<Char>>): String {
        return map.joinToString(separator = "") { it.joinToString(separator = "") }
    }

    fun tiltNorth(map: Array<Array<Char>>): Array<Array<Char>> {
        val height = map.size
        val width = map[0].size
        var emptyPositionInColumn = List(width) {height}.toMutableList()
        for (rowIndex in (0 ..<height)) {
            for (columnIndex in (0 ..<width)) {
                if (map[rowIndex][columnIndex] == EMPTY && rowIndex < emptyPositionInColumn[columnIndex]) {
                    emptyPositionInColumn[columnIndex] = rowIndex
                    continue
                }
                if (map[rowIndex][columnIndex] == SQAURE_ROCK) {
                    emptyPositionInColumn[columnIndex] = height
                    continue
                }
                if (map[rowIndex][columnIndex] == ROLLING_ROCK) {
                    if (emptyPositionInColumn[columnIndex] < rowIndex) {
                        map[rowIndex][columnIndex] = EMPTY
                        map[emptyPositionInColumn[columnIndex]][columnIndex] = ROLLING_ROCK
                        emptyPositionInColumn[columnIndex]++
                    }
                }
            }
        }
        return map
    }
    fun rotateClockwise(map: Array<Array<Char>>): Array<Array<Char>> {
        val oldHeight = map.size
        val oldWidth = map[0].size
        return Array(oldWidth) { newRowIndex -> Array(oldHeight) {newColumnIndex -> map[oldHeight - newColumnIndex - 1][newRowIndex]} }
    }

    fun print(map: Array<Array<Char>>) {
        map.forEach {it ->
            String(it.toCharArray()).println()
        }
    }


    fun calculateWeight(map: Array<Array<Char>>): Long {
        return map.mapIndexed { rowIndex, row ->
            row.sumOf {
                if (it == ROLLING_ROCK) (map.size - rowIndex).toLong() else 0L
            }
        }.sum()
    }

    fun tiltAndCalculateWeight(input: List<String>): Long {
        val platform = tiltNorth(input.map { it.toCharArray().toTypedArray() }.toTypedArray())
        var result = calculateWeight(platform)
        result.println()
        return result
    }

    fun spinAndCalculateWeight(input: List<String>): Long {
        var map = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        var previousPermutations = mutableListOf<Pair<String, Long>>()

        val iterations = 1_000_000_000
        var result = 0L
        for (i in 1..iterations) {
            val cacheKey = cacheKey(map)
            if (previousPermutations.any{ it.first == cacheKey}) {
                "Loop detected".println()
                var loopSize = previousPermutations.size - previousPermutations.indexOfFirst{ it.first == cacheKey}
                var loopStartPosition = previousPermutations.size - loopSize
                "Loop size: ${loopSize}; loop start: $loopStartPosition ".println()
                var resultIndex = (iterations - loopStartPosition) % loopSize + loopStartPosition
                "resultIndex: $resultIndex".println()
                result = previousPermutations[resultIndex].second
                break
            }
            previousPermutations += Pair(cacheKey, calculateWeight(map))
            map = tiltNorth(map)
            map = rotateClockwise(map)
            map = tiltNorth(map)
            map = rotateClockwise(map)
            map = tiltNorth(map)
            map = rotateClockwise(map)
            map = tiltNorth(map)
            map = rotateClockwise(map)
            "$i -> ${calculateWeight(map)}".println()
        }

        return result
    }

    check(tiltAndCalculateWeight(readInput("day14/test1")) == 136L)
    "Part 1:".println()
    tiltAndCalculateWeight(readInput("day14/input")).println()

    check(spinAndCalculateWeight(readInput("day14/test1")) == 64L)

    "Part 2:".println()
    spinAndCalculateWeight(readInput("day14/input")).println()
}




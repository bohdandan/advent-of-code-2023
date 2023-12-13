package day13

import println
import readInput

fun main() {

    class Block(block: List<String>) {
        val width = block[0].length
        val height = block.size
        var map = block.map { it.toCharArray() }
        var smudgePoint: Pair<Int, Int> = Pair(0, 0)
        var withSmudgePoint = false
        var OPPOSITE = mapOf('.' to '#', '#' to '.')

        fun get(row: Int, column: Int): Char {
            var ch = map[row][column]
            if (withSmudgePoint && smudgePoint.first == row && smudgePoint.second == column) {
                return OPPOSITE[ch]!!
            }
            return ch
        }
        fun checkHorizontalMirror(index: Int): Boolean {
            var steps = intArrayOf(height - index, index).min();
            return (1 .. steps).all {step ->
                (0..<width).all { it ->
                    get(index - step, it) == get(index + (step - 1), it)
                }
            }
        }

        fun checkVerticalMirror(index: Int): Boolean {
            var steps = intArrayOf(width - index, index).min();
            return (1 .. steps).all {step ->
                (0..<height).all { it ->
                    get(it, index - step) == get(it, index + (step - 1))
                }
            }
        }
        fun reflectionPoints(differentFrom: Int = 0): Int {
            for ( i in 1..<height) {
                if (checkHorizontalMirror(i) && i * 100 != differentFrom) return i * 100
            }

            for ( i in 1..<width) {
                if (checkVerticalMirror(i) && i != differentFrom) return i
            }
            return 0
        }
        fun findSmuggedReflectionPoint(): Int {
            var originalReflectionPoint = reflectionPoints()
            withSmudgePoint = true
            for(row in 0..<height) {
                for(column in 0..<width) {
                    smudgePoint = Pair(row, column)
                    var result = reflectionPoints(originalReflectionPoint)
                    if (result > 0 && result != originalReflectionPoint) {
                        "Smudge point: ($row, $column) [$originalReflectionPoint]-> $result".println()
                        return result
                    }
                }
            }
            "Error!!!".println()
            return 0
        }
    }

    fun originalReflectionPoint(block: List<String>): Int {
        var result = Block(block).reflectionPoints()
        return result
    }
    fun smudgeReflectionPoint(block: List<String>): Int {
        var result = Block(block).findSmuggedReflectionPoint()
        return result
    }

    fun parseBlocks(input: List<String>): List<List<String>> {
        var result = mutableListOf<List<String>>()
        var currentBlock = mutableListOf<String>()
        input.forEach{
            if(it.isEmpty()) {
                result += currentBlock
                currentBlock = mutableListOf()
            } else {
                currentBlock += it
            }
        }
        result += currentBlock
        return result
    }

    check(parseBlocks(readInput("day13/test1")).sumOf(::originalReflectionPoint) == 405)

    "Part 1:".println()
    parseBlocks(readInput("day13/input")).sumOf(::originalReflectionPoint).println()


    check(parseBlocks(readInput("day13/test1")).sumOf(::smudgeReflectionPoint) == 400)
    "Part 2:".println()
    parseBlocks(readInput("day13/test2")).sumOf(::smudgeReflectionPoint).println()
    parseBlocks(readInput("day13/input")).sumOf(::smudgeReflectionPoint).println()
}


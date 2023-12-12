package day12

import println
import readInput

fun main() {
    var UNKNOWN = '?'
    var WORKING = '.'
    var BROKEN = '#'

    fun findVariations(input: Pair<String, List<Int>>): Long {
        var map = input.first
        var blocks = input.second
        var cache = mutableMapOf<Triple<Int, Int, Int>, Long>()

        fun solve(mapIndex: Int, blockIndex: Int, positionInBlock: Int): Long {
            var key = Triple(mapIndex, blockIndex, positionInBlock)
            if (cache.containsKey(key)) return cache[key]!!
            if (mapIndex == map.length) {
                return when {
                    blockIndex == blocks.size && positionInBlock == 0 -> 1L
                    blockIndex == blocks.size - 1 && blocks[blockIndex] == positionInBlock -> 1L
                    else -> 0L
                }
            }

            var result = 0L
            if (map[mapIndex] == WORKING || map[mapIndex] == UNKNOWN) {
                if (positionInBlock == 0) {
                    result += solve(mapIndex + 1, blockIndex, 0)
                } else if (positionInBlock > 0 && blockIndex < blocks.size && blocks[blockIndex] == positionInBlock) {
                    result += solve(mapIndex + 1, blockIndex + 1, 0)
                }
            }
            if (map[mapIndex] == BROKEN || map[mapIndex] == UNKNOWN) {
                result += solve(mapIndex + 1, blockIndex, positionInBlock + 1)
            }
            cache[key] = result
            return result
        }

        var variations = solve(0,0,0)
        "${input.first} ::: ${input.second} -> $variations".println()
        return variations
    }

    fun parse(input: String): Pair<String, List<Int>> {
        var parts = input.split(" ")
        return Pair(parts[0], parts[1].split(",").map { it.toInt() })
    }

    fun unfold(input: Pair<String, List<Int>>): Pair<String, List<Int>> {
        var map = List(5) {input.first}.joinToString("?")
        return Pair(map, List(5) { input.second }.flatten())
    }

    check(findVariations(parse("???.### 1,1,3")) == 1L)
    check(findVariations(parse(".??..??...?##. 1,1,3")) == 4L)
    check(findVariations(parse("?###???????? 3,2,1")) == 10L)
    check(readInput("day12/test1").map(::parse).sumOf(::findVariations) == 21L)
    "Part 1:".println()
    readInput("day12/input").map(::parse).sumOf(::findVariations).println()

    check(readInput("day12/test1").map(::parse)
        .map(::unfold)
        .map(::findVariations)
        .sum() == 525152L)

    "Part 2:".println()
    readInput("day12/input").map(::parse)
        .map(::unfold)
        .sumOf(::findVariations).println()
}


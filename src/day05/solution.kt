package day05

import println
import readInput

fun main() {
    class MappingLine(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {
        fun isApplicable(input: Long): Boolean {
            return sourceRangeStart <= input &&
                    sourceRangeStart + rangeLength > input
        }
        fun map(input: Long): Long {
            var position = input - sourceRangeStart
            return destinationRangeStart + position
        }
    }
    class Mapping(var name: String, var level: Int, var mappingLines: List<MappingLine>) {
        fun map(input: Long): Long {
            var mappingLine = mappingLines.find { it.isApplicable(input)}
            if (mappingLine == null) {
                return input
            } else {
                return mappingLine.map(input)
            }
        }
    }
    class Almanac {
        var seeds: List<Long> = emptyList()
        var mappings: List<Mapping> = emptyList()
        var seedRanges: Boolean = false

        constructor(input: List<String>, seedRangesInput: Boolean = false) {
            seedRanges = seedRangesInput
            var beginningOfBlock = true
            var level = 0
            for ((index, row) in input.withIndex()) {
                if (index == 0) {
                    val numbers = "\\d+".toRegex().findAll(row)
                        .map { it.value.toLong() }
                        .toList()
                        seeds = numbers
                    continue
                }

                if (row.isEmpty()) {
                    beginningOfBlock = true
                    continue
                }

                if (beginningOfBlock) {
                    beginningOfBlock = false
                    mappings += Mapping(row, level++, emptyList())
                    continue
                } else {
                    val numbers = row.split(" ")
                    mappings.last().mappingLines += MappingLine(numbers[0].toLong(), numbers[1].toLong(), numbers[2].toLong())
                }
            }
        }

        fun mapThrough(seed: Long): Long {
            var nextLevelValue = seed;
            mappings.forEach{
                nextLevelValue = it.map(nextLevelValue)
            }
            return nextLevelValue;
        }

        fun lowestNumber(): Long {
            if (seedRanges) {
                var min = Long.MAX_VALUE
                for (i in seeds.indices step 2) {
                    "Interval $i".println()
                    (seeds[i]..<seeds[i] + seeds[i + 1]).forEach {
                        val mapped = mapThrough(it)
                        if (mapped < min) {
                            min = mapped
                        }
                    }
                }
                return min;
            } else {
                return seeds.map { mapThrough(it) }.min();
            }
        }
    }


    val testAlmanac = Almanac(readInput("day05/test1"))
    check(testAlmanac.lowestNumber() == 35L)

    val almanac = Almanac(readInput("day05/input"))
    almanac.lowestNumber().println()

    val testAlmanac2 = Almanac(readInput("day05/test1"), true)
    check(testAlmanac2.lowestNumber() == 46L)

    val almanac2 = Almanac(readInput("day05/input"), true)
    almanac2.lowestNumber().println()
}
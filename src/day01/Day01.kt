package day01

import println
import readInput

fun main() {
    var spelledDigits = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )
    
    fun getFirstDigit(str: String, fromTheBeginning: Boolean = true): Int {
        var input = if (fromTheBeginning) str else str.reversed()
        for (index in str.indices) {
            val char = input[index]
            if (char.isDigit()) return Character.getNumericValue(char);
            for ((key, value) in spelledDigits) {
                var searchString = if (fromTheBeginning) key else key.reversed()
                if (input.startsWith(searchString, index)) return value
            }
        }
        return 0;
    }

    fun decode(input: List<String>): Int {
         return input.stream()
             .map {it ->
                 var first = getFirstDigit(it)
                 var last = getFirstDigit(it, false)
                 "$first$last".toInt()
             }
             .toList().sum()
    }
    check(decode(listOf("two1nine")) == 29)
    val testInput1 = readInput("day01/test1")
    check(decode(testInput1) == 142)
    val testInput2 = readInput("day01/test2")
    check(decode(testInput2) == 281)

    val input = readInput("day01/input")
    decode(input).println()
}

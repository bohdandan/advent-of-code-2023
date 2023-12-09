package day06

import println
import readInput
import kotlin.math.sqrt

fun main() {

    class Race(val time: Long, val distance: Long) {
        fun getNumberOfWaysYouCanWin(): Long {
//            d = t*th - th^2
//            th^2 - t*th + d = 0
//            D= t^2 – 4d
//            x1 = t - sqrt(D) / 2
//            x2 = t + sqrt(D) / 2
            val discriminant = time * time - 4L * distance
            val min = (time - sqrt(discriminant.toDouble())) / 2
            val max = (time + sqrt(discriminant.toDouble())) / 2

            var result = (Math.ceil(max) - Math.floor(min) - 1).toLong()
            return result
        }
    }

    class GameEngine {
        var races: List<Race> = emptyList()
        constructor(input: List<String>, ignoreSpaces: Boolean = false) {
            var timeString = input[0]
            var distanceString = input[1]
            if (ignoreSpaces) {
                timeString = timeString.replace(" ", "")
                distanceString = distanceString.replace(" ", "")
            }
            val times = "\\d+".toRegex().findAll(timeString)
                .map { it.value.toLong() }
                .toList()
            val distances = "\\d+".toRegex().findAll(distanceString)
                .map { it.value.toLong() }
                .toList()

            for ((index, time) in times.withIndex()) {
                races += Race(time, distances[index])
            }
        }
        fun getNumberOfWaysYouCanWin(): Long {
            return races.map { it.getNumberOfWaysYouCanWin() }
                .reduce { acc, element -> acc * element }
        }
    }

    val testGameEngine = GameEngine(readInput("day06/test1"))
    check(testGameEngine.getNumberOfWaysYouCanWin() == 288L)

    val gameEngine = GameEngine(readInput("day06/input"))
    gameEngine.getNumberOfWaysYouCanWin().println()

    val testGameEngine2 = GameEngine(readInput("day06/test1"), true)
    check(testGameEngine2.getNumberOfWaysYouCanWin() == 71503L)

    val gameEngine2 = GameEngine(readInput("day06/input"), true)
    gameEngine2.getNumberOfWaysYouCanWin().println()

}
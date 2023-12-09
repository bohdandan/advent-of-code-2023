package day02

import println
import readInput
import kotlin.streams.asSequence

enum class CubeColor {
    RED, GREEN, BLUE;
    companion object {
        fun find(colorString: String): CubeColor? {
            return values().find { it.name.equals(colorString, ignoreCase = true) }
        }
    }
}
fun main() {

    class CubeSet(val cubes: Map<CubeColor, Int>) {
        fun isPossible(bagContent: CubeSet): Boolean {
            return !cubes.entries.stream().anyMatch {
                it.value > bagContent.cubes[it.key]!!
            }
        }
    }
    class Game (val id: Int, val draws: List<CubeSet>) {
        fun calculatePowerOfCubeSet(): Int {
            val cubeSet = mutableMapOf(CubeColor.RED to 0, CubeColor.GREEN to 0, CubeColor.BLUE to 0)
            draws.forEach { draw ->
                draw.cubes.forEach {
                    if (it.value > cubeSet[it.key]!!) {
                        cubeSet[it.key] = it.value
                    }
                }
            }
            return cubeSet.values.fold(1) { acc, element -> acc * element }
        }
    }

    fun parseGame(input: String) : Game {
        //    Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        val gamePattern = Regex("Game (\\d+): (.*)")
        val match = gamePattern.findAll(input).first()
        val gameId = match.groupValues[1].toInt()
        val colorInfo = match.groupValues[2]
        val colorEntries = colorInfo.split(";")
        val draws = mutableListOf<CubeSet>();
        for (entry in colorEntries) {
            val colorPattern = Regex("(\\d+) (\\w+)")
            val colorMatch = colorPattern.findAll(entry)
            val cubesInfo = mutableMapOf<CubeColor, Int>()
            for (colorMatchResult in colorMatch) {
                val quantity = colorMatchResult.groupValues[1].toInt()
                val color = colorMatchResult.groupValues[2]
                cubesInfo[CubeColor.find(color)!!] = quantity
            }
            draws.add(CubeSet(cubesInfo))
        }

        return Game(gameId, draws);
    }

    fun validateGame(game: Game, bagContent: CubeSet): Boolean {
        return !game.draws.any { !it.isPossible(bagContent)}
    }

    fun checkGames(input: List<String>, bagContent: CubeSet): Int {
        val games = input.stream().map{parseGame(it)}.toList()
        return games.stream().filter{validateGame(it, bagContent)}.map { it.id }.asSequence().sum()
    }

    fun calculateSumOfPowersOfCubeSet(input: List<String>): Int {
        val games = input.stream().map{parseGame(it)}.toList()

        return games.stream().map {it.calculatePowerOfCubeSet()}.asSequence().sum()
    }

    val testInput1 = readInput("day02/test1")
    check(checkGames(testInput1, CubeSet(mapOf(CubeColor.RED to 12, CubeColor.GREEN to 13, CubeColor.BLUE to 14))) == 8)

    val input1 = readInput("day02/input")
    checkGames(input1, CubeSet(mapOf(CubeColor.RED to 12, CubeColor.GREEN to 13, CubeColor.BLUE to 14))).println()
    check(calculateSumOfPowersOfCubeSet(testInput1) == 2286)
    calculateSumOfPowersOfCubeSet(input1).println()

}

package day18

import Direction
import Position
import assert
import plus
import println
import readInput
import times
import kotlin.math.absoluteValue

fun main() {
    class Command(val direction: Direction, val steps: Int, val hexColor: Int)

    fun parse(input: List<String>): List<Command> {
        return input.map { row ->
            val parts = row.split(" ")
            val direction = Direction.entries.first { it.name.startsWith(parts[0]) }
            val steps = parts[1].toInt()
            val color = parts[2].substring(2, 8).toInt(16)
            Command(direction, steps, color)
        }
    }

    fun List<Pair<Direction, Int>>.volume() = (
        runningFold(Position(0, 0)) { position, (direction, steps) -> position + direction.position * steps}
            .zipWithNext { (y1, x1), (_, x2) ->
                (x2 - x1) * y1.toLong()
            }.sum().absoluteValue + sumOf { it.second } / 2 + 1)

    parse(readInput("day18/test1"))
        .map { it.direction to it.steps }
        .volume()
        .assert(62L)

    "Part 1:".println()
    parse(readInput("day18/input"))
        .map { it.direction to it.steps }
        .volume()
        .assert(46334L)

    val codedDirections = mapOf(0 to Direction.RIGHT, 1 to Direction.DOWN, 2 to Direction.LEFT, 3 to Direction.UP)
    parse(readInput("day18/test1"))
        .map {command ->
            val direction = codedDirections[command.hexColor % 16]!!
            val steps = command.hexColor / 16
            direction to steps
        }
        .volume()
        .assert(952408144115L)
        .println()

    "Part 2:".println()

    parse(readInput("day18/input"))
        .map {command ->
            val direction = codedDirections[command.hexColor % 16]!!
            val steps = command.hexColor / 16
            direction to steps
        }
        .volume()
        .assert(102000662718092L)
        .println()
}
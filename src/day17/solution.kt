package day17

import assert
import println
import readInput
import java.util.*

enum class Direction(val row: Int, val column: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    val opposite by lazy {
        when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }
}
fun main() {
    data class Position(val row: Int, val column: Int) {
        fun move(direction: Direction) = Position(row + direction.row, column + direction.column)
    }

    data class State(val position: Position, val direction: Direction, val stepsMadeInDirection: Int) {
        fun forward(): State = State(position.move(direction), direction, stepsMadeInDirection + 1)
        fun turns(): List<State> = Direction.entries
            .filter { it != direction && it != direction.opposite}
            .map { State(position.move(it), it, 1) }
    }
    data class QueueItem(val weight: Int, val state: State): Comparable<QueueItem> {
        override fun compareTo(other: QueueItem) = weight.compareTo(other.weight)
    }

    data class PathConfiguration(val minForward: Int, val maxForward: Int)

    class PathFinder(input: List<String>, val pathConfig: PathConfiguration) {
        val map = input.map { row -> row.map { it.toString().toInt() } }.toList()
        val height = map.size
        val width = map.first().size
        val exit = Position(height - 1, width - 1)

        private fun getWeight(position: Position): Int {
            return map[position.row][position.column]
        }

        private fun isValidNext(position: Position): Boolean {
            if (position.row < 0 || position.row >= height) return false
            if (position.column < 0 || position.column >= width) return false
            return true
        }

        fun nextSteps(currentState: State): List<State> {
            val result = mutableSetOf<State>()
            if (currentState.stepsMadeInDirection >= pathConfig.minForward) {
                result.addAll(currentState.turns())
            }
            if (currentState.stepsMadeInDirection < pathConfig.maxForward) {
                result += currentState.forward()
            }
            return result.toList()
        }

        fun getMinPathHeat(): Int {
            val queue = PriorityQueue<QueueItem>()
            val stepRight = State(Position(0, 0), Direction.RIGHT, 0)
            val stepDown = State(Position(0, 0), Direction.DOWN, 0)

            val seen = mutableSetOf(stepRight, stepDown)
            queue.add(QueueItem(0, stepRight))
            queue.add(QueueItem(0, stepDown))

            while (queue.isNotEmpty()) {
                val queueItem = queue.poll()

                if (queueItem.state.position == exit && queueItem.state.stepsMadeInDirection >= pathConfig.minForward)
                    return queueItem.weight

                nextSteps(queueItem.state)
                    .filter { isValidNext(it.position) }
                    .filter { !seen.contains(it)}
                    .forEach {
                        queue.add(QueueItem(queueItem.weight + getWeight(it.position), it))
                        seen.add(it)
                    }
            }
            return 0
        }
    }

    val largeCruciblesConfig = PathConfiguration(0, 3)
    PathFinder(readInput("day17/test1"), largeCruciblesConfig)
        .getMinPathHeat()
        .assert(102)

    "Part 1:".println()
    PathFinder(readInput("day17/input"), largeCruciblesConfig)
        .getMinPathHeat()
        .assert(817)
        .println()

    val ultraCruciblesConfig = PathConfiguration(4, 10)
    PathFinder(readInput("day17/test1"), ultraCruciblesConfig)
        .getMinPathHeat()
        .assert(94)
    PathFinder(readInput("day17/test2"), ultraCruciblesConfig)
        .getMinPathHeat()
        .assert(71)

    "Part 2:".println()
    PathFinder(readInput("day17/input"), ultraCruciblesConfig)
        .getMinPathHeat()
        .assert(925)
        .println()
}





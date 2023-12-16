package day16

import assert
import println
import readInput

enum class Direction(val row: Int, val column: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}

fun main() {
    val OPPOSITES = mapOf(
        Direction.UP to Direction.DOWN,
        Direction.DOWN to Direction.UP,
        Direction.LEFT to Direction.RIGHT,
        Direction.RIGHT to Direction.LEFT
    )
    class Path(val from: Direction, val to: List<Direction>)
    class Position(val row: Int, val column: Int) {
        fun move(direction: Direction): Position {
            return Position(row + direction.row, column + direction.column)
        }
    }
    class LavaProductionFacility(input: List<String>) {
        val map = input.map { it.toCharArray() }
        val height = map.size
        val width = map.first().size

        inner class LightTrace(width: Int, height: Int) {
            val map = Array(height) { Array(width) { mutableListOf<Path>() } }
            fun addIfNotExist(position: Position, path: Path): Boolean {
                if (map[position.row][position.column].contains(path)) return false
                map[position.row][position.column] += path
                return true
            }

            fun sum(): Int {
                return map.sumOf { row ->
                    row.count { it.size > 0 }
                }
            }
        }

        val tileTypes = hashMapOf(
            '|' to listOf(
                Path(Direction.UP, listOf(Direction.DOWN)),
                Path(Direction.DOWN, listOf(Direction.UP)),
                Path(Direction.LEFT, listOf(Direction.UP, Direction.DOWN)),
                Path(Direction.RIGHT, listOf(Direction.UP, Direction.DOWN))
            ),
            '-' to listOf(
                Path(Direction.LEFT, listOf(Direction.RIGHT)),
                Path(Direction.RIGHT, listOf(Direction.LEFT)),
                Path(Direction.UP, listOf(Direction.LEFT, Direction.RIGHT)),
                Path(Direction.DOWN, listOf(Direction.LEFT, Direction.RIGHT))
            ),
            '\\' to listOf(
                Path(Direction.LEFT, listOf(Direction.DOWN)),
                Path(Direction.DOWN, listOf(Direction.LEFT)),
                Path(Direction.RIGHT, listOf(Direction.UP)),
                Path(Direction.UP, listOf(Direction.RIGHT))
            ),
            '/' to listOf(
                Path(Direction.LEFT, listOf(Direction.UP)),
                Path(Direction.UP, listOf(Direction.LEFT)),
                Path(Direction.RIGHT, listOf(Direction.DOWN)),
                Path(Direction.DOWN, listOf(Direction.RIGHT))
            ),
            '.' to listOf(
                Path(Direction.LEFT, listOf(Direction.RIGHT)),
                Path(Direction.RIGHT, listOf(Direction.LEFT)),
                Path(Direction.UP, listOf(Direction.DOWN)),
                Path(Direction.DOWN, listOf(Direction.UP))
            )
        )

        private fun get(position: Position): Char {
            return map[position.row][position.column]
        }
        private fun isValid(position: Position): Boolean {
            if (position.row !in 0..<height) return false
            if (position.column !in 0..<width) return false
            return true
        }

        private  fun lightStep(lightTrace: LightTrace, position: Position, direction: Direction) {
            if (!isValid(position)) return
            var path = tileTypes[get(position)]?.find { it.from == OPPOSITES[direction] }!!
            if (!lightTrace.addIfNotExist(position, path)) return
            path.to.map {
                lightStep(lightTrace, position.move(it), it)
            }
        }

        fun countEnergised(entryPosition: Position, entryDirection: Direction): Int {
            var lightTrace = LightTrace(width, height)
            lightStep(lightTrace, entryPosition, entryDirection)
            return lightTrace.sum()
        }

        private fun entryPoints(): List<Pair<Position, Direction>> {
            return listOf((0..<width).map {
                listOf(
                    Pair(Position(0, it), Direction.DOWN),
                    Pair(Position(height - 1, it), Direction.UP)
                )
            }.flatten(),
                (0..<height).map {
                    listOf(
                        Pair(Position(it, 0), Direction.RIGHT),
                        Pair(Position(it, width - 1), Direction.LEFT),
                    )
                }.flatten()).flatten()
        }

        fun maxEnergised(): Int {
            return entryPoints().maxOfOrNull { this.countEnergised(it.first, it.second) }!!
        }
    }

    LavaProductionFacility(readInput("day16/test1"))
        .countEnergised(Position(0,0), Direction.RIGHT)
        .assert(46)

    "Part 1:".println()
    LavaProductionFacility(readInput("day16/input"))
        .countEnergised(Position(0,0), Direction.RIGHT)
        .assert(8901).println()

    LavaProductionFacility(readInput("day16/test1"))
        .maxEnergised()
        .assert(51)

    "Part 2:".println()
    LavaProductionFacility(readInput("day16/input"))
        .maxEnergised()
        .assert(9064)
        .println()
}





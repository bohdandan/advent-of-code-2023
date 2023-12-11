package day10

import println
import readInput
import java.lang.Exception

enum class Directions(val vector: Position) {
    UP(Position(-1, 0)),
    DOWN(Position(1, 0)),
    LEFT(Position(0, -1)),
    RIGHT(Position(0, 1));
}

class Route(val from: Directions, val to: Directions, val region1: List<Directions>, val region2: List<Directions>)
enum class TileType(val char: Char, val routes: List<Route>) {
    VERTICAL('|', listOf(
            Route(Directions.UP, Directions.DOWN, listOf(Directions.RIGHT), listOf(Directions.LEFT)),
            Route(Directions.DOWN, Directions.UP, listOf(Directions.LEFT), listOf(Directions.RIGHT))
        )),
    HORIZONTAL('-', listOf(
            Route(Directions.LEFT, Directions.RIGHT, listOf(Directions.UP), listOf(Directions.DOWN)),
            Route(Directions.RIGHT, Directions.LEFT, listOf(Directions.DOWN), listOf(Directions.UP))
        )),
    L_SHAPE('L', listOf(
            Route(Directions.UP, Directions.RIGHT, listOf(), listOf(Directions.LEFT, Directions.DOWN)),
            Route(Directions.RIGHT, Directions.UP, listOf(Directions.LEFT, Directions.DOWN), listOf()),
        )),
    J_SHAPE('J', listOf(
            Route(Directions.UP, Directions.LEFT, listOf(Directions.RIGHT, Directions.DOWN), listOf()),
            Route(Directions.LEFT, Directions.UP, listOf(), listOf(Directions.RIGHT, Directions.DOWN)),
        )),
    SEVEN_SHAPE('7', listOf(
            Route(Directions.LEFT, Directions.DOWN, listOf(Directions.UP, Directions.RIGHT), listOf()),
            Route(Directions.DOWN, Directions.LEFT, listOf(), listOf(Directions.UP, Directions.RIGHT)),
        )),
    F_SHAPE('F', listOf(
        Route(Directions.DOWN, Directions.RIGHT, listOf(Directions.LEFT, Directions.UP), listOf()),
        Route(Directions.RIGHT, Directions.DOWN, listOf(), listOf(Directions.LEFT, Directions.UP)),
    )),
    EMPTY('.', listOf()),
    START('S', listOf()),
    REGION1('1', listOf()),
    REGION2('2', listOf()),
}
class Position(val row: Int, val column: Int) {
    fun move(vector: Position): Position {
        return Position(row + vector.row,column + vector.column)
    }

    fun move(to: Directions): Position {
        return move(to.vector)
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        return row == other.row && column == other.column
    }
}
class Tile(val position: Position, val type: TileType)
class TileWithRoute(val position: Position, val type: TileType, val route: Route)
fun main() {
    class Puzzle(val map: List<CharArray>) {

        val OPPOSITES = mapOf(
            Directions.UP to Directions.DOWN,
            Directions.DOWN to Directions.UP,
            Directions.LEFT to Directions.RIGHT,
            Directions.RIGHT to Directions.LEFT
        )
        private fun findStartPosition(): Position {
            for (rowNumber in map.indices) {
                for (columnNumber in map[rowNumber].indices) {
                    if (map[rowNumber][columnNumber] == TileType.START.char) {
                        return Position(rowNumber, columnNumber)
                    }
                }
            }
            throw Exception("Can't find initial position")
        }
        fun getTile(map: List<CharArray>, position: Position): Tile? {
            if (position.row > map.lastIndex || position.row < 0) return null
            if (position.column > map[position.row].lastIndex || position.column < 0) return null

            var type = TileType.values().find { it.char == map[position.row][position.column]}
            if (type == null) {
                return null
            } else {
                return Tile(position, type)
            }
        }
        fun getTile(position: Position): Tile? {
            return getTile(map, position)
        }
        fun findStartingDirection(startingPosition: Position): Directions? {
            for (opposite in OPPOSITES.entries) {
                var tile = getTile(startingPosition.move(opposite.key)) ?: continue
                if(tile.type.routes.any{ it.from == opposite.value }) {
                    return opposite.key
                }
            }
            throw Exception("Can't find initial direction")
        }
        private fun calculateLoop(): List<TileWithRoute> {
            var start = findStartPosition()
            var startDirection = findStartingDirection(start)

            var currentPosition = start
            var currentDirection: Directions? = startDirection
            val loopTiles = emptyList<TileWithRoute>().toMutableList()
            do {
                var tile = getTile(currentPosition.move(currentDirection!!))!!
                if (tile.type != TileType.START) {
                    currentPosition = tile.position
                    var route = tile.type.routes.find { it.from == OPPOSITES[currentDirection] }!!
                    loopTiles += TileWithRoute(tile.position, tile.type, route)
                    currentDirection = route.to
                }
            } while (tile.type != TileType.START)

            var startTileType = TileType.values().find {type ->
                type.routes.any{it.from == startDirection && it.to == OPPOSITES[currentDirection]}
            }!!

            loopTiles += TileWithRoute(start, startTileType, startTileType.routes.find { it.from == startDirection}!!)

            return loopTiles
        }
        fun getDistanceToFarthestPoint(): Int {
            return calculateLoop().size / 2
        }
        fun getSizeOfAreaEnclosedByTheLoop(): Int {
            var loopTiles = calculateLoop()
            var loopMap = map.map { str ->
                ".".repeat(str.size).toCharArray()
            }

            loopTiles.forEach {
                loopMap[it.position.row][it.position.column] = it.type.char
            }

            loopMap.forEach { String(it).println() }
            var uncoloredTotal = loopMap.sumOf { row -> row.count { it == TileType.EMPTY.char } }
            uncoloredTotal.println()
            do {
                for (rowNumber in loopMap.indices) {
                    for (columnNumber in loopMap[rowNumber].indices) {
                        if (loopMap[rowNumber][columnNumber] != TileType.EMPTY.char) continue
                        var position = Position(rowNumber, columnNumber)
                        for (direction in OPPOSITES.entries) {
                            var neighbourPosition = position.move(direction.key)
                            var neighbourTile = getTile(loopMap, neighbourPosition) ?: continue

                            if (neighbourTile.type in listOf(TileType.REGION1, TileType.REGION2)) {
                                loopMap[rowNumber][columnNumber] = neighbourTile.type.char
                                break
                            }
                            if (neighbourTile.type.routes.isNotEmpty()) {
                                var tileWithRoute = loopTiles.find { it.position == neighbourPosition}!!
                                if (tileWithRoute.route.region1.contains(direction.value)) {
                                    loopMap[rowNumber][columnNumber] = TileType.REGION1.char
                                } else if (tileWithRoute.route.region2.contains(direction.value)) {
                                    loopMap[rowNumber][columnNumber] = TileType.REGION2.char
                                }
                            }
                        }
                    }
                }
                loopMap.forEach { String(it).println() }
                uncoloredTotal = loopMap.sumOf { row -> row.count { it == TileType.EMPTY.char } }
                uncoloredTotal.println()
            } while (uncoloredTotal > 0)

            var isRegion1 = false;
            for (tile in loopTiles) {
                if (tile.position.row == 0) {
                    if (tile.route.region1.contains(Directions.UP)) {
                        isRegion1 = false
                        break
                    }
                    if (tile.route.region2.contains(Directions.UP)) {
                        isRegion1 = true
                        break
                    }
                }
            }

            var charToFind = if (isRegion1) TileType.REGION1.char else TileType.REGION2.char
            charToFind.println()
            var result = loopMap.sumOf { row -> row.count { it == charToFind } }
            result.println()

            return result
        }
    }

    var testPuzzle1 = Puzzle(readInput("day10/test1").map { it.toCharArray() })
    check(testPuzzle1.getDistanceToFarthestPoint() == 8)

    var puzzle = Puzzle(readInput("day10/input").map { it.toCharArray() })
    puzzle.getDistanceToFarthestPoint().println()

    var testPuzzle2 = Puzzle(readInput("day10/test2").map { it.toCharArray() })
    check(testPuzzle2.getSizeOfAreaEnclosedByTheLoop() == 8)

    var testPuzzle3 = Puzzle(readInput("day10/test3").map { it.toCharArray() })
    check(testPuzzle3.getSizeOfAreaEnclosedByTheLoop() == 10)

    puzzle.getSizeOfAreaEnclosedByTheLoop().println()
}
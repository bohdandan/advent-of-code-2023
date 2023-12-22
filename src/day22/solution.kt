package day22

import assert
import println
import readInput
import kotlin.math.min

typealias Point = Triple<Int, Int, Int>
operator fun Point.plus(other: Point) = Point(first + other.first, second + other.second, third + other.third)

val DOWN = Point(0,0,-1)
data class Brick(val index: Int, var point1: Point, var point2: Point) {
    var occupiedBricks: List<Point> = calculateOccupiedBricks()

    private fun calculateOccupiedBricks(): List<Point> {
        return (point1.first..point2.first).map { x ->
            (point1.second..point2.second).map { y ->
                (point1.third..point2.third).map { z ->
                    Triple(x, y, z)
                }
            }.flatten()
        }.flatten()
    }

    fun copy() = Brick(index, point1.copy(), point2.copy())
    fun move(vector: Point) {
        point1 += vector
        point2 += vector
        occupiedBricks = calculateOccupiedBricks()
    }
}

class SandSlabs(input: List<String>) {
    val bricks = input.mapIndexed{ index, row ->
        val (point1, point2) = row.split("~")
        val (x1, y1, z1) = point1.split(",").map{it.toInt()}
        val (x2, y2, z2) = point2.split(",").map{it.toInt()}

        Brick(index, Point(x1,y1, z1), Point(x2,y2, z2))
    }

    fun moveDown(bricks: List<Brick>): Int {
        val sortedBricks = bricks.sortedBy { min(it.point1.third, it.point2.third) }
        var moves = 0

        for (brick in sortedBricks) {
            val lowerLevel = brick.occupiedBricks.map { it + DOWN }
            if (lowerLevel.any{it.third < 1}) continue
            if (!bricks.filter { it != brick}.map { it.occupiedBricks }.flatten().any{lowerLevel.contains(it)}) {
                brick.move(DOWN)
                moves++
            }
        }

        return moves
    }

    fun init() {
        var moves = 1
        var iteration = 0
        while (moves > 0) {
            moves = moveDown(bricks)
            iteration++
            "$iteration -> $moves moves".println()
        }
    }

    fun calculateRemovable(): Int {
        init()
        var result = 0
        bricks.indices.forEach { it ->
            val tmpBricks = bricks.map(Brick::copy).toMutableList()
            tmpBricks.removeAt(it)
            val moves = moveDown(tmpBricks)
            if (moves == 0) {
                result++
                "Brick ${bricks[it].index} is safely removable".println()
            }
        }

        return result
    }

    fun calculateMaxNumberOfFall(): Int {
        init()
        var result = 0
        bricks.indices.forEach { it ->
            val tmpBricks = bricks.map(Brick::copy).toMutableList()
            tmpBricks.removeAt(it)
            val moves = moveDown(tmpBricks)
            if (moves > 0) {
                result += moves
                "Brick ${bricks[it].index} is not removable: $moves".println()
            }
        }

        return result
    }
}
fun main() {
    SandSlabs(readInput("day22/test1"))
        .calculateRemovable()
        .assert(5)
        .println()

    "Part 1:".println()
    SandSlabs(readInput("day22/test1"))
        .calculateMaxNumberOfFall()
        .assert(7)
        .println()


    "Part 2:".println()
    SandSlabs(readInput("day22/input"))
        .calculateMaxNumberOfFall()
        .assert(80948)
        .println()
}
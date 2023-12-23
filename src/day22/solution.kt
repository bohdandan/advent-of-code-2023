package day22

import assert
import println
import readInput
import java.util.*

data class Brick(val index: Int, var x: IntRange, var y: IntRange, var z: IntRange) {
    val top by lazy { copy(z = z.last..z.last) }
    val bottom by lazy { copy(z = z.first..z.first) }
    val holding = mutableListOf<Brick>()
    val standingOn = mutableListOf<Brick>()
    fun intersects(brick: Brick) =
        x.intersect(brick.x).isNotEmpty() &&
            y.intersect(brick.y).isNotEmpty() &&
            z.intersect(brick.z).isNotEmpty()

    fun moveDown(steps: Int = 1): Brick = copy(z = (z.first - steps)..(z.last - steps))
}

class SandSlabs(input: List<String>) {
    private var bricks = input.mapIndexed{ index, row ->
        val (point1, point2) = row.split("~")
        val (x1, y1, z1) = point1.split(",").map{it.toInt()}
        val (x2, y2, z2) = point2.split(",").map{it.toInt()}

        Brick(index, x1..x2,y1..y2, z1..z2)
    }.sortedWith(compareBy { it.z.first })

    init {
        fun settle(settledBricks: MutableList<Brick>, brick: Brick): MutableList<Brick> {
            var holdingBricks = listOf<Brick>()
            var bottom = brick.bottom

            while (bottom.z.first != 0 && holdingBricks.isEmpty()) {
                bottom = bottom.moveDown()
                holdingBricks = settledBricks.filter { it.top.intersects(bottom) }
            }
            val settledBrick = brick.moveDown(brick.z.first - bottom.z.first - 1)

            holdingBricks.forEach {
                it.holding += settledBrick
                settledBrick.standingOn += it
            }

            settledBricks += settledBrick
            return settledBricks
        }

        bricks = bricks.fold(mutableListOf(), ::settle).sortedWith(compareBy { it.z.first })
    }

    fun calculateSafelyRemovable(): Int {
        return bricks.count { brick -> brick.holding.isEmpty() || brick.holding.all { it.standingOn.size > 1 } }
    }

    fun calculateMaxNumberOfFall(): Int {
        fun countMovedBricksIfDisintegrated(brick: Brick): Int {
            val movedBricks = mutableSetOf(brick.index)
            val queue: Queue<Brick> = LinkedList()
            brick.holding.forEach(queue::offer)
            while (queue.isNotEmpty()) {
                val brickToCheck = queue.poll()
                if (brickToCheck.standingOn.all { movedBricks.contains(it.index) }) {
                    movedBricks.add(brickToCheck.index)
                    brickToCheck.holding.forEach(queue::offer)
                }
            }
            movedBricks.remove(brick.index)
            return movedBricks.size
        }
        
        return bricks.map(::countMovedBricksIfDisintegrated).sum()
    }
}
fun main() {
    SandSlabs(readInput("day22/test1"))
        .calculateSafelyRemovable()
        .assert(5)

    "Part 1:".println()
    SandSlabs(readInput("day22/input"))
        .calculateSafelyRemovable()
        .assert(501)
        .println()

    SandSlabs(readInput("day22/test1"))
        .calculateMaxNumberOfFall()
        .assert(7)

    "Part 2:".println()
    SandSlabs(readInput("day22/input"))
        .calculateMaxNumberOfFall()
        .assert(80948)
        .println()
}
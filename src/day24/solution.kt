package day24

import assert
import println
import readInput
import kotlin.math.*

data class Velocity(val x: Long, val y: Long, val z: Long)
data class Hailstone(val x: Long, val y: Long, val z: Long, val velocity: Velocity) {

}
class NeverTellMeTheOdds(input: List<String>) {
    private val hailstones = input.map { row ->
        val (point1, point2) = row.split("@")
        val (x1, y1, z1) = point1.split(",").map{it.trim().toLong()}
        val (x2, y2, z2) = point2.split(",").map{it.trim().toLong()}

        Hailstone(x1,y1,z1, Velocity(x2, y2, z2))
    }

    private fun toABC(hailstone: Hailstone): Triple<Double, Double, Double> {
        val a = ((hailstone.y + hailstone.velocity.y) - hailstone.y).toDouble() / ((hailstone.x + hailstone.velocity.x) - hailstone.x)
        val c = hailstone.y - hailstone.x.toDouble() * ((hailstone.y + hailstone.velocity.y) - hailstone.y) / ((hailstone.x + hailstone.velocity.x) - hailstone.x)
        return Triple(a, -1.0, c)
    }

    private fun collisionPoint(hailstone1: Hailstone, hailstone2: Hailstone): Pair<Double, Double> {
        val (a1, b1, c1) = toABC(hailstone1)
        val (a2, b2, c2) = toABC(hailstone2)

        val x = (b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1)
        val y = (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1)

        val t = min((x - hailstone1.x) / hailstone1.velocity.x, (x - hailstone2.x) / hailstone2.velocity.x)
        if (t < 0) return Pair(0.0, 0.0)

        return Pair(x, y)
    }

    fun countIntersections(area: LongRange = 7L..27L): Int {
        val collisions = hailstones.mapIndexed { index, h1 ->
            val collisions = mutableListOf<Pair<Double, Double>>()
            for (h2 in hailstones.listIterator(index + 1)) {
                collisions += collisionPoint(h1, h2)
            }
            collisions
        }.flatten()
        return collisions.count {
            it.first > area.first && it.first < area.last &&
            it.second > area.first && it.second < area.last
        }
    }

    // Paste results in SageMath 😝
    // https://sagecell.sagemath.org/
    fun getEquation(): String {
        var equationCounter = 1
        return buildString {
            appendLine("var('x y z vx vy vz t1 t2 t3 ans')")
            hailstones.take(3).forEachIndexed { index, h ->
                appendLine("eq${equationCounter++} = x + (vx * t${index + 1}) == ${h.x} + (${h.velocity.x} * t${index + 1})")
                appendLine("eq${equationCounter++} = y + (vy * t${index + 1}) == ${h.y} + (${h.velocity.y} * t${index + 1})")
                appendLine("eq${equationCounter++} = z + (vz * t${index + 1}) == ${h.z} + (${h.velocity.z} * t${index + 1})")
            }
            appendLine("eq10 = ans == x + y + z")
            appendLine("print(solve([eq1,eq2,eq3,eq4,eq5,eq6,eq7,eq8,eq9,eq10],x,y,z,vx,vy,vz,t1,t2,t3,ans))")
        }
    }
}


fun main() {
    NeverTellMeTheOdds(readInput("day24/test1"))
        .countIntersections()
        .assert(2)

    "Part 1:".println()
    NeverTellMeTheOdds(readInput("day24/input"))
        .countIntersections(200000000000000..400000000000000)
        .assert(27732)
        .println()

    "Part 2:".println()
    "-".repeat(100).println()
    NeverTellMeTheOdds(readInput("day24/input"))
        .getEquation()
        .println()
    "-".repeat(100).println()

//    641619849766168
}
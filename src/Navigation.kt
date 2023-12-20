enum class Direction(val position: Position) {
    UP(-1 to 0),
    DOWN(1 to 0),
    LEFT(0 to -1),
    RIGHT(0 to 1);

    val opposite by lazy {
        when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }
}

typealias Position = Pair<Int, Int>
operator fun Position.plus(other: Position) = first + other.first to second + other.second
operator fun Position.minus(other: Position) = first - other.first to second - other.second
operator fun Position.times(amount: Int) = first * amount to second * amount
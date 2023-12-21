import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <R> Any?.assert(expected: R): R {
    if (this != expected) {
        "Assertion failed: Expected $expected but found $this".println()
    }
    return this as R
}
fun Long.gcd(other: Long): Long {
    fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a else gcd(b, a % b)
    }
    return gcd(this, other)
}

fun Long.lcm(other: Long): Long {
    return if (this == 0L || other == 0L) 0 else Math.abs(this * other) / this.gcd(other)
}
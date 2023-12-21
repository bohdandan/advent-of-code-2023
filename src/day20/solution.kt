package day20

import assert
import lcm
import println
import readInput
import java.util.*

enum class ModuleType{
    Broadcaster, FlipFlop, Conjunction
}

abstract class Module {
    abstract val name: String
    abstract val targets: List<String>
    abstract fun process(from: String, signal: Int): Int
    abstract fun type(): ModuleType
}
class FlipFlop(override val name: String, override val targets: List<String>, var isOn: Boolean = false): Module() {
    override fun process(from: String, signal: Int): Int {
        if (signal == 1) return -1
        return if(isOn) {
            isOn = false
            0
        } else {
            isOn = true
            1
        }
    }
    override fun type(): ModuleType {
        return ModuleType.FlipFlop
    }
}
class Conjunction(override val name: String, override val targets: List<String>): Module() {
    private var lastInputs = mutableMapOf<String, Int>()
    override fun process(from: String, signal: Int): Int {
        lastInputs[from] = signal
        return if (allSet()) {
            0
        } else {
            1
        }
    }

    private fun allSet() = lastInputs.values.all { it == 1 }
    fun initialiseMemory(inputs: List<String>) {
        lastInputs = inputs.associateWith { 0 }.toMutableMap()
    }
    override fun type(): ModuleType {
        return ModuleType.Conjunction
    }
}
class Broadcaster(override val name: String, override val targets: List<String>): Module() {
    override fun process(from: String, signal: Int): Int {
        return signal
    }

    override fun type(): ModuleType {
        return ModuleType.Broadcaster
    }
}
data class Signal(val from: String, val to: String, val signal: Int)

class PulsePropagation(inputs: List<String>) {
    private var modules = listOf<Module>()

    init {
        modules = inputs.map {line ->
            val result: Module
            val targets = line.split("->")[1]
                .split(",")
                .map { it.trim() }
                .toList()
            result = if (line.startsWith("broadcaster")) {
                Broadcaster("broadcaster", targets)
            } else {
                val name = line.split("->")[0].drop(1).trim()
                if (line.startsWith("%")) {
                    FlipFlop(name, targets)
                } else {
                    Conjunction(name, targets)
                }
            }
            result
        }

        modules.filter { it.type() == ModuleType.Conjunction }
            .forEach {module ->
                val sourceNames = modules.filter { it.targets.contains(module.name)}
                    .map { it.name}.toList()
                (module as Conjunction).initialiseMemory(sourceNames)
        }
    }

    private fun simulate(): List<Signal> {
        val queue: Queue<Signal> = LinkedList()
        val initial = Signal("", "broadcaster", 0)
        queue.offer(initial)
        val result = mutableListOf(initial)
        while (queue.isNotEmpty()) {
            val signal = queue.poll()
            modules.find { it.name == signal.to }?.let { module ->
                val output = module.process(signal.from, signal.signal)
                if (output > -1) {
                    module.targets.forEach {
                        val newSignal = Signal(module.name, it, output)
                        result += newSignal
                        queue.offer(newSignal)
                    }
                }
            }
        }

        return result
    }

    fun run1000Simulations(): Long {
        return generateSequence {
                simulate()
            }.take(1000)
            .fold(0L to 0L) { acc, signals ->
                (acc.first + signals.count { it.signal == 0 }) to (acc.second + signals.count { it.signal == 1 })
            }.let {  it.first * it.second }
    }
    fun minNumberPressedToEnableRx(): Long {
        val rxParent = modules.find { it.targets.contains("rx") }?.name
        val rxGrandParents = modules.filter { it.targets.contains(rxParent) }
        val flips = rxGrandParents.map { it.name }.associateWith { 0L }.toMutableMap()
        var buttonsPressed = 1L
        while(flips.values.any { it == 0L }){
            simulate()
                .filter { it.to == rxParent }
                .filter { it.signal == 1 }
                .filter { flips[it.from] == 0L }
                .forEach{
                    flips[it.from] = buttonsPressed
                }
            buttonsPressed ++
        }

        return flips.values.reduce(Long::lcm)
    }
}

fun main() {
    PulsePropagation(readInput("day20/test1"))
        .run1000Simulations()
        .assert(32000000L)

    PulsePropagation(readInput("day20/test2"))
        .run1000Simulations()
        .assert(11687500L)

    "Part 1:".println()
    PulsePropagation(readInput("day20/input"))
        .run1000Simulations()
        .assert(944750144L)
        .println()

    "Part 2:".println()
    PulsePropagation(readInput("day20/input"))
        .minNumberPressedToEnableRx()
        .assert(222718819437131L)
        .println()

}
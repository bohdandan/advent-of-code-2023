package day25

import println
import readInput

class Snowverload(input: List<String>) {
    private val graph = buildMap {
        input.forEach {row ->
            val (key, values) = row.split(':')
            put(key.trim(), values.split(' ').map(String::trim).filter { it.isNotEmpty() })
        }
    }
    private val nodes = buildSet {
        addAll(graph.values.flatten())
        addAll(graph.keys)
    }
    private val connections = buildSet {
        graph.forEach {left ->
            addAll(left.value.map { setOf(left.key, it) })
        }
    }
    private fun partsOfGraph(connections: Set<Set<String>>): List<Set<String>> {
        val seenNodes = mutableSetOf<String>()
        fun findConnected(node: String, seen: MutableSet<String> = mutableSetOf()): Set<String> {
            seen += node
            val newConnections = connections
                .filter { it.contains(node) }
                .flatten()
                .filter {!seen.contains(it)}
                .toSet()
            seen.addAll(newConnections.map { findConnected(it, seen) }.flatten())
            return seen
        }

        val result = mutableListOf<Set<String>>()
        do {
            result += findConnected(nodes.first { !seenNodes.contains(it) })
            seenNodes.addAll(result.last())
        } while (seenNodes.size != nodes.size)

        return result
    }
    fun solve(): Int {
        val nodesToRemove = setOf(setOf("hvm", "grd"), setOf("pmn", "kdc"), setOf("zfk", "jmn"))
        val connectionsToTest = connections.toMutableSet()
        connectionsToTest.removeAll(nodesToRemove)

        val subGraphs = partsOfGraph(connectionsToTest)
        return if (subGraphs.size == 2)
            subGraphs[0].size * subGraphs[1].size
            else
                0
    }

    // Visualise with
    // brew install graphviz
    // dot -Tsvg -Kneato graph.dot > graph.svg
    fun printGraph(): String{
        return buildString {
            appendLine("strict graph {")
            connections.forEach {
                val list = it.toList()
                appendLine("${list[0]} -- ${list[1]}")
            }
            appendLine("}")
        }
    }
}
fun main() {
    "Part 1:".println()
    Snowverload(readInput("day25/input"))
        .printGraph()
        .println()

    Snowverload(readInput("day25/input"))
        .solve()
        .println()
}
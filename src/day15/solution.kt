package day15

import assert
import println
import readInput

fun main() {

    class Command(val label: String, val command: Char, val focalLength: Int)
    class Mirror(val label: String, val focalLength: Int)
    class Box(val number: Int, val mirrors: MutableList<Mirror>) {
        fun focusingPower(): Int {
            return mirrors.mapIndexed { index, mirror ->
                (this.number + 1) * (index + 1) * mirror.focalLength
            }.sum()
        }
    }

    fun String.hash(): Int {
        return this.fold(0) { acc, char ->
            (char.code + acc) * 17 % 256
        }
    }
    fun splitToBlocks(input: String): List<String> {
        return input.split(",")
    }

    fun parseCommand(input: String): Command {
        val matchResult = Regex("^([a-zA-Z]+)([=-])(\\d*)\$").matchEntire(input)!!
        val (label, command, focalLength) = matchResult.destructured
        return Command(label, command.first(), focalLength.ifBlank{"0"}.toInt())
    }

    fun processCommands(boxes: Array<Box>, command: Command) : Array<Box> {
        val box = boxes[command.label.hash()]
        val sameLabelMirror = box.mirrors.indexOfFirst { it.label == command.label }
        when(command.command) {
            '=' -> {
                if (sameLabelMirror >= 0) {
                    box.mirrors[sameLabelMirror] = Mirror(command.label, command.focalLength)
                } else {
                    box.mirrors.add(Mirror(command.label, command.focalLength))
                }
            }
            else -> {
                if (sameLabelMirror >= 0) {
                    box.mirrors.removeAt(sameLabelMirror)
                }
            }
        }

        return boxes
    }

    "HASH".hash().assert(52)
    splitToBlocks(readInput("day15/test1")[0])
        .sumOf { it.hash() }
        .assert(1320)
    "Part 1:".println()
    splitToBlocks(readInput("day15/input")[0])
        .sumOf { it.hash() }
        .assert(510388)
        .println()

    fun emptyBoxes(): Array<Box> {
        return Array(256) {Box(it, mutableListOf())}
    }
    splitToBlocks(readInput("day15/test1")[0])
        .map(::parseCommand)
        .fold(emptyBoxes(), ::processCommands)
        .sumOf { it.focusingPower() }
        .assert(145)
    "Part 2:".println()
    splitToBlocks(readInput("day15/input")[0])
        .map(::parseCommand)
        .fold(emptyBoxes(), ::processCommands)
        .sumOf { it.focusingPower() }
        .assert(291774)
        .println()
}






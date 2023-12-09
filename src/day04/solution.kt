package day04

import println
import readInput

fun main() {
    class Card (val id: Int, val winningNumbers: Set<Int>, val numbersYouHave: Set<Int>, var numberOfCopies: Int = 1) {
        fun matches(): Int {
            val overlap = winningNumbers.intersect(numbersYouHave)
            return overlap.size
        }
    }
    class CardStack {
        var cards: List<Card> = emptyList();
        constructor(input: List<String>) {
            cards = input.stream().map{parseCard(it)}.toList()
        }

        fun splitIntoNumbers(input: String): Set<Int> {
            return input.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        }
        fun parseCard(input: String): Card {
            val cardPattern = Regex("Card\\s*(\\d+): (.*)")
            val match = cardPattern.findAll(input).first()
            val cardId = match.groupValues[1].toInt()
            val numberGroups = match.groupValues[2].split("|")
            var winningNumbers = splitIntoNumbers(numberGroups[0])
            var numbersYouHave = splitIntoNumbers(numberGroups[1])
            return Card(cardId, winningNumbers, numbersYouHave)
        }
        fun winningPointLogic1(): Int {
            return cards.map { it.matches() }
                .filter { it > 0 }
                .map {
                    Math.pow(2.0, (it - 1).toDouble()).toInt()
                }.sum()
        }

        fun numberOfCardsLogic2(): Int {
            cards.forEach { card ->
                val matches = card.matches()
                if (matches > 0) {
                    var rangeToDuplicate = card.id + 1 .. card.id + matches
                    cards.filter { rangeToDuplicate.contains(it.id) }
                        .forEach{
                            it.numberOfCopies += card.numberOfCopies
                        }
                }
            }
            return cards.sumOf { it.numberOfCopies }
        }
    }


    val testCardStack = CardStack(readInput("day04/test1"))
    check(testCardStack.winningPointLogic1() == 13)
    check(testCardStack.numberOfCardsLogic2() == 30)

    val cardStack = CardStack(readInput("day04/input"))
    cardStack.winningPointLogic1().println()
    cardStack.numberOfCardsLogic2().println()
}

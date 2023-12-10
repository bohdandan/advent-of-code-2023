package day07

import println
import readInput
import kotlin.math.pow

enum class HandType(val weight: Int, val sequencePattern: List<Int>) {
    FIVE_OF_A_KIND(7, listOf(5)),
    FOUR_OF_A_KIND(6, listOf(4, 1)),
    FULL_HOUSE(5, listOf(3, 2)),
    THREE_OF_A_KIND(4, listOf(3, 1, 1)),
    TWO_PAIR(3, listOf(2, 2, 1)),
    ONE_PAIR(2, listOf(2, 1, 1, 1)),
    HIGH_CARD(1, listOf(1 ,1, 1, 1, 1))
}
fun main() {
    class CardRules(val cardWeights: String, val withJoker: Boolean,
                    val numberOfCards: Int = 5,
                    val joker: Char = 'J'
    )
    class Hand {
        var cards: String = ""
        var bid: Int = 0
        var weight: Long = 0
        constructor(cards: String, bid: Int, cardRules: CardRules) {
            this.cards = cards
            this.bid = bid
            this.weight = getWeight(cards, cardRules)
            kotlin.io.println(cards + " -> "+ weight.toString(16))
        }
        fun getHandType(cardRules: CardRules): HandType {
            val cardsCount = mutableMapOf<Char, Int>()
            cards.forEach {
                cardsCount[it] = cardsCount.getOrDefault(it, 0) + 1
            }

            var handSequencePattern = cardsCount.values.sortedDescending()
            if (cardRules.withJoker) {
                var numberOfJokers = cardsCount.getOrDefault(cardRules.joker, 0)
                if (numberOfJokers > 0 && numberOfJokers != cardRules.numberOfCards) {
                    var tmp = cardsCount.filter { it.key != cardRules.joker }.values
                        .sortedDescending()
                        .toMutableList()
                    tmp[0] += numberOfJokers
                    handSequencePattern = tmp
                }
            }

            return HandType.values().find { it.sequencePattern == handSequencePattern }!!
        }
        fun getWeight(cards: String, cardRules: CardRules): Long {
            var result = 0L
            cards.forEachIndexed {index, char ->
                var cardWeight = cardRules.cardWeights.indexOf(char)
                result += (cardWeight * 16.0.pow(cardRules.numberOfCards - 1 - index.toDouble())).toLong()
            }
            var handType = getHandType(cardRules)
            return result + (handType.weight * 16.0.pow(cardRules.numberOfCards)).toLong()
        }
    }
    class CamelCardsGame {
        var hands: List<Hand> = emptyList()
        constructor(input: List<String>, cardRules: CardRules) {
            for (row in input) {
                var handDetails = row.split(" ");
                hands += Hand(handDetails[0], handDetails[1].trim().toInt(), cardRules)
            }
        }

        fun getTotalScore(): Long {
            var sortedHands = hands.sortedBy { it.weight }
            var result = 0L;
            sortedHands.forEachIndexed {index, hand ->
                result += (index + 1) * hand.bid
            }

            return result
        }
    }


    val ruleSet1 = CardRules("23456789TJQKA", false)
    val ruleSet2 = CardRules("J23456789TQKA", true)

    var testGameEngine = CamelCardsGame(readInput("day07/test1"), ruleSet1)
    check(testGameEngine.getTotalScore() == 6440L)

    val gameEngine = CamelCardsGame(readInput("day07/input"), ruleSet1)
    gameEngine.getTotalScore().println()

    var testGameEngine2 = CamelCardsGame(readInput("day07/test1"), ruleSet2)
    check(testGameEngine2.getTotalScore() == 5905L)

    val gameEngine2 = CamelCardsGame(readInput("day07/input"), ruleSet2)
    gameEngine2.getTotalScore().println()
}
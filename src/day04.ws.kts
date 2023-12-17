import java.io.File
import kotlin.math.pow

val basePath = "C:\\Users\\ak\\dev\\aoc2023\\input"

val input =
        File("$basePath\\day04")
                .readLines()

val testInput = """
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
""".trimIndent()
        .split("\n")

data class Scratchcard(val id: Int, val winners: Set<Int>, val haves: Set<Int>) {
    val countWins = winners.intersect(haves).size
    val score = calculateScore()

    val copies = if (countWins == 0) {
        emptyList()
    } else {
        (id + 1) .. (id + countWins)
    }

    companion object {
        fun parse(line: String): Scratchcard {
            val colonSplit = line.split(": ")
            val id = colonSplit[0].split(" ").last.toInt()

            val pipeSplit = colonSplit[1].split(" | ")

            val winners = pipeSplit[0].let { parseNumbers(it) }
            val haves = pipeSplit[1].let { parseNumbers(it) }

            return Scratchcard(id, winners, haves)
        }

        private fun parseNumbers(part: String): Set<Int> {
            return part
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }
                    .toSet()
        }
    }

    fun calculateScore(): Int {
        return countWins
                .let { countOfMatches ->
                    Math.pow(2.0, (countOfMatches - 1).toDouble()).toInt()
                }
    }
}

fun day04p1(lines: List<String>): Int {
    val scratchcards = lines.map { line ->
        Scratchcard.parse(line)
    }

    val scoreSum = scratchcards
            .map { scratchcard ->
                val score = scratchcard.score
//                println("id: ${scratchcard.id}; score: $score")

                score
            }
            .sum()

    return scoreSum
}


println(day04p1(testInput))
println(day04p1(input))

// replacement strategy
// replace and increment counter
//1 -> 2,3,4,5
//2 -> 3,4
//3 -> 4
//4 -> 5
//5 ->
//6 ->
//
//1, 2, 3, 4, 5, 6
//3,4,3,4,5, 3,4, 3, 4, 5, 6
//
// 1 instance  of card 1
// 2 instances of card 2
// 4 instances of card 3
// 8 instances of card 4
//14 instances of card 5
// 1 instance  of card 6

fun day04p2(lines: List<String>): Int {
    val scratchcards = lines.map { line ->
        Scratchcard.parse(line)
    }

    var countScratchCards = 0

    val replacements = scratchcards
            .associate { card ->
                card.id to card.copies
            }
    val deck = scratchcards.associate { it.id to 0 }.toMutableMap()

    val scratchcardQueue = scratchcards.map { it.id }.toMutableList()
    while (scratchcardQueue.isNotEmpty()) {
        val id = scratchcardQueue.removeLast()
        deck[id] = deck[id]!! + 1

        scratchcardQueue.addAll(replacements[id]!!)
    }

    println("deck: $deck")
    return deck.values.sum()
}


println(day04p2(testInput))
println(day04p2(input))

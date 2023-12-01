import java.io.File

val basePath = "/home/ak/dev/aoc2023/input/"

val input =
    File("$basePath/day01")
        .readLines()

val testInputP1 = """
    1abc2
    pqr3stu8vwx
    a1b2c3d4e5f
    treb7uchet
""".trimIndent()
    .split("\n")

val testInputP2 = """
    two1nine
    eightwothree
    abcone2threexyz
    xtwone3four
    4nineeightseven2
    zoneight234
    7pqrstsixteen
""".trimIndent()
    .split("\n")

val digitMapP1 = mutableMapOf<String, Int>()
    .also {
        for (i in 0..9) {
            it["$i"] = i
        }
    }

val digitMapP2 =
    digitMapP1 + mapOf(
        "zero" to 0,
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )

fun day01p1(input: List<String>): Int =
    day01(input, digitMapP1)

fun day01p2(input: List<String>): Int =
    day01(input, digitMapP2)

fun day01(input: List<String>, digitMap: Map<String, Int>): Int =
    input
        .asSequence()
        .map { line ->
            val firstToken = digitMap.keys
                .asSequence()
                .map { line.indexOf(it) to it }
                .filter { 0 <= it.first }
                .minBy { it.first }
                .second

            val lastToken = digitMap.keys
                .asSequence()
                .map { line.lastIndexOf(it) to it }
                .filter { 0 <= it.first }
                .maxBy { it.first }
                .second

            digitMap[firstToken]!! * 10 + digitMap[lastToken]!!
        }
        .sum()

day01p1(testInputP1)
day01p1(input)

day01p2(testInputP2)
day01p2(input)

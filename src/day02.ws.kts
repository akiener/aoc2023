import java.io.File

val basePath = "/home/ak/dev/aoc2023/input/"

val input =
    File("$basePath/day02")
        .readLines()

val testInput = """
    Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
    Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
    Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
    Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
    Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
""".trimIndent()
    .split("\n")

enum class Color {
    blue,
    red,
    green,
}

data class Fetch(val count: Int, val color: Color)

val availableColors = mapOf(
    Color.red to 12,
    Color.green to 13,
    Color.blue to 14,
)

fun Map<Color, Int>.isPossible(): Boolean {
    return this.all {
        it.value <= availableColors[it.key]!!
    }
}

fun parseFetch(input: String): Fetch {
    val parts = input.trim().split(" ")
    return Fetch(parts[0].toInt(), Color.valueOf(parts[1]))
}

fun day02p1(input: List<String>): Int {
    val possibleGames = input
        .map { line ->
            val (id, restLine) = line
                .drop(5)
                .split(":")
                .let { it.first().toInt() to it[1] }

            val groups = restLine
                .split("; ")

            val isPossible = groups
                .all { fetch ->
                    fetch
                        .split(", ")
                        .map { parseFetch(it) }
                        .associate { it.color to it.count }
                        .isPossible()
                }

            id to isPossible
        }

    return possibleGames.filter { it.second }.sumOf { it.first }
}

fun day02p2(input: List<String>): Int {
    val requiredPowers = input
        .map { line ->
            val (_, restLine) = line
                .drop(5)
                .split(":")
                .let { it.first().toInt() to it[1] }

            val groups = restLine
                .split("; ")

            val requiredOfColor = groups
                .map { fetch ->
                    fetch
                        .split(", ")
                        .map { parseFetch(it) }
                        .groupBy { it.color }
                        .map { (color, fetch) -> color to fetch.maxBy { it.count }.count }
                        .toMap()
                }
                .flatMap { it.entries }
                .groupBy { it.key }
                .map { it.key to it.value.maxOf { it.value } }
                .toMap()

            requiredOfColor
        }

    return requiredPowers
        .sumOf { it.values.reduce { product, i -> product * i } }
}

day02p1(testInput)
day02p1(input)

day02p2(testInput)
day02p2(input)

import java.io.File
import kotlin.math.pow

val basePath = "/home/ak/dev/aoc2023/input/"

val input =
    File("$basePath/day03")
        .readLines()

val customTestInput = """
    15......2*..
    +.........20
    .5.....23..${'$'}
    8...10*12...
""".trimIndent()
    .split("\n")

val testInput = """
    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...${'$'}.*....
    .664.598..
""".trimIndent()
    .split("\n")

val testInputOnline = """
    12.......*..
    +.........34
    .......-12..
    ..78........
    ..*....60...
    78.........9
    .5.....23..${'$'}
    8...90*12...
    ............
    2.2......12.
    .*.........*
    1.1..503+.56
""".trimIndent()
    .split("\n")

val testInput35 = """
    1........*..
    +.........34
""".trimIndent()
    .split("\n")

val directions = listOf(
    -1 to -1,
    -1 to 0,
    -1 to 1,
    0 to -1,
    0 to 1,
    1 to -1,
    1 to 0,
    1 to 1,
)

data class Coordinate(val line: Int, val column: Int)

operator fun Coordinate.plus(direction: Pair<Int, Int>): Coordinate {
    return Coordinate(this.line + direction.first, this.column + direction.second)
}

fun List<CharArray>.getSafe(line: Int, column: Int): Char {
    if (line <= -1
        || this.size <= line
        || column <= -1
        || this.first().size <= column
    )
        return '.'

    return this[line][column]
}


fun Coordinate.isAdjacentTo(other: Coordinate): Boolean {
    return this.line in other.line - 1..other.line + 1
            && this.column in other.column - 1..other.column + 1
}

fun List<CharArray>.hasAdjacentSymbol(positions: Set<Coordinate>, gears: MutableList<Coordinate>): Boolean {
    val gearAdditions = mutableSetOf<Coordinate>()

    val positionHasAdjacent = positions
        .map { coordinate ->
            val adjacentCoords =
                directions
                    .map { direction ->
                        coordinate + direction
                    }

            val adjacentIsSymbol = adjacentCoords
                .map { (line, column) ->
                    val adjacentChar = this.getSafe(line, column)

//                    if (adjacentChar != '.' && !adjacentChar.isDigit()) {
//                        println("found adjacent char: $adjacentChar to $positions")
//                    }

                    if (adjacentChar == '*') {
                        gearAdditions.add(Coordinate(line, column))
                    }

                    adjacentChar != '.' && !adjacentChar.isDigit()
                }

            adjacentIsSymbol
        }

    gears.addAll(gearAdditions)

    return positionHasAdjacent.flatten().any { it }
}

fun day03p1(input: List<String>): Int {
    val grid = input.map { it.toCharArray() }

    val partNumbersWithAdjacentSymbol = grid
        .mapIndexed { lIndex, line ->
            val partNumbers = mutableListOf<Pair<Set<Coordinate>, Int>>()

            var buffer: Pair<MutableSet<Coordinate>, MutableList<Int>> = mutableSetOf<Coordinate>() to mutableListOf()
            line.forEachIndexed { cIndex, char ->
                if (char.isDigit()) {
                    buffer.first.add(Coordinate(lIndex, cIndex))
                    buffer.second.add(char.digitToInt())
                } else {
                    if (buffer.second.isNotEmpty()) {
                        val number = buffer.second.reversed().reduceIndexed { i, acc, digit ->
                            acc + 10.0.pow(i.toDouble()).toInt() * digit
                        }
                        partNumbers.add(buffer.first.toSet() to number)
                        buffer = mutableSetOf<Coordinate>() to mutableListOf()
                    }
                }

                if (cIndex == grid[0].size - 1) {
                    if (buffer.second.isNotEmpty()) {
                        val number = buffer.second.reversed().reduceIndexed { i, acc, digit ->
                            acc + 10.0.pow(i.toDouble()).toInt() * digit
                        }
                        partNumbers.add(buffer.first.toSet() to number)
                    }
                }
            }

            val partNumbersWithAdjacentSymbol = partNumbers
                .filter { (coordinates, _) ->
                    grid.hasAdjacentSymbol(coordinates, mutableListOf())
                }
                .map { (_, number) -> number }

            partNumbersWithAdjacentSymbol
        }

    return partNumbersWithAdjacentSymbol
        .flatten()
        .sum()
}

fun day03p2(input: List<String>): Int {
    val grid = input.map { it.toCharArray() }
    val gears = mutableListOf<Coordinate>()

    val partNumbersWithAdjacentSymbol = grid
        .mapIndexed { lIndex, line ->
            val partNumbers = mutableListOf<Pair<Set<Coordinate>, Int>>()

            var buffer: Pair<MutableSet<Coordinate>, MutableList<Int>> = mutableSetOf<Coordinate>() to mutableListOf()
            line.forEachIndexed { cIndex, char ->
                if (char.isDigit()) {
                    buffer.first.add(Coordinate(lIndex, cIndex))
                    buffer.second.add(char.digitToInt())
                } else {
                    if (buffer.second.isNotEmpty()) {
                        val number = buffer.second.reversed().reduceIndexed { i, acc, digit ->
                            acc + 10.0.pow(i.toDouble()).toInt() * digit
                        }
                        partNumbers.add(buffer.first.toSet() to number)
                        buffer = mutableSetOf<Coordinate>() to mutableListOf()
                    }
                }

                if (cIndex == grid[0].size - 1) {
                    if (buffer.second.isNotEmpty()) {
                        val number = buffer.second.reversed().reduceIndexed { i, acc, digit ->
                            acc + 10.0.pow(i.toDouble()).toInt() * digit
                        }
                        partNumbers.add(buffer.first.toSet() to number)
                    }
                }
            }

            partNumbers
                .filter { (coordinates, _) ->
                    grid.hasAdjacentSymbol(coordinates, gears)
                }
        }
        .flatten()

    val partNumbersWithAdjacentSymbolAfterGearRemoval = partNumbersWithAdjacentSymbol.toMutableList()

    val appliedGears = gears
        .groupBy { it }
        .filter { it.value.size == 2 }
        .keys

    val partNumbersWithGearApplied = mutableListOf<Int>()

    appliedGears
        .forEach { gearCoordinate ->
            partNumbersWithAdjacentSymbol
                .filter { it.first.any { partCoordinate -> partCoordinate.isAdjacentTo(gearCoordinate) } }
                .takeIf { it.size == 2 }
                ?.let { gearedParts ->
                    partNumbersWithAdjacentSymbolAfterGearRemoval.removeAll(gearedParts)

                    partNumbersWithGearApplied.add(
                        gearedParts
                            .map { it.second }
                            .reduce { acc, partNum -> acc * partNum }
                    )
                } ?: throw Exception("no connection found")
        }

//    partNumbersWithGearApplied + partNumbersWithAdjacentSymbolAfterGearRemoval.map { it.second }

    return partNumbersWithGearApplied.sum() + partNumbersWithAdjacentSymbolAfterGearRemoval
        .sumOf { it.second }
}

15 + 2 + 20 + 5 + 23 + 10 + 12
day03p1(customTestInput) // 87
day03p1(testInputOnline) // 925
//day03p1(testInput35)
//day03p1(testInput)
//day03p1(input)

15 + 2*20 + 5 + 23 + 10 + 12
//day03p2(customTestInput) // 105
day03p2(testInputOnline) // 6756
day03p2(testInput) // 467835
day03p2(input)

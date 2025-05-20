package it.qbr.testapisudoku.model

data class Board(val cells: List<List<Int>>)

fun List<List<Double>>.toBoard(): Board {
    return Board(
        this.map { row -> row.map { it.toInt() } }
    )
}
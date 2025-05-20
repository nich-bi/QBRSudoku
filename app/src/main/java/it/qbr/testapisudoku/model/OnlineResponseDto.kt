package it.qbr.testapisudoku.model


data class OnlineResponseDto(
    val newboard: NewBoard
)

data class NewBoard(
    val grids: List<Grid>
)

data class Grid(
    val value: List<List<Double>>,
    val solution: List<List<Double>>
)
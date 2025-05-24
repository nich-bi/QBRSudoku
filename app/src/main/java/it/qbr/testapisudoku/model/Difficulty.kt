package it.qbr.testapisudoku.model

const val maxErrEasy = 10
const val maxErrMid = 5
const val maxErrHard = 2
const val maxErrImpossible = 0


enum class Difficulty(val maxErrors: Int) {
    EASY(maxErrEasy), MID(maxErrMid), HARD(maxErrHard), IMPOSSIBLE(maxErrImpossible)
}


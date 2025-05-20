package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import it.qbr.testapisudoku.model.Board
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.network.SudokuApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/*
@Composable
fun SudokuScreen() {
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val (initialBoard, _) = withContext(Dispatchers.IO) {
            SudokuApi.generateOnlineBoard()
        }
        board = initialBoard
        loading = false
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        board?.let {
            SudokuBoard(it.cells)
        }
    }
}
*/

@Composable
fun SudokuScreen() {
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var cells by remember { mutableStateOf<List<MutableList<Int>>>(emptyList()) }
    var fixedCells by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val (initialBoard, _) = withContext(Dispatchers.IO) {
            SudokuApi.generateOnlineBoard()
        }
        board = initialBoard
        cells = initialBoard.cells.map { it.toMutableList() }
        fixedCells = initialBoard.cells.map { row -> row.map { it != 0 } }
        loading = false
    }

    fun updateCell(row: Int, col: Int, value: Int) {
        if (value in 0..9 && !fixedCells[row][col]) {
            cells = cells.mapIndexed { r, rowList ->
                if (r == row) rowList.mapIndexed { c, oldValue ->
                    if (c == col) value else oldValue
                }.toMutableList()
                else rowList
            }
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFE7EBF0))
        ) {
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SudokuBoard(
                    grid = cells,
                    fixedCells = fixedCells,
                    selectedCell = selectedCell,
                    onCellSelected = { row, col -> if (!fixedCells[row][col]) selectedCell = row to col }
                )
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                SudokuKeypad { number ->
                    selectedCell?.let { (row, col) ->
                        updateCell(row, col, if (number == 0) 0 else number)
                    }
                }
            }
        }
    }
}
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.qbr.testapisudoku.network.SudokuApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.material3.Icon
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(navController: NavHostController) {
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var cells by remember { mutableStateOf<List<MutableList<Int>>>(emptyList()) }
    var fixedCells by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var solution by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var errorCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var seconds by remember { mutableStateOf(0) }
    var errorCount by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val (initialBoard, solutionBoard) = withContext(Dispatchers.IO) {
            SudokuApi.generateOnlineBoard()
        }
        board = initialBoard
        cells = initialBoard.cells.map { it.toMutableList() }
        fixedCells = initialBoard.cells.map { row -> row.map { it != 0 } }
        solution = solutionBoard.cells
        loading = false
    }
    LaunchedEffect(loading) {
        if (!loading) {
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }
    fun updateCell(row: Int, col: Int, value: Int) {
        if (value in 0..9 && !fixedCells[row][col]) {
            if (solution.isNotEmpty() && value != 0 && value != solution[row][col]) {
                errorCell = row to col
                //errorMessage = "Numero errato!"
                errorCount++
            } else {
                errorCell = null
                errorMessage = null
            }
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
            )
            {


                SudokuTopBar(seconds = seconds, errorCount = errorCount,onHomeClick = { showDialog = true } )
                Spacer(Modifier.height(14.dp))

                SudokuBoard(
                    grid = cells,
                    fixedCells = fixedCells,
                    selectedCell = selectedCell,
                    errorCell = errorCell,
                    onCellSelected = { row, col -> if (!fixedCells[row][col]) selectedCell = row to col },
                    onSuggestMove = { }
                )
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        selectedCell?.let { (row, col) ->
                            updateCell(row, col, solution[row][col])
                        }
                    },
                    modifier = Modifier.align(Alignment.Center).padding(bottom = 16.dp)
                ) {
                    Text("Suggerisci Mossa")
                }
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


            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Sei sicuro?") },
                    text = { Text("Tornando alla home perderai la partita in corso.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            navController.popBackStack() // Torna alla home
                        }) {
                            Text("Sì, torna alla Home")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Annulla")
                        }
                    }
                )
            }
        }
        }
    }

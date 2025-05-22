package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import it.qbr.testapisudoku.model.Board
import it.qbr.testapisudoku.network.SudokuApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.get
import kotlin.text.compareTo


enum class Difficulty(val maxErrors: Int) {
    FACILE(10), MEDIO(5), DIFFICILE(2), IMPOSSIBILE(0)
}

/**
 * Composable function for the Sudoku game screen.
 *
 * This function manages the entire Sudoku game flow, including:
 * - Difficulty selection.
 * - Game board generation (fetches from an API).
 * - User interaction with the Sudoku grid.
 * - Input validation and error tracking.
 * - Hints functionality.
 * - Game over conditions.
 * - Timer.
 * - Navigation back to the home screen.
 *
 * @param navController The NavHostController used for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(navController: NavHostController) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) } // 0: selezione livello, 1: gioco
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var maxErrors by remember { mutableStateOf(10) }
    var errorCount by remember { mutableIntStateOf(0) }
    var showGameOver by remember { mutableStateOf(false) }
    var hintsLeft by remember { mutableIntStateOf(3) }
    var showNoHintsDialog by remember { mutableStateOf(false) }
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var cells by remember { mutableStateOf<List<MutableList<Int>>>(emptyList()) }
    var fixedCells by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var solution by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var errorCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var seconds by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var errorCells by remember { mutableStateOf<Set<Pair<Int, Int>>>(emptySet()) }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }


    if (step == 0) {
        // Step selezione difficoltà

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton( onClick = { navController.popBackStack() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_svgrepo_com),
                    contentDescription = "Torna alla Home",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text("Scegli la difficoltà", fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))
            Difficulty.entries.forEach { diff ->
                Button(
                    onClick = {
                        selectedDifficulty = diff
                        maxErrors = diff.maxErrors
                        errorCount = 0
                        step = 1 // Vai al gioco
                    },
                    Modifier.padding(8.dp)
                ) {
                    Text(diff.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    } else {
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

        fun checkWin(): Boolean {
            if (errorCount > maxErrors) return false
            if (cells.size != 9 || solution.size != 9) return false
            for (r in 0..8) {
                for (c in 0..8) {
                    if (cells[r][c] != solution[r][c]) return false
                }
            }
            return true
        }

    fun updateCell(row: Int, col: Int, value: Int) {
        if (value in 0..9 && !fixedCells[row][col]) {
            if (solution.isNotEmpty() && value != 0 && value != solution[row][col]) {
               // errorCell = row to col
                errorCells = errorCells + (row to col)
                errorCount++
                if (errorCount >= maxErrors) {
                    showGameOver = true
                }
            } else {

                errorCells = errorCells - (row to col)
            }
            cells = cells.mapIndexed { r, rowList ->
                if (r == row) rowList.mapIndexed { c, oldValue ->
                    if (c == col) value else oldValue
                }.toMutableList()
                else rowList
            }
            if (checkWin()) {
                showWinDialog = true
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
                    errorCells = errorCells,
                    selectedNumber = selectedNumber,
                    onCellSelected = { row, col -> if (!fixedCells[row][col]) selectedCell = row to col },
                    onSuggestMove = { }
                )
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = {
                        if(hintsLeft > 0) {
                            hintsLeft--
                            selectedCell?.let { (row, col) ->
                                updateCell(row, col, solution[row][col])
                            }
                        } else {
                            showNoHintsDialog = true
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 250.dp)
                ) {
                    Text("Suggerisci Mossa")
                    if (hintsLeft > 0) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .offset(x = 5.dp, y = (-12).dp)
                                .background(Color.Red, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = hintsLeft.toString(),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                }
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                SudokuKeypad { number ->
                    selectedNumber = if (number == 0) null else number
                    selectedCell?.let { (row, col) ->
                        updateCell(row, col, if (number == 0) 0 else number)
                    }
                }
            }

                if (showNoHintsDialog) {
                    AlertDialog(
                        onDismissRequest = { showNoHintsDialog = false },
                        title = { Text("Suggerimenti terminati") },
                        text = { Text("Hai esaurito i suggerimenti negro.") },
                        confirmButton = {
                            TextButton(onClick = { showNoHintsDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Sei sicuro?") },
                    text = { Text("Tornando alla home perderai la partita in corso.") },
                    confirmButton = {
                        TextButton(onClick = {

                            CoroutineScope(Dispatchers.IO).launch {
                                AppDatabase.getDatabase(context).partitaDao().inserisci(
                                    Game(
                                        dataOra = System.currentTimeMillis(),
                                        vinta = false, // o true se vinta
                                        tempo = seconds,
                                        difficolta = selectedDifficulty?.name ?: ""
                                    )
                                )
                            }
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


                if (showGameOver) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Hai perso!") },
                        text = { Text("Hai raggiunto il numero massimo di errori.\nVerrà generata una nuova partita.") },
                        confirmButton = {
                            TextButton(onClick = {
                                // Salva la partita persa
                                CoroutineScope(Dispatchers.IO).launch {
                                    AppDatabase.getDatabase(context).partitaDao().inserisci(
                                        Game(
                                            dataOra = System.currentTimeMillis(),
                                            vinta = false, // o true se vinta
                                            tempo = seconds,
                                            difficolta = selectedDifficulty?.name ?: ""
                                        )
                                    )
                                }

                                // Rigenera la partita e torna alla selezione livello
                                step = 0
                                showGameOver = false
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
                if (showWinDialog) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Hai vinto!") },
                        text = { Text("Complimenti, hai risolto il Sudoku!") },
                        confirmButton = {
                            TextButton(onClick = {
                                // Salva la partita vinta
                                CoroutineScope(Dispatchers.IO).launch {
                                    AppDatabase.getDatabase(context).partitaDao().inserisci(
                                        Game(
                                            dataOra = System.currentTimeMillis(),
                                            vinta = true,
                                            tempo = seconds,
                                            difficolta = selectedDifficulty?.name ?: ""
                                        )
                                    )
                                }
                                // Torna alla selezione livello
                                step = 0
                                showWinDialog = false
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }

         }
        }
      }
    }
 }


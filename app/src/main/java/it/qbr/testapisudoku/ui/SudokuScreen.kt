package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.gson.Gson
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import it.qbr.testapisudoku.model.Board
import it.qbr.testapisudoku.model.Difficulty
import it.qbr.testapisudoku.network.SudokuApi
import it.qbr.testapisudoku.ui.theme.blue_p
import it.qbr.testapisudoku.ui.theme.blue_primary
import it.qbr.testapisudoku.utils.PreferencesConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri


@SuppressLint("MutableCollectionMutableState", "UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight")
@Composable
fun SudokuScreen(navController: NavHostController) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var maxErrors by remember { mutableIntStateOf(10) }
    var errorCount by remember { mutableIntStateOf(0) }
    var showGameOver by remember { mutableStateOf(false) }
    var hintsLeft by remember { mutableIntStateOf(0) }
    var showNoHintsDialog by remember { mutableStateOf(false) }
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var cells by remember { mutableStateOf<List<MutableList<Int>>>(emptyList()) }
    var fixedCells by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var solution by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var seconds by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var errorCells by remember { mutableStateOf<Set<Pair<Int, Int>>>(emptySet()) }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }
    val completedNumbers = remember { mutableStateListOf<Int>() }
    val isSuggestEnabled = selectedCell?.let { (row, col) -> !fixedCells.getOrNull(row)?.getOrNull(col).orFalse() && cells.getOrNull(row)?.getOrNull(col) != solution.getOrNull(row)?.getOrNull(col) } == true
    var noteMode by remember { mutableStateOf(false) }
    var cellNotes by remember { mutableStateOf(mutableMapOf<Pair<Int, Int>, MutableSet<Int>>()) }

    LaunchedEffect(cells) {
        completedNumbers.clear()
        for (n in 1..9) {
            val count = cells.sumOf { row -> row.count { it == n } }
            if (count == 9) completedNumbers.add(n)
        }
    }

    if (step == 0) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
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
                        step = 1
                    },
                    Modifier.padding(8.dp).size(150.dp, 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blue_primary),
                    shape = RoundedCornerShape(50.dp),
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
            for (r in 0..8)
                for (c in 0..8)
                    if (cells[r][c] != solution[r][c]) return false
            return true
        }

        fun updateCell(row: Int, col: Int, value: Int) {
            if (noteMode) {
                val key = row to col
                val notes = cellNotes.getOrPut(key) { mutableSetOf() }
                if (notes.contains(value)) notes.remove(value) else notes.add(value)
                cellNotes = cellNotes.toMutableMap()
            } else {
                if (value in 0..9 && !fixedCells.getOrNull(row)?.getOrNull(col).orFalse()) {
                    if (solution.isNotEmpty() && value != 0 && value != solution[row][col]) {
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
                        }.toMutableList() else rowList
                    }
                    if (checkWin()) showWinDialog = true
                }
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        } else {
            // ----------- LAYOUT RESPONSIVE CON BOARD SEMPRE VISIBILE -------------
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val screenHeight = configuration.screenHeightDp.dp
            // Stima delle altezze delle barre (personalizza se necessario)
            val topBarHeight = 56.dp
            val iconBarHeight = 56.dp
            val keypadHeight = 72.dp
            val verticalPadding = 16.dp * 2
            // Calcola la dimensione massima della board
            val maxBoardHeight = screenHeight - topBarHeight - iconBarHeight - keypadHeight - verticalPadding
            val boardSize = androidx.compose.ui.unit.min(screenWidth, maxBoardHeight).coerceAtLeast(0.dp)

            Scaffold(
                topBar = {
                    Box(modifier = Modifier.statusBarsPadding()) {
                        SudokuTopBar(
                            maxErr = maxErrors,
                            seconds = seconds,
                            errorCount = errorCount,
                            onHomeClick = { showDialog = true }
                        )
                    }
                },
                containerColor = Color(0xFFE7EBF0)
            ) { innerPadding ->

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    val (boardRef, iconBar, keypad) = createRefs()

                    // BOARD: sempre quadrata e visibile
                    Box(
                        modifier = Modifier
                            .constrainAs(boardRef) {
                                top.linkTo(parent.top, margin = topBarHeight)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .size(boardSize)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        SudokuBoard(
                            grid = cells,
                            fixedCells = fixedCells,
                            selectedCell = selectedCell,
                            errorCells = errorCells,
                            selectedNumber = selectedNumber,
                            onCellSelected = { row, col ->
                                selectedCell = row to col
                                selectedNumber = if (cells.getOrNull(row)?.getOrNull(col) != 0) cells[row][col] else null
                            },
                            onSuggestMove = { },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // ICON BAR
                    SudokuIconBar(
                        noteMode = noteMode,
                        onNoteModeToggle = { noteMode = !noteMode },
                        onErase = { selectedCell?.let { (row, col) -> updateCell(row, col, 0) } },
                        onSuggest = {
                            hintsLeft++
                            selectedCell?.let { (row, col) ->
                                updateCell(row, col, solution[row][col])
                                selectedNumber = if (cells[row][col] != 0) cells[row][col] else null
                            }
                        },
                        isSuggestEnabled = isSuggestEnabled,
                        hintsLeft = hintsLeft,
                        maxHints = PreferencesConstants.MAX_HINT,
                        showNoHintsDialog = { showNoHintsDialog = true },
                        blue_p = blue_p,
                        onHelp = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.wikihow.com/Solve-a-Sudoku".toUri()
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .constrainAs(iconBar) {
                                top.linkTo(boardRef.bottom, margin = 8.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .height(iconBarHeight)
                            .fillMaxWidth()
                    )

                    // KEYPAD
                    SudokuKeypad(
                        onNumberSelected = { number ->
                            selectedNumber = if (number == 0) null else number
                            selectedCell?.let { (row, col) ->
                                updateCell(row, col, if (number == 0) 0 else number)
                            }
                        },
                        disabledNumbers = completedNumbers,
                        modifier = Modifier
                            .constrainAs(keypad) {
                                top.linkTo(iconBar.bottom, margin = 8.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                            .height(keypadHeight)
                            .fillMaxWidth()
                    )
                }
            }
        }

        // ---- DIALOGS ----
        if (showNoHintsDialog) {
            AlertDialog(
                onDismissRequest = { showNoHintsDialog = false },
                title = { Text("Suggerimenti terminati") },
                text = { Text("Hai esaurito i suggerimenti disponibili.") },
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
                                    vinta = false,
                                    tempo = seconds,
                                    difficolta = selectedDifficulty?.name ?: "",
                                    errori = errorCount,
                                    initialBoard = Gson().toJson(board),
                                    solutionBoard = Gson().toJson(solution),
                                    finalBard = Gson().toJson(cells)
                                )
                            )
                        }
                        showDialog = false
                        navController.popBackStack()
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
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.getDatabase(context).partitaDao().inserisci(
                                Game(
                                    dataOra = System.currentTimeMillis(),
                                    vinta = false,
                                    tempo = seconds,
                                    difficolta = selectedDifficulty?.name ?: "",
                                    errori = errorCount,
                                    initialBoard = Gson().toJson(board),
                                    solutionBoard = Gson().toJson(solution),
                                    finalBard = Gson().toJson(cells)
                                )
                            )
                        }
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
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.getDatabase(context).partitaDao().inserisci(
                                Game(
                                    dataOra = System.currentTimeMillis(),
                                    vinta = true,
                                    tempo = seconds,
                                    difficolta = selectedDifficulty?.name ?: "",
                                    errori = errorCount,
                                    initialBoard = Gson().toJson(board),
                                    solutionBoard = Gson().toJson(solution),
                                    finalBard = Gson().toJson(cells)
                                )
                            )
                        }
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

// Helper extension for null safety
private fun Boolean?.orFalse() = this == true
package it.qbr.testapisudoku.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.background_cell
import it.qbr.testapisudoku.ui.theme.background_rows
import it.qbr.testapisudoku.ui.theme.background_same_number
import it.qbr.testapisudoku.ui.theme.blue_number
import it.qbr.testapisudoku.ui.theme.blue_p
import it.qbr.testapisudoku.ui.theme.gray
import it.qbr.testapisudoku.ui.theme.light_gray
import it.qbr.testapisudoku.ui.theme.quit_background


@Composable
fun SudokuTopBar(
    maxErr: Int,
    seconds: Int,
    errorCount: Int,
    onHomeClick: () -> Unit
) {
    val minutes = seconds / 60
    val secs = seconds % 60
    val timeString = "%02d%02d".format(minutes, secs)
    val errorCountString = errorCount.toString().padStart(2, '0')
    val maxErrString = maxErr.toString().padStart(2, '0')

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onHomeClick) {
            Icon(
                painter = painterResource(id = R.drawable.back_svgrepo_com),
                contentDescription = "Torna alla Home",
                modifier = Modifier.size(30.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // mm:ss formato -> timeString[0], timeString[1], ':', timeString[2], timeString[3]
                    for (i in 0..3) {
                        AnimatedDigit(
                            digit = timeString[i],
                            key = "timer$i"
                        )
                        if (i == 1) {
                            Text(
                                text = ":",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                                color = Color.Black
                            )
                        }
                    }
                }
                Text(
                    text = "Tempo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = Color.Black
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Animazione cifra-per-cifra per gli errori
                for (i in errorCountString.indices) {
                    AnimatedDigit(
                        digit = errorCountString[i],
                        key = "err$i"
                    )
                }
                Text(
                    text = "/",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = Color.Black,
                )
                for (i in maxErrString.indices) {
                    AnimatedDigit(
                        digit = maxErrString[i],
                        key = "maxerr$i"
                    )
                }
            }

            Text(
                text = "Errori",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = Color.Black,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedDigit(digit: Char, key: String) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() with
                    slideOutVertically { height -> -height } + fadeOut()
        },
        label = key
    ) { targetDigit ->
        Text(
            text = targetDigit.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.poppins_bold)),
            color = Color.Black,
        )
    }
}



@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    errorCells: Set<Pair<Int, Int>>,
    selectedNumber: Int?,
    onSuggestMove: () -> Unit,
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        // Calcola la dimensione massima per la cella in base allo spazio disponibile
        val cellSize = (maxWidth / 9).coerceAtMost(maxHeight / 9)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for ((rowIdx, row) in grid.withIndex()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for ((colIdx, cell) in row.withIndex()) {
                        val thickTop = if (rowIdx == 0) 3.dp else if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickLeft = if (colIdx == 0) 3.dp else if (colIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickRight = if (colIdx == 8) 3.dp else if ((colIdx + 1) % 3 == 0) 2.dp else 0.5.dp
                        val thickBottom = if (rowIdx == 8) 3.dp else if ((rowIdx + 1) % 3 == 0) 2.dp else 0.5.dp

                        val isHighlighted = selectedCell?.let { (selRow, selCol) ->
                            rowIdx == selRow ||
                                    colIdx == selCol ||
                                    (rowIdx / 3 == selRow / 3 && colIdx / 3 == selCol / 3)
                        } == true

                        SudokuCell(
                            value = cell,
                            isSelected = selectedCell == Pair(rowIdx, colIdx),
                            isFixed = fixedCells.getOrNull(rowIdx)?.getOrNull(colIdx) ?: false,
                            isError = errorCells.contains(Pair(rowIdx, colIdx)),
                            isHighlighted = isHighlighted && selectedCell != Pair(rowIdx, colIdx),
                            isSameNumber = selectedNumber != null && cell == selectedNumber && cell != 0,
                            onClick = { onCellSelected(rowIdx, colIdx) },
                            borderTop = thickTop,
                            borderLeft = thickLeft,
                            borderRight = thickRight,
                            borderBottom = thickBottom,
                            borderColor = Color.Black,
                            cellSize = cellSize
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun SudokuCell(
    value: Int,
    isSelected: Boolean,
    isFixed: Boolean,
    isError: Boolean,
    onClick: () -> Unit,
    borderTop: Dp = 0.dp,
    borderLeft: Dp = 0.dp,
    borderRight: Dp = 0.dp,
    borderBottom: Dp = 0.dp,
    isHighlighted: Boolean,
    isSameNumber: Boolean,
    borderColor: Color,
    notes: Set<Int> = emptySet(),
    cellSize: Dp
) {
    val backgroundColor = when {
        isHighlighted -> background_rows
        isSelected -> background_cell
        isSameNumber -> background_same_number
        else -> Color.White
    }

    val numberColor = when {
        isError -> Color.Red
        isFixed -> Color.Black
        else -> blue_number
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cellSize)
            .background(backgroundColor)
            .drawBehind {
                // Bordo superiore
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = borderTop.toPx()
                )
                // Bordo sinistro
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = borderLeft.toPx()
                )
                // Bordo destro
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderRight.toPx()
                )
                // Bordo inferiore
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderBottom.toPx()
                )
            }
            .clickable { onClick() }
    ) {
        if (value != 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(cellSize * 0.8f)
            ) {
                Text(
                    text = value.toString(),
                    fontSize = (cellSize.value * 0.6).sp,
                    color = numberColor,
                    fontWeight = FontWeight.Normal
                )
            }
        } else if (notes.isNotEmpty()) {
            // Mostra le note in piccolo, ad esempio in una griglia 3x3
            Column {
                for (i in 1..9 step 3) {
                    Row {
                        for (j in i..i + 2) {
                            Text(
                                text = if (notes.contains(j)) j.toString() else "",
                                fontSize = (cellSize.value * 0.2).sp,
                                modifier = Modifier.width(cellSize / 3)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun SudokuKeypad(
    onNumberSelected: (Int) -> Unit,
    disabledNumbers: List<Int>,
    modifier: Modifier,
    onAbbandona: (() -> Unit)? = null
) {
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prima fila: 1-5
        Row(
            Modifier
                .fillMaxWidth().
                padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 1..5) {
                val isDisabled = disabledNumbers.contains(number)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isDisabled) background_same_number else light_gray)
                        .clickable(enabled = !isDisabled) { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 25.sp,
                        color = if (!isDisabled) blue_p else gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Seconda fila: 6-9 + un Box vuoto alla fine
        Row(
            Modifier
                .fillMaxWidth().
                padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 6..9) {
                val isDisabled = disabledNumbers.contains(number)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isDisabled) background_same_number else light_gray)
                        .clickable(enabled = !isDisabled) { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 25.sp,
                        color = if (!isDisabled) blue_p else gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(quit_background)
                    .clickable()  {
                        onAbbandona?.invoke()
                        },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Abbandona",
                    tint = Color(0xFFB71C1C),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@Composable
fun SudokuIconBar(
    noteMode: Boolean,
    onNoteModeToggle: () -> Unit,
    onErase: () -> Unit,
    onSuggest: () -> Unit,
    isSuggestEnabled: Boolean,
    hintsLeft: Int,
    maxHints: Int,
    showNoHintsDialog: () -> Unit,
    blue_p: Color,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(Modifier.align(Alignment.BottomCenter)) {
            // Note Button
            IconButton(
                onClick = onNoteModeToggle,
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.notes_svgrepo_com),
                    contentDescription = "Modalità note",
                    tint = if (noteMode) Color.Blue else Color.Black,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Note",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Erase Button
                IconButton(
                    onClick = onErase,
                    modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eraser_24),
                        contentDescription = "Cancella cella",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Cancella",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 55.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }


            // Suggest Button
            IconButton(
                onClick = {
                    if (hintsLeft <= maxHints) {
                        onSuggest()
                    } else {
                        showNoHintsDialog()
                    }
                },
                enabled = isSuggestEnabled,
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lightbulb_stars_24),
                    contentDescription = "Suggerimento",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Sugg",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
                if (hintsLeft <= maxHints) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 8.dp, y = 12.dp)
                            .background(blue_p, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (maxHints - hintsLeft).toString(),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Help Button
            IconButton(
                onClick = onHelp,
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_question),
                    contentDescription = "Come si gioca",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Aiuto",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}




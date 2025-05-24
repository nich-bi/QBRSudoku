package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import it.qbr.testapisudoku.utils.PreferencesConstants

@Composable
fun SudokuTopBar(maxErr: Int, seconds: Int, errorCount: Int,onHomeClick: () -> Unit) {
    val minutes = seconds / 60
    val secs = seconds % 60

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )
    {
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
                Text(
                    text = "Tempo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.segoeuithis)),
                    color = gray
                )

                Text(
                    text = "%02d:%02d".format(minutes, secs),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.segoeuithis)),
                    color = gray
                )
            }
            /*
            Image(
                painter = painterResource(id = R.drawable.stopwatch_svgrepo_com),
                contentDescription = "Icona timer",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(4.dp)) // Spazio tra immagine e testo
            Text(
                text = "%02d:%02d".format(minutes, secs),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.segoeuithis)),
                color = Color.Black
            )

             */
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Errori",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily(Font(R.font.segoeuithis)),
                color = gray,
            )

            Text(
                text = "$errorCount / $maxErr",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily(Font(R.font.segoeuithis)),
                color = gray,
            )
        }

    }
}


@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    errorCells:Set<Pair<Int, Int>>,
    selectedNumber: Int?,
    onSuggestMove: ()-> Unit,
    onCellSelected: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
           .padding(top = 50.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)

    ) {
        Column(
            Modifier
                .padding(10.dp)
        ) {
            for ((rowIdx, row) in grid.withIndex()) {
                Row {
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
                            isFixed = fixedCells[rowIdx][colIdx],
                            isError = errorCells.contains(Pair(rowIdx, colIdx)),
                            isHighlighted = isHighlighted && selectedCell != Pair(rowIdx, colIdx),
                            isSameNumber = selectedNumber != null && cell == selectedNumber && cell != 0,
                            onClick = { onCellSelected(rowIdx, colIdx) },
                            borderTop = thickTop,
                            borderLeft = thickLeft,
                            borderRight = thickRight,
                            borderBottom = thickBottom,
                            borderColor = Color.Black
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
) {
    val backgroundColor = when {
        isHighlighted -> background_rows
        isSelected -> background_cell
        isSameNumber-> background_same_number
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
            .size(40.dp)
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
            .clickable{ onClick() } // cliccabile anche numeri fissi

    ) {
        if (value != 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)

            ) {
                Text(
                    text = value.toString(),
                    fontSize = 27.sp,
                    color = numberColor,
                    fontWeight = FontWeight.Normal
                )
            }
        }else if (notes.isNotEmpty()) {
            // Mostra le note in piccolo, ad esempio in una griglia 3x3
            Column {
                for (i in 1..9 step 3) {
                    Row {
                        for (j in i..i+2) {
                            Text(
                                text = if (notes.contains(j)) j.toString() else "",
                                fontSize = 10.sp,
                                modifier = Modifier.width(10.dp)
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
    isDisabled: Boolean = false
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prima fila: 1-5
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 1..5) {
                val isDisabled = disabledNumbers.contains(number)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDisabled) gray else background_same_number)
                        .clickable(enabled = !isDisabled){ onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 25.sp,
                        color = if (isDisabled) Color.White else blue_p,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Seconda fila: 6-9
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 6..9) {
                val isDisabled = disabledNumbers.contains(number)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDisabled) gray else background_same_number)
                        .clickable(enabled = !isDisabled) { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 25.sp,
                        color =if (isDisabled) Color.White else blue_p,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }

}




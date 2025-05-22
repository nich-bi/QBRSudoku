package it.qbr.testapisudoku.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.light_gray
import it.qbr.testapisudoku.ui.theme.light_primary
import it.qbr.testapisudoku.ui.theme.light_secondary
import it.qbr.testapisudoku.ui.theme.white



@Composable
fun SudokuTopBar(seconds: Int, errorCount: Int,onHomeClick: () -> Unit) {
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
        }
        Text(
            text = "Errori: $errorCount",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.segoeuithis)),
            color = Color.Black,

        )
    }
}


@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
   // errorCell: Pair<Int, Int>?,
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
                     val thickTop = if (rowIdx == 0) 3.dp else if (rowIdx % 3 == 0) 3.dp else 0.5.dp
                        val thickLeft = if (colIdx == 0) 3.dp else if (colIdx % 3 == 0) 3.dp else 0.5.dp
                        val thickRight = if (colIdx == 8) 3.dp else if ((colIdx + 1) % 3 == 0) 3.dp else 0.5.dp
                        val thickBottom = if (rowIdx == 8) 3.dp else if ((rowIdx + 1) % 3 == 0) 3.dp else 0.5.dp

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
) {
    val backgroundColor = when {
      //  isError -> Color.Red.copy(alpha = 0.2f)
        isSelected -> light_gray.copy(alpha = 0.5f)
        isHighlighted -> light_gray
        else -> Color.White
    }

    val circularShapeColor = when {
        isError -> Color.Red.copy(alpha = 0.2f)
        isSameNumber-> light_primary
        else -> light_secondary
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
            .clickable(enabled = !isFixed, onClick = onClick)
    ) {
        if (value != 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .background(circularShapeColor, shape = CircleShape)
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}



@Composable
fun SudokuKeypad(onNumberSelected: (Int) -> Unit) {
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
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, light_primary, RoundedCornerShape(10.dp))
                        .background(white)
                        .clickable { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Light
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
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(55.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, light_primary, RoundedCornerShape(10.dp))
                        .background(white)
                        .border(2.dp, Color.Black, RectangleShape)
                        .clickable { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Light
                    )
                }
            }

        }

    }
}


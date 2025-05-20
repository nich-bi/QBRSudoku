package it.qbr.testapisudoku.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush


/*
@Composable
fun SudokuBoard(grid: List<List<Int>>) {
    Column(Modifier.padding(16.dp)) {
        for (row in grid) {
            Row {
                for (cell in row) {
                    SudokuCell(cell)
                }
            }
        }
    }
}

@Composable
fun SudokuCell(value: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .padding(1.dp)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        if (value != 0) {
            Text(text = value.toString(), fontSize = 18.sp, color = Color.Black)
        }
    }
}*/

@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    errorCell: Pair<Int, Int>?,
    onSuggestMove: ()-> Unit,
    onCellSelected: (Int, Int) -> Unit

) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Column(
            Modifier
                .padding(10.dp)
        ) {
            for ((rowIdx, row) in grid.withIndex()) {
                Row {
                    for ((colIdx, cell) in row.withIndex()) {
                        val thickTop = if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickLeft = if (colIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickRight = if (colIdx == 8) 2.dp else 0.5.dp
                        val thickBottom = if (rowIdx == 8) 2.dp else 0.5.dp

                        SudokuCell(
                            value = cell,
                            isSelected = selectedCell == Pair(rowIdx, colIdx),
                            isFixed = fixedCells[rowIdx][colIdx],
                            isError = errorCell == Pair(rowIdx, colIdx),
                            onClick = { onCellSelected(rowIdx, colIdx) },
                            borderTop = thickTop,
                            borderLeft = thickLeft,
                            borderRight = thickRight,
                            borderBottom = thickBottom
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
    borderTop: Dp = 1.dp,
    borderLeft: Dp = 1.dp,
    borderRight: Dp = 1.dp,
    borderBottom: Dp = 1.dp
) {
    val backgroundColor = when {
        isError -> Color.Red.copy(alpha = 0.2f)
        isSelected -> Color(0xFFBBDEFB).copy(alpha = 0.5f)
        else -> Color(0xFFF8FAFF)
    }
    val borderColor = Color(0xFF90CAF9)
    val textColor = if (isFixed) Color(0xFF1976D2) else Color(0xFF0D47A1)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .padding(0.5.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(2.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(enabled = !isFixed, onClick = onClick)
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                fontSize = 22.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun SudokuKeypad(onNumberSelected: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0..2) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (col in 1..3) {
                    val number = row * 3 + col
                    if (number <= 9) {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(12.dp))
                                .clickable { onNumberSelected(number) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 22.sp,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .padding(5.dp)
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFF90CAF9), RoundedCornerShape(12.dp))
                .clickable { onNumberSelected(0) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "C", fontSize = 22.sp, color = Color(0xFF1976D2))
        }
    }
}

@Composable
fun SudokuTopBar(
    onReset: () -> Unit,
    onTakePicture: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                )
            )
            .padding(top = 32.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, Color.White)
        ) {
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = null,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("↻ Reset", color = Color.White, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, Color.White)
        ) {
            Button(
                onClick = onTakePicture,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = null,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("📷 Take picture", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
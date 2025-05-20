package it.qbr.testapisudoku.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
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
    onCellSelected: (Int, Int) -> Unit
) {
    Column(
        Modifier
            .padding(8.dp)
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
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

@Composable
fun SudokuCell(
    value: Int,
    isSelected: Boolean,
    isFixed: Boolean,
    onClick: () -> Unit,
    borderTop: Dp = 1.dp,
    borderLeft: Dp = 1.dp,
    borderRight: Dp = 1.dp,
    borderBottom: Dp = 1.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .padding(0.dp)
            .border(
                width = borderTop,
                color = Color.Black,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
            )
            .border(borderLeft, Color.Black, RoundedCornerShape(0.dp))
            .border(borderRight, Color.Black, RoundedCornerShape(0.dp))
            .border(borderBottom, Color.Black, RoundedCornerShape(0.dp))
            .background(
                color = when {
                    isSelected -> Color(0xFFD0E7FF)
                    isFixed -> Color(0xFFE0E0E0)
                    value == 0 -> Color(0xFFF5F5F5)
                    else -> Color(0xFFFFFFFF)
                },
                shape = RoundedCornerShape(4.dp)
            )
            .then(
                if (!isFixed) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                fontSize = 18.sp,
                color = if (isFixed) Color.DarkGray else Color.Black
            )
        }
    }
}

@Composable
fun SudokuKeypad(
    onKeyClick: (Int) -> Unit
) {
    Surface(
        color = Color(0xFFE7EBF0),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 3) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 1..3) {
                        val number = row * 3 + col
                        Button(
                            onClick = { onKeyClick(number) },
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 22.sp,
                                color = Color(0xFF0A324D)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onKeyClick(0) },
                    modifier = Modifier
                        .size(width = 120.dp, height = 44.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBCC8D3)
                    )
                ) {
                    Text(text = "Cancella", fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }
}
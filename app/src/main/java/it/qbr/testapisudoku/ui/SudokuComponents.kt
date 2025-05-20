package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
}

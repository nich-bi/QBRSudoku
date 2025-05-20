package it.qbr.testapisudoku

import androidx.activity.ComponentActivity
import it.qbr.testapisudoku.ui.SudokuScreen
import android.os.Bundle

import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuScreen()
        }
    }
}
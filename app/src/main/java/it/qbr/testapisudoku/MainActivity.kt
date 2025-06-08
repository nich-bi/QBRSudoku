package it.qbr.testapisudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import it.qbr.testapisudoku.ui.MainNavHost
import it.qbr.testapisudoku.ui.theme.QBRSudokuTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QBRSudokuTheme {
                val navController = rememberNavController()
                MainNavHost(navController)
            }
        }
    }
}


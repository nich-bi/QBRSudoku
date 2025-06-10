package it.qbr.testapisudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import it.qbr.testapisudoku.ui.MainNavHost
import it.qbr.testapisudoku.ui.theme.QBRSudokuTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()
            QBRSudokuTheme(darkTheme = isDarkTheme) {
                MainNavHost(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}


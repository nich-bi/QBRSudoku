package it.qbr.testapisudoku

import androidx.activity.ComponentActivity
import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import it.qbr.testapisudoku.ui.MainNavHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MainNavHost(navController)
        }
    }
}


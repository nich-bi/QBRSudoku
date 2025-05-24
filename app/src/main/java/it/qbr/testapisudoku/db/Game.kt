package it.qbr.testapisudoku.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Game")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dataOra: Long,
    val vinta: Boolean,
    val tempo: Int,
    val difficolta: String,
    val errori: Int
)
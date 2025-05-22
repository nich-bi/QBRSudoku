package it.qbr.testapisudoku.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Insert
    suspend fun inserisci(game: Game)

    @Query("SELECT * FROM game ORDER BY dataOra DESC")
    suspend fun tutteLePartite(): List<Game>
}
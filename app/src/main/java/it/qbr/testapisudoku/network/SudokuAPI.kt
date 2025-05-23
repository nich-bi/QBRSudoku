package it.qbr.testapisudoku.network

import com.google.gson.Gson
import it.qbr.testapisudoku.model.Board
import it.qbr.testapisudoku.model.OnlineResponseDto
import it.qbr.testapisudoku.model.toBoard
import okhttp3.OkHttpClient
import okhttp3.Request


object SudokuApi {
    private val client = OkHttpClient()

    fun generateOnlineBoard(): Pair<Board, Board> {
        val request = Request.Builder()
            .url("https://sudoku-api.vercel.app/api/dosuku")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful){
            println("errore HTTP")
            throw Exception("Errore HTTP: ${response.code}")
        }else{
            println("HTTP ok")

        }

        val body = response.body?.string() ?: throw Exception("Risposta vuota")
        val onlineResponse = Gson().fromJson(body, OnlineResponseDto::class.java)
        val grid = onlineResponse.newboard.grids[0]

        return Pair(
            grid.value.toBoard(),
            grid.solution.toBoard()
        )
    }
}
package io.github.breskin.asteroids

import android.content.Context
import androidx.room.Room
import io.github.breskin.asteroids.db.Database
import io.github.breskin.asteroids.db.GameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreManager(private val context: Context) {
    private val db = Room.databaseBuilder(context, Database::class.java, "database").build()

    var bestScore: Int = 0
        private set
    var bestTime: Int = 0
        private set

    init {
        GlobalScope.launch(Dispatchers.IO) {
            bestScore = db.gameResultDao().getBestScore()
            bestTime = db.gameResultDao().getBestTime()
        }
    }

    fun saveResult(score: Int, time: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            if (score > bestScore)
                bestScore = score

            if (time > bestTime)
                bestTime = time

            db.gameResultDao().insert(GameResult(score, time))
        }
    }

    fun isBestScore(score: Int) = score >= bestScore
    fun isBestTime(time: Int) = time >= bestTime
}
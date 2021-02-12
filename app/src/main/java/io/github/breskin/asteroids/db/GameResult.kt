package io.github.breskin.asteroids.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    val score: Int,
    val time: Int,

    @PrimaryKey val timestamp: Long = System.currentTimeMillis()
)

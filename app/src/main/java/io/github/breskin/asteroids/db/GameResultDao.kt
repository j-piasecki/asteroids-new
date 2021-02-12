package io.github.breskin.asteroids.db

import androidx.room.*

@Dao
interface GameResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg results: GameResult)

    @Delete
    fun delete(vararg result: GameResult)

    @Query("SELECT score FROM game_results ORDER BY score DESC LIMIT 1")
    fun getBestScore(): Int

    @Query("SELECT time FROM game_results ORDER BY time DESC LIMIT 1")
    fun getBestTime(): Int
}
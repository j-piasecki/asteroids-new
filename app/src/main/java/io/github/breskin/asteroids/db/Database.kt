package io.github.breskin.asteroids.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameResult::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
}
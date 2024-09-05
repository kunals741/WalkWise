package com.kunal.walkwise.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepsData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepsDao(): StepsDao
}
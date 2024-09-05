package com.kunal.walkwise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {

    @Insert
    suspend fun createNewUserEntry(userData: StepsData)

    @Query("SELECT steps FROM StepsData WHERE id = :id")
    fun getStepsFromUserId(id: String): Flow<Long>

    @Query("SELECT steps FROM StepsData WHERE id = :id")
    suspend fun getCurrentStepsFromUserId(id: String): Long

    @Query("SELECT NOT EXISTS(SELECT 1 FROM StepsData WHERE id = :id)")
    suspend fun isNewUserEntry(id: String): Boolean

    @Update
    suspend fun updateStepsForUser(stepsData: StepsData)
}
package com.kunal.walkwise

import android.content.Context
import androidx.room.Room
import com.kunal.walkwise.database.AppDatabase
import com.kunal.walkwise.database.StepsData
import kotlinx.coroutines.flow.Flow

class MainRepository private constructor(context: Context) {

    companion object {
        private var INSTANCE: MainRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MainRepository(context)
            }
        }

        fun get(): MainRepository =
            INSTANCE ?: throw IllegalStateException("Repository is not initialize")
    }

    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    suspend fun createNewUserEntry(id: String) {
        database.stepsDao().createNewUserEntry(
            StepsData(
                id,
                0L
            )
        )
    }

    suspend fun isNewUser(id: String): Boolean = database.stepsDao().isNewUserEntry(id)

    fun getStepsFromUserId(id: String): Flow<Long> =
        database.stepsDao().getStepsFromUserId(id)

    suspend fun getCurrentStepsFromUserId(id: String): Long =
        database.stepsDao().getCurrentStepsFromUserId(id)

    suspend fun updateStepsForUser(stepsData: StepsData) =
        database.stepsDao().updateStepsForUser(stepsData)
}
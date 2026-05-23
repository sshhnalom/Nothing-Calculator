package com.example.calculator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(entity: CalculationEntity)

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()
    
    @Query("DELETE FROM calculation_history WHERE id NOT IN (SELECT id FROM calculation_history ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun pruneHistoryToLimit(limit: Int)
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailySiteLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailySiteLogDao {
    @Query("SELECT * FROM daily_site_logs WHERE projectId = :projectId ORDER BY date DESC")
    fun getLogsForProject(projectId: Long): Flow<List<DailySiteLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailySiteLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<DailySiteLogEntity>)

    @Update
    suspend fun updateLog(log: DailySiteLogEntity)

    @Query("DELETE FROM daily_site_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM daily_site_logs WHERE projectId = :projectId")
    suspend fun deleteAllLogsForProject(projectId: Long)
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources WHERE projectId = :projectId ORDER BY id ASC")
    fun getResourcesForProject(projectId: Long): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getResourcesForProjectDirect(projectId: Long): List<ResourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: ResourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<ResourceEntity>)

    @Update
    suspend fun updateResource(resource: ResourceEntity)

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun deleteResourceById(id: Long)

    @Query("DELETE FROM resources WHERE projectId = :projectId")
    suspend fun deleteAllResourcesForProject(projectId: Long)
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityType
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityTypeDao {
    @Query("SELECT * FROM activity_types ORDER BY id ASC")
    fun getAllActivityTypes(): Flow<List<ActivityType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityType(type: ActivityType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityTypes(types: List<ActivityType>)

    @Update
    suspend fun updateActivityType(type: ActivityType)

    @Delete
    suspend fun deleteActivityType(type: ActivityType)

    @Query("DELETE FROM activity_types WHERE id = :id")
    suspend fun deleteActivityTypeById(id: Long)
}

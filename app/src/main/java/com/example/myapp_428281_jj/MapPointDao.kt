package com.example.myapp_428281_jj

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MapPointDao {
    @Query("SELECT * FROM map_points")
    fun getAllPoints(): Flow<List<MapPoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: MapPoint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<MapPoint>)

    @Query("DELETE FROM map_points")
    suspend fun deleteAll()

    @androidx.room.Delete
    suspend fun deletePoint(point: MapPoint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): Flow<Double?>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

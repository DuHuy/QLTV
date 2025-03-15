package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface DattruocDao {
    @Insert
    suspend fun insert(dattruoc: Reservation)

    @Query("SELECT * FROM dattruoc")
    fun getAllDattruocs(): Flow<List<Reservation>>

    @Query("SELECT * FROM dattruoc WHERE maDatTruoc = :maDatTruoc")
    suspend fun getDattruocById(maDatTruoc: Int): Reservation?

    @Update
    suspend fun update(dattruoc: Reservation)

    @Delete
    suspend fun delete(dattruoc: Reservation)
}
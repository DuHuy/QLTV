package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.BorrowRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MuontraDao {
    @Insert
    suspend fun insert(muontra: BorrowRecord)

    @Query("SELECT * FROM muontra")
    fun getAllMuontras(): Flow<List<BorrowRecord>>

    @Query("SELECT * FROM muontra WHERE maMuon = :maMuon")
    suspend fun getMuontraById(maMuon: Int): BorrowRecord?

    @Update
    suspend fun update(muontra: BorrowRecord)

    @Delete
    suspend fun delete(muontra: BorrowRecord)
}
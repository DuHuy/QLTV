package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface BaocaoDao {
    @Insert
    suspend fun insert(baocao: Report)

    @Query("SELECT * FROM baocao")
    fun getAllBaocaos(): Flow<List<Report>>

    @Query("SELECT * FROM baocao WHERE maBaoCao = :maBaoCao")
    suspend fun getBaocaoById(maBaoCao: Int): Report?

    @Update
    suspend fun update(baocao: Report)

    @Delete
    suspend fun delete(baocao: Report)
}
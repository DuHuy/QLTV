package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Type
import kotlinx.coroutines.flow.Flow

@Dao
interface LoaiDao {
    @Insert
    suspend fun insert(loai: Type)

    @Query("SELECT * FROM loai")
    fun getAllLoais(): Flow<List<Type>>

    @Query("SELECT * FROM loai WHERE maLoai = :maLoai")
    suspend fun getLoaiById(maLoai: Int): Type?

    @Update
    suspend fun update(loai: Type)

    @Delete
    suspend fun delete(loai: Type)
}
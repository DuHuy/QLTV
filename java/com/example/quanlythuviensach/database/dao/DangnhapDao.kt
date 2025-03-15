package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Login
import kotlinx.coroutines.flow.Flow

@Dao
interface DangnhapDao {
    @Insert
    suspend fun insert(dangnhap: Login)

    @Query("SELECT * FROM dangnhap")
    fun getAllDangnhaps(): Flow<List<Login>>

    @Query("SELECT * FROM dangnhap WHERE maDangNhap = :maDangNhap")
    suspend fun getDangnhapById(maDangNhap: Int): Login?

    @Update
    suspend fun update(dangnhap: Login)

    @Delete
    suspend fun delete(dangnhap: Login)
}
package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Type
import com.example.quanlythuviensach.models.TypeBook
import kotlinx.coroutines.flow.Flow

@Dao
interface LoaiVsSachDao {
    @Insert
    suspend fun insert(loaiVsSach: TypeBook)

    @Query("SELECT * FROM loaivssach")
    fun getAllLoaiVsSachs(): Flow<List<TypeBook>>

    @Query("SELECT * FROM loaivssach WHERE loaiMaLoai = :maLoai AND sachMaSach = :maSach")
    suspend fun getLoaiVsSach(maLoai: Int, maSach: Int): TypeBook?

    @Update
    suspend fun update(loaiVsSach: TypeBook)

    @Delete
    suspend fun delete(loaiVsSach: TypeBook)
}
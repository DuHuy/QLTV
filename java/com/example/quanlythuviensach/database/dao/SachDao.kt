package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface SachDao {
    @Insert
    suspend fun insert(sach: Book)

    @Query("SELECT * FROM sach")
    fun getAllSachs(): Flow<List<Book>>

    @Query("SELECT * FROM sach WHERE maSach = :maSach")
    suspend fun getSachById(maSach: Int): Book?

    @Update
    suspend fun update(sach: Book)

    @Delete
    suspend fun delete(sach: Book)

    @Query("SELECT * FROM sach WHERE tenSach LIKE :query OR tacGia LIKE :query")
    fun searchBooks(query: String): Flow<List<Book>>
}
package com.example.quanlythuviensach.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.quanlythuviensach.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface TaikhoanDao {
    @Query("SELECT * FROM taikhoan WHERE tenTaiKhoan = :username AND matKhau = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Insert
    suspend fun insert(taikhoan: User)

    @Query("SELECT * FROM taikhoan")
    fun getAllTaikhoans(): Flow<List<User>>

    @Query("SELECT * FROM taikhoan WHERE maNguoiDung = :maNguoiDung")
    suspend fun getTaikhoanById(maNguoiDung: Int): User?

    @Update
    suspend fun update(taikhoan: User)

    @Delete
    suspend fun delete(taikhoan: User)
}
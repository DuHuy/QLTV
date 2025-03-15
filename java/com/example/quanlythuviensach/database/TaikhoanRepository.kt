package com.example.quanlythuviensach.database
import com.example.quanlythuviensach.database.dao.TaikhoanDao
import com.example.quanlythuviensach.models.User
import kotlinx.coroutines.flow.Flow

class TaikhoanRepository(private val taikhoanDao: TaikhoanDao) {
    suspend fun login(username: String, password: String): User? = taikhoanDao.login(username, password)

    fun getAllUsers(): Flow<List<User>> = taikhoanDao.getAllTaikhoans()

    suspend fun getUserById(maNguoiDung: Int): User? = taikhoanDao.getTaikhoanById(maNguoiDung)

    suspend fun insert(user: User) = taikhoanDao.insert(user)

    suspend fun update(user: User) = taikhoanDao.update(user)

    suspend fun delete(user: User) = taikhoanDao.delete(user)
}
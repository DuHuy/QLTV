package com.example.quanlythuviensach

import android.app.Application
import androidx.room.Room
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryApp : Application() {
    companion object {
        lateinit var database: DatabaseHelper
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            DatabaseHelper::class.java,
            "library_database" // Tên cơ sở dữ liệu SQLite
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            val taikhoanDao = database.taikhoanDao()
            val existingAdmin = taikhoanDao.login("admin", "admin123")
            if (existingAdmin == null) {
                val admin = User(
                    tenTaiKhoan = "admin",
                    matKhau = "admin123",
                    vaiTro = "ADMIN",
                    ten = "Admin User",
                    email = "admin@example.com",
                    dienThoai = "0123456789",
                    ngayTao = System.currentTimeMillis()
                )
                taikhoanDao.insert(admin)
            }
        }

    }
}
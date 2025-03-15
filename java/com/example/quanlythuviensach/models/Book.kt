package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sach")
data class Book(
    @PrimaryKey(autoGenerate = true) val maSach: Int = 0, // Khóa chính
    val tenSach: String, // VARCHAR(255)
    val tacGia: String, // VARCHAR(255)
    val namXuatBan: Int, // YEAR(4)
    val tinhTrangSach: String // ENUM (giả định là chuỗi, ví dụ: "Còn sách", "Đã hết ", "Sắp có")
)
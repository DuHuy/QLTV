package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taikhoan")
data class User(
    @PrimaryKey(autoGenerate = true) val maNguoiDung: Int = 0, // Khóa chính
    val ten: String, // VARCHAR(255)
    val tenTaiKhoan: String, // VARCHAR(255)
    val matKhau: String, // VARCHAR(255)
    val email: String, // VARCHAR(100)
    val vaiTro: String, // ENUM (giả định là chuỗi, ví dụ: "Sinh viên", "ADMIN", "Giáo viên")
    val dienThoai: String, // VARCHAR(15)
    val ngayTao: Long // TIMESTAMP, lưu dưới dạng Long (giây hoặc mili giây)
)

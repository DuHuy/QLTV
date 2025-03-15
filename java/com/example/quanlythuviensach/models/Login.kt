package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dangnhap",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["maNguoiDung"],
            childColumns = ["maNguoiDung"],
            onDelete = ForeignKey.CASCADE, // Xóa bản ghi con khi bản ghi cha bị xóa
            onUpdate = ForeignKey.CASCADE // Cập nhật bản ghi con khi bản ghi cha thay đổi
        )
    ],
    indices = [Index(value = ["maNguoiDung"])]
)
data class Login(
    @PrimaryKey(autoGenerate = true) val maDangNhap: Int = 0, // Khóa chính
    val maNguoiDung: Int, // Khóa ngoại tham chiếu đến taikhoan
    val timestamp: Long // TIMESTAMP, lưu dưới dạng Long
)

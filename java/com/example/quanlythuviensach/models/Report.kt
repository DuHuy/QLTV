package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "baocao",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["maNguoiDung"],
            childColumns = ["maNguoiDung"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["maNguoiDung"])]
    )
data class Report(
    @PrimaryKey(autoGenerate = true) val maBaoCao: Int = 0, // Khóa chính
    val maNguoiDung: Int, // Khóa ngoại tham chiếu đến taikhoan
    val loaiBaoCao: String, // ENUM (giả định là chuỗi, ví dụ: "số sách đã được mượn/trả/quá hạn", "Tổng quan hoạt động mượn trả sách, số lượng người dùng", "...")
    val noiDung: String, // TEXT
    val ngayTao: Long // TIMESTAMP, lưu dưới dạng Long
)

package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dattruoc",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["maNguoiDung"],
            childColumns = ["maNguoiDung"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["maSach"],
            childColumns = ["maSach"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["maSach"]), // Thêm chỉ mục cho cột maSach
        Index(value = ["maNguoiDung"]) // Thêm chỉ mục cho cột maNguoiDung
    ]

)
data class Reservation(
    @PrimaryKey(autoGenerate = true) val maDatTruoc: Int = 0, // Khóa chính
    val maNguoiDung: Int, // Khóa ngoại tham chiếu đến taikhoan
    val maSach: Int, // Khóa ngoại tham chiếu đến sach
    val ngayDatTruoc: Long, // TIMESTAMP, lưu dưới dạng Long
    val trangThaiDat: String // ENUM (giả định là chuỗi, ví dụ: "PENDING", "CONFIRMED", "CANCELLED")
)

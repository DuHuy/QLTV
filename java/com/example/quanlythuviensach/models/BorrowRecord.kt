package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "muontra",
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
data class BorrowRecord(
    @PrimaryKey(autoGenerate = true) val maMuon: Int = 0, // Khóa chính
    val maNguoiDung: Int, // Khóa ngoại tham chiếu đến taikhoan
    val maSach: Int, // Khóa ngoại tham chiếu đến sach
    val trangThaiMuonTra: String, // ENUM (giả định là chuỗi, ví dụ: "Đăng mượn", "Đã trả", "Chưa trả")
    val ngayMuon: Long, // DATETIME, lưu dưới dạng Long
    val ngayTra: Long?, // DATETIME, có thể null, lưu dưới dạng Long
    val dienThoai: String // VARCHAR(15)
)

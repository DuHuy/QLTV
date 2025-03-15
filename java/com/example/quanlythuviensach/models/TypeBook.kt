package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "loaivssach",
    foreignKeys = [
        ForeignKey(
            entity = Type::class,
            parentColumns = ["maLoai"],
            childColumns = ["loaiMaLoai"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["maSach"],
            childColumns = ["sachMaSach"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sachMaSach"])],
    primaryKeys = ["loaiMaLoai", "sachMaSach"])
data class TypeBook(
    val loaiMaLoai: Int, // Khóa ngoại tham chiếu đến loai
    val sachMaSach: Int // Khóa ngoại tham chiếu đến sach
)

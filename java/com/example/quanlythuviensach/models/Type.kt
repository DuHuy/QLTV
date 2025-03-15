package com.example.quanlythuviensach.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loai")
data class Type(
    @PrimaryKey(autoGenerate = true) val maLoai: Int = 0, // Khóa chính
    val tenLoai: String // VARCHAR(255)
)
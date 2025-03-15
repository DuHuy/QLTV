package com.example.quanlythuviensach.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quanlythuviensach.models.* // Đảm bảo import tất cả các thực thể
import com.example.quanlythuviensach.database.dao.* // Đảm bảo import tất cả các DAO

@Database(
    entities = [
        User::class,
        Login::class,
        Reservation::class,
        BorrowRecord::class,
        Book::class,
        TypeBook::class,
        Type::class,
        Report::class
    ],
    version = 1, // Phiên bản cơ sở dữ liệu, tăng lên khi có thay đổi schema
    exportSchema = true
)
abstract class DatabaseHelper : RoomDatabase() {

    // Khai báo các DAO tương ứng với từng thực thể
    abstract fun taikhoanDao(): TaikhoanDao
    abstract fun dangnhapDao(): DangnhapDao
    abstract fun dattruocDao(): DattruocDao
    abstract fun muontraDao(): MuontraDao
    abstract fun sachDao(): SachDao
    abstract fun loaiVsSachDao(): LoaiVsSachDao
    abstract fun loaiDao(): LoaiDao
    abstract fun baocaoDao(): BaocaoDao

    // (Tùy chọn) Singleton pattern để đảm bảo chỉ có một instance của database
    companion object {
        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getDatabase(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHelper::class.java,
                    "library_database" // Tên cơ sở dữ liệu SQLite
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO Taikhoan (tenTaiKhoan, matKhau, email, vaiTro, dienThoai, ngayTao) VALUES ('Admin User', 'admin123', 'admin@example.co', 'ADMIN', '0123456789', 174116106189)")                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.quanlythuviensach.database

import com.example.quanlythuviensach.database.dao.SachDao
import com.example.quanlythuviensach.models.Book
import kotlinx.coroutines.flow.Flow

class BookRepository(private val sachDao: SachDao) {
    suspend fun insert(book: Book) = sachDao.insert(book)

    fun getAllBooks(): Flow<List<Book>> = sachDao.getAllSachs()

    suspend fun getBookById(bookId: Int): Book? = sachDao.getSachById(bookId)

    suspend fun update(book: Book) = sachDao.update(book)

    suspend fun delete(book: Book) = sachDao.delete(book)

    fun searchBooks(query: String): Flow<List<Book>> = sachDao.searchBooks("%$query%")
}
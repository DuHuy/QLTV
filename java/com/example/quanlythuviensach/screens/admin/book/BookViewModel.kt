package com.example.quanlythuviensach.screens.admin.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.BookRepository
import com.example.quanlythuviensach.models.Book // Sử dụng thực thể Book từ models
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllBooks().collect { bookList ->
                _books.value = bookList
            }
            _isLoading.value = false
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.insert(book)
            loadBooks() // Cập nhật danh sách sau khi thêm
            _isLoading.value = false
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.update(book)
            loadBooks() // Cập nhật danh sách sau khi sửa
            _isLoading.value = false
        }
    }

    fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val book = repository.getBookById(bookId)
            book?.let { repository.delete(it) }
            loadBooks() // Cập nhật danh sách sau khi xóa
            _isLoading.value = false
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchBooks(query).collect { bookList ->
                _books.value = bookList
            }
            _isLoading.value = false
        }
    }
    fun getBookById(bookId: Int): StateFlow<Book?> {
        val bookFlow = MutableStateFlow<Book?>(null)
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val book = repository.getBookById(bookId)
                bookFlow.value = book
                if (book == null) {
                    _errorMessage.value = "Không tìm thấy sách với ID: $bookId"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi lấy thông tin sách: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        return bookFlow.asStateFlow()
    }

    companion object {
        fun provideFactory(repository: BookRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
                    return BookViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
package com.example.quanlythuviensach.screens.admin.borrowreturn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.MuontraRepository
import com.example.quanlythuviensach.models.BorrowRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class BorrowViewModel(private val repository: MuontraRepository) : ViewModel() {
    private val _borrowRecords = MutableStateFlow<List<BorrowRecord>>(emptyList())
    val borrowRecords: StateFlow<List<BorrowRecord>> = _borrowRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadBorrowRecords()
    }

    fun loadBorrowRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _borrowRecords.value = repository.getAllBorrowRecords().first()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi tải danh sách mượn/trả: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addBorrowRecord(borrowRecord: BorrowRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insert(borrowRecord)
                loadBorrowRecords()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi thêm phiếu mượn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBorrowRecord(borrowRecord: BorrowRecord) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(borrowRecord)
                loadBorrowRecords()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi cập nhật phiếu mượn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBorrowRecord(maMuon: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val borrowRecord = repository.getBorrowRecordById(maMuon)
                borrowRecord?.let { repository.delete(it) }
                loadBorrowRecords()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi xóa phiếu mượn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBorrowRecordById(maMuon: Int): StateFlow<BorrowRecord?> {
        val borrowFlow = MutableStateFlow<BorrowRecord?>(null)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                borrowFlow.value = repository.getBorrowRecordById(maMuon)
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi lấy thông tin phiếu mượn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        return borrowFlow.asStateFlow()
    }

    companion object {
        fun provideFactory(repository: MuontraRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BorrowViewModel::class.java)) {
                    return BorrowViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
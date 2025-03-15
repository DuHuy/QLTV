package com.example.quanlythuviensach.screens.admin.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.TaikhoanRepository
import com.example.quanlythuviensach.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(private val repository: TaikhoanRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _users.value = repository.getAllUsers().first() // Chuyển Flow<List<User>> thành List
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi tải danh sách người dùng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insert(user)
                loadUsers() // Cập nhật danh sách
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi thêm người dùng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(user)
                loadUsers() // Cập nhật danh sách
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi cập nhật người dùng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(maNguoiDung: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getUserById(maNguoiDung)
                user?.let { repository.delete(it) }
                loadUsers() // Cập nhật danh sách
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi xóa người dùng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserById(maNguoiDung: Int): StateFlow<User?> {
        val userFlow = MutableStateFlow<User?>(null)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userFlow.value = repository.getUserById(maNguoiDung)
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi lấy thông tin người dùng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        return userFlow.asStateFlow()
    }

    companion object {
        fun provideFactory(repository: TaikhoanRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    return UserViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
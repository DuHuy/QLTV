package com.example.quanlythuviensach.screens.admin.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.TaikhoanRepository
import com.example.quanlythuviensach.models.User
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: TaikhoanRepository) : ViewModel() {
    fun login(username: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repository.login(username, password)
                onResult(user)
            } catch (e: Exception) {
                onResult(null) // Trả về null nếu có lỗi
            }
        }
    }
    companion object {
        fun provideFactory(repository: TaikhoanRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
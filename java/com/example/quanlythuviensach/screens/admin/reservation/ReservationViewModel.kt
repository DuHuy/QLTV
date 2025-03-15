package com.example.quanlythuviensach.screens.admin.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.DattruocRepository
import com.example.quanlythuviensach.models.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class ReservationViewModel(private val repository: DattruocRepository) : ViewModel() {
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadReservations()
    }

    fun loadReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _reservations.value = repository.getAllReservations().first()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi tải danh sách đặt trước: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReservation(reservation: Reservation) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insert(reservation)
                loadReservations()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi thêm yêu cầu đặt trước: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReservation(reservation: Reservation) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.update(reservation)
                loadReservations()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi cập nhật yêu cầu đặt trước: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReservation(maDatTruoc: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reservation = repository.getReservationById(maDatTruoc)
                reservation?.let { repository.delete(it) }
                loadReservations()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi xóa yêu cầu đặt trước: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(repository: DattruocRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
                    return ReservationViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
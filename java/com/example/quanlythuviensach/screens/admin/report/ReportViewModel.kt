package com.example.quanlythuviensach.screens.admin.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quanlythuviensach.database.BaocaoRepository
import com.example.quanlythuviensach.models.Report
import com.example.quanlythuviensach.screens.admin.user.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowViewModel

class ReportViewModel(
    private val repository: BaocaoRepository,
    private val borrowViewModel: BorrowViewModel,
    private val userViewModel: UserViewModel
) : ViewModel() {
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _reportContent = MutableStateFlow<String?>(null)
    val reportContent: StateFlow<String?> = _reportContent.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _reports.value = repository.getAllReports().first()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi tải danh sách báo cáo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateReport(reportType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                when (reportType) {
                    "Số sách đã được mượn/trả/quá hạn" -> {
                        val borrowRecords = borrowViewModel.borrowRecords.first()
                        val soNguoiMuon = borrowRecords.count { it.trangThaiMuonTra == "Đang mượn" }
                        val soNguoiTra = borrowRecords.count { it.trangThaiMuonTra == "Đã trả" }
                        val soNguoiQuaHan = borrowRecords.count {
                            val dueDate = it.ngayMuon + 14 * 24 * 60 * 60 * 1000L // Giả sử hạn trả là 14 ngày
                            it.trangThaiMuonTra == "Đang mượn" && System.currentTimeMillis() > dueDate
                        }
                        _reportContent.value = """
                            Báo cáo: Số sách đã được mượn/trả/quá hạn
                            - Số người đang mượn: $soNguoiMuon
                            - Số người đã trả: $soNguoiTra
                            - Số người quá hạn: $soNguoiQuaHan
                        """.trimIndent()
                    }
                    "Tổng quan hoạt động mượn trả sách, số lượng người dùng" -> {
                        val borrowRecords = borrowViewModel.borrowRecords.first()
                        val users = userViewModel.users.first()
                        val tongSoNguoiDung = users.size
                        val tongSoNguoiMuon = borrowRecords.count { it.trangThaiMuonTra == "Đang mượn" }
                        val tongSoNguoiTra = borrowRecords.count { it.trangThaiMuonTra == "Đã trả" }
                        _reportContent.value = """
                            Báo cáo: Tổng quan hoạt động mượn trả sách, số lượng người dùng
                            - Tổng số người dùng: $tongSoNguoiDung
                            - Tổng số người đang mượn: $tongSoNguoiMuon
                            - Tổng số người đã trả: $tongSoNguoiTra
                        """.trimIndent()
                    }
                    "Số lượng yêu cầu đặt trước theo trạng thái" -> {
                        _reportContent.value = "Chưa triển khai do thiếu dữ liệu đặt trước."
                    }
                    else -> _reportContent.value = "Loại báo cáo không hợp lệ."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi tạo báo cáo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveReport(maNguoiDung: Int, loaiBaoCao: String, noiDung: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newReport = Report(
                    maBaoCao = 0,
                    maNguoiDung = maNguoiDung,
                    loaiBaoCao = loaiBaoCao,
                    noiDung = noiDung,
                    ngayTao = System.currentTimeMillis()
                )
                repository.insert(newReport)
                loadReports()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi lưu báo cáo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: BaocaoRepository,
            borrowViewModel: BorrowViewModel,
            userViewModel: UserViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
                    return ReportViewModel(repository, borrowViewModel, userViewModel) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
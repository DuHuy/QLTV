package com.example.quanlythuviensach.screens.admin.report

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlythuviensach.database.BaocaoRepository
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.TaikhoanRepository
import com.example.quanlythuviensach.database.MuontraRepository
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowViewModel
import com.example.quanlythuviensach.screens.admin.user.UserViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportStatisticsScreen(navController: NavController) {
    val context = LocalContext.current
    // Khởi tạo UserViewModel
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            TaikhoanRepository(DatabaseHelper.getDatabase(context).taikhoanDao())
        )
    )
    // Khởi tạo BorrowViewModel
    val borrowViewModel: BorrowViewModel = viewModel(
        factory = BorrowViewModel.provideFactory(
            MuontraRepository(DatabaseHelper.getDatabase(context).muontraDao())
        )
    )
    // Khởi tạo ReportViewModel với các dependency
    val reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModel.provideFactory(
            repository = BaocaoRepository(DatabaseHelper.getDatabase(context).baocaoDao()),
            borrowViewModel = borrowViewModel,
            userViewModel = userViewModel
        )
    )

    val users by userViewModel.users.collectAsState()
    val reportContent by reportViewModel.reportContent.collectAsState()
    val isLoading by reportViewModel.isLoading.collectAsState()
    val errorMessage by reportViewModel.errorMessage.collectAsState()

    var selectedUser by remember { mutableStateOf<com.example.quanlythuviensach.models.User?>(null) }
    var selectedReportType by remember { mutableStateOf("") }
    var expandedUser by remember { mutableStateOf(false) }
    var expandedReportType by remember { mutableStateOf(false) }

    val reportTypes = listOf(
        "Số sách đã được mượn/trả/quá hạn",
        "Tổng quan hoạt động mượn trả sách, số lượng người dùng",
        "Số lượng yêu cầu đặt trước theo trạng thái"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Phần chọn loại báo cáo và người tạo
        ExposedDropdownMenuBox(
            expanded = expandedUser,
            onExpandedChange = { expandedUser = it }
        ) {
            OutlinedTextField(
                value = selectedUser?.ten ?: "Chọn người tạo báo cáo",
                onValueChange = {},
                label = { Text("Chọn người tạo báo cáo") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUser)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedUser,
                onDismissRequest = { expandedUser = false }
            ) {
                users.forEach { user ->
                    DropdownMenuItem(
                        onClick = {
                            selectedUser = user
                            expandedUser = false
                        }
                    ) {
                        Text(text = user.ten)
                    }
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedReportType,
            onExpandedChange = { expandedReportType = it }
        ) {
            OutlinedTextField(
                value = selectedReportType.takeIf { it.isNotEmpty() } ?: "Chọn loại báo cáo",
                onValueChange = {},
                label = { Text("Chọn loại báo cáo") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReportType)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedReportType,
                onDismissRequest = { expandedReportType = false }
            ) {
                reportTypes.forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            selectedReportType = type
                            expandedReportType = false
                        }
                    ) {
                        Text(text = type)
                    }
                }
            }
        }

        // Nút tạo báo cáo
        Button(
            onClick = {
                if (selectedReportType.isNotEmpty()) {
                    reportViewModel.generateReport(selectedReportType)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text("Tạo báo cáo", color = Color.White)
        }

        // Khu vực hiển thị báo cáo
        Box(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Lỗi không xác định",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (reportContent != null) {
                Text(
                    text = reportContent ?: "Chưa có báo cáo",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            } else {
                Text(
                    text = "Vui lòng chọn loại báo cáo và nhấn 'Tạo báo cáo'.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Nút lưu báo cáo
        if (reportContent != null) {
            Button(
                onClick = {
                    if (selectedUser == null || selectedReportType.isEmpty()) return@Button
                    reportViewModel.saveReport(
                        maNguoiDung = selectedUser!!.maNguoiDung,
                        loaiBaoCao = selectedReportType,
                        noiDung = reportContent!!
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Lưu báo cáo", color = Color.White)
            }
        }
    }
}
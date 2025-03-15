package com.example.quanlythuviensach.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quanlythuviensach.database.BaocaoRepository
import com.example.quanlythuviensach.database.BookRepository
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.DattruocRepository
import com.example.quanlythuviensach.database.MuontraRepository
import com.example.quanlythuviensach.database.TaikhoanRepository

import com.example.quanlythuviensach.screens.admin.book.BookManagementScreen
import com.example.quanlythuviensach.screens.admin.book.BookViewModel
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowViewModel
import com.example.quanlythuviensach.screens.admin.user.UserManagementScreen
import com.example.quanlythuviensach.screens.admin.user.UserViewModel
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowReturnManagementScreen
import com.example.quanlythuviensach.screens.admin.navigation.NavGraph
import com.example.quanlythuviensach.screens.admin.report.ReportViewModel
import com.example.quanlythuviensach.screens.admin.report.ReportStatisticsScreen
import com.example.quanlythuviensach.screens.admin.reservation.ReservationViewModel
import com.example.quanlythuviensach.screens.admin.reservation.ReservationManagementScreen


@Composable
fun AdminDashboard(navController: NavHostController) {

    val menuItems = listOf(
        "Danh mục sách", "Tài khoản", "Mượn/trả", "Đặt trước", "Thống kê"
    )
    var selectedMenuItem by remember { mutableStateOf(menuItems[0]) }

    Scaffold(
        topBar = { AdminTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // Menu ngang
            HorizontalMenu(menuItems, selectedMenuItem) { selectedMenuItem = it }

            // Nội dung chính chiếm toàn bộ phần còn lại
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                ContentArea(selectedMenuItem, navController)
            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdminTopBar() {
    TopAppBar(
        title = { Text("Admin Dashboard") },
        backgroundColor = Color(0xAE35A4EE),
        contentColor = Color.White

    )
}

@Composable
fun HorizontalMenu(menuItems: List<String>, selectedItem: String, onMenuSelected: (String) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = menuItems.indexOf(selectedItem),
        backgroundColor = Color(0xAE35A4EE),
        contentColor = Color.White,
        edgePadding = 8.dp
    ) {
        menuItems.forEach { item ->
            Tab(
                selected = item == selectedItem,
                onClick = { onMenuSelected(item) },
                text = { Text(item, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun ContentArea(selectedMenuItem: String, navController: NavHostController) { // Xóa tham số navController không cần thiết
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(
            TaikhoanRepository(DatabaseHelper.getDatabase(context).taikhoanDao())
        )
    )
    val borrowViewModel: BorrowViewModel = viewModel(
        factory = BorrowViewModel.provideFactory(
            MuontraRepository(DatabaseHelper.getDatabase(context).muontraDao())
        )
    )
    when (selectedMenuItem) {
        "Danh mục sách" -> {
            val context = LocalContext.current
            val sachDao = DatabaseHelper.getDatabase(context).sachDao()
            val repository = BookRepository(sachDao)
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(repository))
            BookManagementScreen(viewModel = viewModel, navController = navController)
        } // Gọi màn hình quản lý sách
        "Tài khoản" -> {
            val taikhoanDao = DatabaseHelper.getDatabase(context).taikhoanDao()
            val repository = TaikhoanRepository(taikhoanDao)
            val viewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(repository))
            UserManagementScreen(navController = navController, viewModel = viewModel)
        }
        "Mượn/trả" -> {
            val muontraDao = DatabaseHelper.getDatabase(context).muontraDao()
            val repository = MuontraRepository(muontraDao)
            val viewModel: BorrowViewModel = viewModel(factory = BorrowViewModel.provideFactory(repository))
            BorrowReturnManagementScreen(navController = navController, viewModel = viewModel)
        }
        "Đặt trước" -> {
            val dattruocDao = DatabaseHelper.getDatabase(context).dattruocDao()
            val repository = DattruocRepository(dattruocDao)
            val viewModel: ReservationViewModel = viewModel(factory = ReservationViewModel.provideFactory(repository))
            ReservationManagementScreen(navController = navController, viewModel = viewModel)
        }
        "Thống kê" -> {
            val baocaoDao = DatabaseHelper.getDatabase(context).baocaoDao()
            val repository = BaocaoRepository(baocaoDao)
            val reportViewModel: ReportViewModel = viewModel(
                factory = ReportViewModel.provideFactory(
                    repository = repository,
                    borrowViewModel = borrowViewModel,
                    userViewModel = userViewModel
                )
            )
            ReportStatisticsScreen(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboard() {
    AdminDashboard(navController = rememberNavController())
}
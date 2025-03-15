package com.example.quanlythuviensach.screens.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.Text
import com.example.quanlythuviensach.database.BaocaoRepository
import com.example.quanlythuviensach.database.BookRepository
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.DattruocRepository
import com.example.quanlythuviensach.database.MuontraRepository
import com.example.quanlythuviensach.database.TaikhoanRepository
import com.example.quanlythuviensach.database.dao.SachDao
import com.example.quanlythuviensach.screens.admin.AdminDashboard
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowReturnManagementScreen
import com.example.quanlythuviensach.screens.admin.UserDetailScreen
import com.example.quanlythuviensach.screens.admin.auth.LoginScreen
import com.example.quanlythuviensach.screens.admin.user.UserManagementScreen
import com.example.quanlythuviensach.screens.admin.book.BookManagementScreen
//import com.example.quanlythuviensach.screens.admin.auth.LoginScreen
import com.example.quanlythuviensach.screens.admin.book.BookDetailScreen
import com.example.quanlythuviensach.screens.admin.book.BookViewModel
import com.example.quanlythuviensach.screens.admin.borrowreturn.BorrowViewModel
import com.example.quanlythuviensach.screens.admin.report.ReportStatisticsScreen
import com.example.quanlythuviensach.screens.admin.report.ReportViewModel
import com.example.quanlythuviensach.screens.admin.reservation.ReservationViewModel
import com.example.quanlythuviensach.screens.admin.reservation.ReservationManagementScreen
import com.example.quanlythuviensach.screens.admin.user.UserViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("admin_dashboard") {
            AdminDashboard(navController = navController)
        }
        composable("book_management") {
            val context = LocalContext.current
            val sachDao = DatabaseHelper.getDatabase(context).sachDao()
            val repository = BookRepository(sachDao)
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(repository))
            BookManagementScreen(viewModel = viewModel, navController = navController)
        }
        composable("book_detail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toInt() ?: 0
            val context = LocalContext.current
            val sachDao = DatabaseHelper.getDatabase(context).sachDao()
            val repository = BookRepository(sachDao)
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(repository))
            BookDetailScreen(bookId = bookId, navController = navController, viewModel = viewModel)
        }
        composable("advanced_search") {
            Text("Tìm kiếm nâng cao - Chưa triển khai")
        }

        composable("filter_books") {
            Text("Lọc sách - Chưa triển khai")
        }

        composable("user_detail/{maNguoiDung}") { backStackEntry ->
            val maNguoiDung = backStackEntry.arguments?.getString("maNguoiDung")?.toInt() ?: 0
            val context = LocalContext.current
            val taikhoanDao = DatabaseHelper.getDatabase(context).taikhoanDao()
            val repository = TaikhoanRepository(taikhoanDao)
            val viewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(repository))
            UserDetailScreen(maNguoiDung = maNguoiDung, navController = navController, viewModel = viewModel)
        }

        composable("borrow_management") {
            val context = LocalContext.current
            val muontraDao = DatabaseHelper.getDatabase(context).muontraDao()
            val repository = MuontraRepository(muontraDao)
            val viewModel: BorrowViewModel = viewModel(factory = BorrowViewModel.provideFactory(repository))
            BorrowReturnManagementScreen(navController = navController, viewModel = viewModel)
        }

        composable("reservation_management") {
            val context = LocalContext.current
            val dattruocDao = DatabaseHelper.getDatabase(context).dattruocDao()
            val repository = DattruocRepository(dattruocDao)
            val viewModel: ReservationViewModel = viewModel(factory = ReservationViewModel.provideFactory(repository))
            ReservationManagementScreen(navController = navController, viewModel = viewModel)
        }
        composable("report_statistics") {
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
            // Khởi tạo ReportViewModel với đầy đủ tham số
            val baocaoDao = DatabaseHelper.getDatabase(context).baocaoDao()
            val repository = BaocaoRepository(baocaoDao)
            val reportViewModel: ReportViewModel = viewModel(
                factory = ReportViewModel.provideFactory(
                    repository = repository,
                    borrowViewModel = borrowViewModel,
                    userViewModel = userViewModel
                )
            )
            // Xóa tham số viewModel vì ReportStatisticsScreen không nhận tham số này
            ReportStatisticsScreen(navController = navController)
        }
    }
}
package com.example.quanlythuviensach.screens.admin.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quanlythuviensach.R
import com.example.quanlythuviensach.models.Book

@Composable
fun BookDetailScreen(bookId: Int, navController: NavHostController, viewModel: BookViewModel) {
    val bookState by viewModel.getBookById(bookId).collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sách") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo_background),
                            contentDescription = "Quay lại"
                        )
                    }
                },
                backgroundColor = Color(0xAE35A4EE),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Lỗi không xác định",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (bookState != null) {
                val book = bookState!!
                // Hiển thị thông tin chi tiết sách
                Image(
                    painter = painterResource(id = R.mipmap.logodhtn),
                    contentDescription = "Biểu tượng sách",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = book.tenSach,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Tác giả: ${book.tacGia}",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Năm xuất bản: ${book.namXuatBan}",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Tình trạng: ${book.tinhTrangSach}",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Nút chỉnh sửa và xóa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteBook(book.maSach)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Xóa", color = Color.White)
                    }
                }
            }
        }
    }
}
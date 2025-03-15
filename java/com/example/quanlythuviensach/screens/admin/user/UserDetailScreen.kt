package com.example.quanlythuviensach.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quanlythuviensach.R
import com.example.quanlythuviensach.models.User
import com.example.quanlythuviensach.screens.admin.user.UserViewModel
import java.util.Date

@Composable
fun UserDetailScreen(navController: NavController, maNguoiDung: Int, viewModel: UserViewModel) {
    val userState by viewModel.getUserById(maNguoiDung).collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin người dùng") },
                backgroundColor = Color.White,
                contentColor = Color.Black
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage ?: "Lỗi không xác định", color = Color.Red)
            } else if (userState != null) {
                val user = userState!!
                Image(
                    painter = painterResource(id = R.mipmap.user_foreground),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = user.ten, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Tên tài khoản: ${user.tenTaiKhoan}", fontSize = 16.sp, color = Color.Gray)
                Text(text = "Email: ${user.email}", fontSize = 16.sp, color = Color.Gray)
                Text(text = "Vai trò: ${user.vaiTro}", fontSize = 16.sp, color = Color.Gray)
                Text(text = "Điện thoại: ${user.dienThoai}", fontSize = 16.sp, color = Color.Gray)
                Text(text = "Ngày tạo: ${Date(user.ngayTao)}", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("Quay lại", color = Color.White)
                }
            }
        }
    }
}
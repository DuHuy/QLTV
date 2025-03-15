package com.example.quanlythuviensach.screens.admin.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.TaikhoanRepository

@Composable
fun LoginScreen(navController: NavController) {
    val repository = TaikhoanRepository(DatabaseHelper.getDatabase(LocalContext.current).taikhoanDao())
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(repository))
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Text(
            text = "Đăng nhập",
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create an account",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nhập tài khoản") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nhập mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị thông báo lỗi nếu có
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(username, password) { user ->
                    Log.d("LoginScreen", "User from login: $user")
                    if (user != null && user.vaiTro == "ADMIN") {
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true // Đảm bảo không tạo nhiều instance của admin_dashboard
                        }
                        errorMessage = "Đăng nhập thành công"
                    } else {
                        errorMessage = "Tài khoản hoặc mật khẩu không đúng, hoặc không phải admin!"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text("Đăng nhập", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bằng cách nhấp vào Đăng Nhập, đồng nghĩa bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của chúng tôi",
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}
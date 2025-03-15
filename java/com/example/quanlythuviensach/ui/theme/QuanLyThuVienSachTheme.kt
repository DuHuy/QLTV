package com.example.quanlythuviensach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple80 = Color(0xFFD0BCFF) // Khởi tạo màu Purple80
private val PurpleGrey80 = Color(0xFFCCC2DC) // Khởi tạo màu PurpleGrey80
private val Pink80 = Color(0xFFEFB8C8) // Khởi tạo màu Pink80

private val Purple40 = Color(0xFF6650a4) // Khởi tạo màu Purple40
private val PurpleGrey40 = Color(0xFF625b71) // Khởi tạo màu PurpleGrey40
private val Pink40 = Color(0xFF7D5260) // Khởi tạo màu Pink40

// Định nghĩa Typography (nếu cần)
private val Typography = Typography() // Sử dụng Typography từ Material3

private val DarkColorScheme = darkColors(
    primary = Purple80, // Màu chính cho dark theme
    secondary = PurpleGrey80, // Màu phụ cho dark theme
    // Có thể thêm primaryVariant, secondaryVariant nếu cần:
    primaryVariant = Pink80, // Tái sử dụng Pink80 cho variant
    secondaryVariant = Pink80 // Tái sử dụng Pink80 cho variant
)

private val LightColorScheme = lightColors(
    primary = Purple40, // Màu chính cho light theme
    secondary = PurpleGrey40, // Màu phụ cho light theme
    // Có thể thêm primaryVariant, secondaryVariant nếu cần:
    primaryVariant = Pink40, // Tái sử dụng Pink40 cho variant
    secondaryVariant = Pink40 // Tái sử dụng Pink40 cho variant
)

@Composable
fun QuanLyThuVienSachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
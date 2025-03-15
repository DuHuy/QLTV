package com.example.quanlythuviensach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.quanlythuviensach.screens.admin.navigation.NavGraph
import com.example.quanlythuviensach.ui.theme.QuanLyThuVienSachTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuanLyThuVienSachTheme {
                 MyApp()
            }
        }
    }
}
@Composable
fun MyApp(){
    val navController = rememberNavController()
    NavGraph(navController = navController)}
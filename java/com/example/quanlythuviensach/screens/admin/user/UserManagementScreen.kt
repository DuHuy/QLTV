package com.example.quanlythuviensach.screens.admin.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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

@Composable
fun UserManagementScreen(navController: NavController, viewModel: UserViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Danh sách", "Giáo viên", "Sinh viên")
    var searchQuery by remember { mutableStateOf("") }

    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddUserDialog by remember { mutableStateOf(false) }
    var showEditUserDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Tabs(tabs, selectedTab) { selectedTab = it }

        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tìm kiếm theo tên") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Hiển thị danh sách hoặc trạng thái
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Lỗi không xác định",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val filteredUsers = when (selectedTab) {
                    0 -> users // Hiển thị tất cả khi tab "Danh sách"
                    1 -> users.filter { it.vaiTro == "Giáo viên" } // Hiển thị giáo viên
                    2 -> users.filter { it.vaiTro == "Sinh viên" } // Hiển thị sinh viên
                    else -> users
                }.filter {
                    it.ten.contains(searchQuery, ignoreCase = true)
                }
                UserList(filteredUsers, navController, viewModel, { user ->
                    selectedUser = user
                    showEditUserDialog = true
                })
            }
        }

        // Nút chức năng (chỉ giữ nút "Thêm")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showAddUserDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Thêm", color = Color.White)
            }
        }
    }

    // Gọi AddUserDialog
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onConfirm = { user ->
                viewModel.addUser(user)
                showAddUserDialog = false
            }
        )
    }

    // Gọi EditUserDialog
    if (showEditUserDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = {
                showEditUserDialog = false
                selectedUser = null
            },
            onConfirm = { updatedUser ->
                viewModel.updateUser(updatedUser)
                showEditUserDialog = false
                selectedUser = null
            }
        )
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onConfirm: (User) -> Unit) {
    var ten by remember { mutableStateOf("") }
    var tenTaiKhoan by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var vaiTro by remember { mutableStateOf("Sinh viên, Giáo viên") }
    var dienThoai by remember { mutableStateOf("") }
    var ngayTao by remember { mutableStateOf(System.currentTimeMillis()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm người dùng") },
        text = {
            Column {
                OutlinedTextField(
                    value = ten,
                    onValueChange = { ten = it },
                    label = { Text("Tên") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = tenTaiKhoan,
                    onValueChange = { tenTaiKhoan = it },
                    label = { Text("Tên tài khoản") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = matKhau,
                    onValueChange = { matKhau = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = vaiTro,
                    onValueChange = { vaiTro = it },
                    label = { Text("Vai trò") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = dienThoai,
                    onValueChange = { dienThoai = it },
                    label = { Text("Điện thoại") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newUser = User(
                        maNguoiDung = 0,
                        ten = ten,
                        tenTaiKhoan = tenTaiKhoan,
                        matKhau = matKhau,
                        email = email,
                        vaiTro = vaiTro,
                        dienThoai = dienThoai,
                        ngayTao = ngayTao
                    )
                    onConfirm(newUser)
                }
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun EditUserDialog(user: User, onDismiss: () -> Unit, onConfirm: (User) -> Unit) {
    var ten by remember { mutableStateOf(user.ten) }
    var tenTaiKhoan by remember { mutableStateOf(user.tenTaiKhoan) }
    var matKhau by remember { mutableStateOf(user.matKhau) }
    var email by remember { mutableStateOf(user.email) }
    var vaiTro by remember { mutableStateOf(user.vaiTro) }
    var dienThoai by remember { mutableStateOf(user.dienThoai) }
    var ngayTao by remember { mutableStateOf(user.ngayTao) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật người dùng") },
        text = {
            Column {
                OutlinedTextField(
                    value = ten,
                    onValueChange = { ten = it },
                    label = { Text("Tên") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = tenTaiKhoan,
                    onValueChange = { tenTaiKhoan = it },
                    label = { Text("Tên tài khoản") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = matKhau,
                    onValueChange = { matKhau = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = vaiTro,
                    onValueChange = { vaiTro = it },
                    label = { Text("Vai trò") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = dienThoai,
                    onValueChange = { dienThoai = it },
                    label = { Text("Điện thoại") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedUser = user.copy(
                        ten = ten,
                        tenTaiKhoan = tenTaiKhoan,
                        matKhau = matKhau,
                        email = email,
                        vaiTro = vaiTro,
                        dienThoai = dienThoai,
                        ngayTao = ngayTao
                    )
                    onConfirm(updatedUser)
                }
            ) {
                Text("Cập nhật")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun Tabs(tabs: List<String>, selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            Button(
                onClick = { onTabSelected(index) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedTab == index) Color.Black else Color.LightGray,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(title)
            }
        }
    }
}

@Composable
fun UserList(users: List<User>, navController: NavController, viewModel: UserViewModel, onEditClick: (User) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(users) { user ->
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc muốn xóa ${user.ten}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteUser(user.maNguoiDung)
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Xác nhận")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }

            UserItem(
                user = user,
                onClick = { navController.navigate("user_detail/${user.maNguoiDung}") },
                onEditClick = { onEditClick(user) },
                onDeleteClick = { showDeleteDialog = true }
            )
        }
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp)) {
            Image(
                painter = painterResource(id = R.mipmap.user_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(user.ten, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(user.vaiTro, fontSize = 14.sp, color = Color.Gray)
        }
        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text("Cập nhật", color = Color.White, fontSize = 12.sp)
        }
        IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red, modifier = Modifier.size(16.dp))
        }
    }
}
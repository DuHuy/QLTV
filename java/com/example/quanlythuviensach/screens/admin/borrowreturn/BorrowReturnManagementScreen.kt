package com.example.quanlythuviensach.screens.admin.borrowreturn

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.quanlythuviensach.models.BorrowRecord
import com.example.quanlythuviensach.screens.admin.book.BookViewModel
import com.example.quanlythuviensach.screens.admin.user.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlythuviensach.database.BookRepository
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.TaikhoanRepository

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BorrowReturnManagementScreen(navController: NavController, viewModel: BorrowViewModel) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(TaikhoanRepository(DatabaseHelper.getDatabase(context).taikhoanDao())))
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(BookRepository(DatabaseHelper.getDatabase(context).sachDao())))

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Đang mượn", "Đã trả", "Quá hạn")
    var searchQuery by remember { mutableStateOf("") }

    val borrowRecords by viewModel.borrowRecords.collectAsState()
    val users by userViewModel.users.collectAsState()
    val books by bookViewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddBorrowDialog by remember { mutableStateOf(false) }
    var showEditBorrowDialog by remember { mutableStateOf(false) }
    var selectedBorrowRecord by remember { mutableStateOf<BorrowRecord?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Thanh tab
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Thanh tìm kiếm
//        OutlinedTextField(
//            value = searchQuery,
//            onValueChange = { searchQuery = it },
//            label = { Text("Tìm kiếm mã sách hoặc mã người dùng") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )

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
                val filteredRecords = when (selectedTab) {
                    0 -> borrowRecords // Hiển thị tất cả
                    1 -> borrowRecords.filter { it.trangThaiMuonTra == "Đang mượn" } // Đang mượn
                    2 -> borrowRecords.filter { it.trangThaiMuonTra == "Đã trả" } // Đã trả
                    3 -> borrowRecords.filter {
                        val dueDate = it.ngayMuon + 14 * 24 * 60 * 60 * 1000L // Giả sử hạn trả là 14 ngày
                        it.trangThaiMuonTra == "Đang mượn" && System.currentTimeMillis() > dueDate
                    } // Quá hạn
                    else -> borrowRecords
                }
//                .filter {
//                    it.maSach.toString().contains(searchQuery, ignoreCase = true) ||
//                            it.maNguoiDung.toString().contains(searchQuery, ignoreCase = true)
//                }
                BorrowList(filteredRecords, navController, viewModel) { borrowRecord ->
                    selectedBorrowRecord = borrowRecord
                    showEditBorrowDialog = true
                }
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
                onClick = { showAddBorrowDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Thêm", color = Color.White)
            }
        }
    }

    // Gọi AddBorrowDialog
    if (showAddBorrowDialog) {
        AddBorrowDialog(
            users = users,
            books = books,
            onDismiss = { showAddBorrowDialog = false },
            onConfirm = { borrowRecord ->
                viewModel.addBorrowRecord(borrowRecord)
                showAddBorrowDialog = false
            }
        )
    }

    // Gọi EditBorrowDialog
    if (showEditBorrowDialog && selectedBorrowRecord != null) {
        EditBorrowDialog(
            borrowRecord = selectedBorrowRecord!!,
            onDismiss = {
                showEditBorrowDialog = false
                selectedBorrowRecord = null
            },
            onConfirm = { updatedBorrowRecord ->
                viewModel.updateBorrowRecord(updatedBorrowRecord)
                showEditBorrowDialog = false
                selectedBorrowRecord = null
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddBorrowDialog(
    users: List<com.example.quanlythuviensach.models.User>,
    books: List<com.example.quanlythuviensach.models.Book>,
    onDismiss: () -> Unit,
    onConfirm: (BorrowRecord) -> Unit
) {
    var selectedUser by remember { mutableStateOf<com.example.quanlythuviensach.models.User?>(null) }
    var selectedBook by remember { mutableStateOf<com.example.quanlythuviensach.models.Book?>(null) }
    var trangThaiMuonTra by remember { mutableStateOf("Đang mượn") }
    var ngayMuon by remember { mutableStateOf(System.currentTimeMillis()) }
    var ngayTra by remember { mutableStateOf<Long?>(null) }
    var dienThoai by remember { mutableStateOf("") }
    var expandedUser by remember { mutableStateOf(false) }
    var expandedBook by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm phiếu mượn") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expandedUser,
                    onExpandedChange = { expandedUser = it }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.ten ?: "Chọn người mượn",
                        onValueChange = {},
                        label = { Text("Chọn người mượn") },
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
                    expanded = expandedBook,
                    onExpandedChange = { expandedBook = it }
                ) {
                    OutlinedTextField(
                        value = selectedBook?.tenSach ?: "Chọn sách",
                        onValueChange = {},
                        label = { Text("Chọn sách") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBook)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBook,
                        onDismissRequest = { expandedBook = false }
                    ) {
                        books.forEach { book ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedBook = book
                                    expandedBook = false
                                }
                            ) {
                                Text(text = book.tenSach)
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = trangThaiMuonTra,
                    onValueChange = { trangThaiMuonTra = it },
                    label = { Text("Trạng thái (Đang mượn/Đã trả)") },
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
                    if (selectedUser == null || selectedBook == null) return@Button
                    val newBorrowRecord = BorrowRecord(
                        maMuon = 0,
                        maNguoiDung = selectedUser?.maNguoiDung ?: 0,
                        maSach = selectedBook?.maSach ?: 0,
                        trangThaiMuonTra = trangThaiMuonTra,
                        ngayMuon = ngayMuon,
                        ngayTra = ngayTra,
                        dienThoai = dienThoai
                    )
                    onConfirm(newBorrowRecord)
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
fun EditBorrowDialog(borrowRecord: BorrowRecord, onDismiss: () -> Unit, onConfirm: (BorrowRecord) -> Unit) {
    var maNguoiDung by remember { mutableStateOf(borrowRecord.maNguoiDung.toString()) }
    var maSach by remember { mutableStateOf(borrowRecord.maSach.toString()) }
    var trangThaiMuonTra by remember { mutableStateOf(borrowRecord.trangThaiMuonTra) }
    var ngayMuon by remember { mutableStateOf(borrowRecord.ngayMuon) }
    var ngayTra by remember { mutableStateOf(borrowRecord.ngayTra?.toString() ?: "") }
    var dienThoai by remember { mutableStateOf(borrowRecord.dienThoai) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật phiếu mượn") },
        text = {
            Column {
                OutlinedTextField(
                    value = maNguoiDung,
                    onValueChange = { maNguoiDung = it },
                    label = { Text("Mã người dùng") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = maSach,
                    onValueChange = { maSach = it },
                    label = { Text("Mã sách") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = trangThaiMuonTra,
                    onValueChange = { trangThaiMuonTra = it },
                    label = { Text("Trạng thái (Đang mượn/Đã trả)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = ngayTra,
                    onValueChange = { ngayTra = it },
                    label = { Text("Ngày trả (có thể để trống)") },
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
                    val updatedBorrowRecord = borrowRecord.copy(
                        maNguoiDung = maNguoiDung.toIntOrNull() ?: borrowRecord.maNguoiDung,
                        maSach = maSach.toIntOrNull() ?: borrowRecord.maSach,
                        trangThaiMuonTra = trangThaiMuonTra,
                        ngayTra = ngayTra.toLongOrNull(),
                        dienThoai = dienThoai
                    )
                    onConfirm(updatedBorrowRecord)
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
    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun BorrowList(
    borrowRecords: List<BorrowRecord>,
    navController: NavController,
    viewModel: BorrowViewModel,
    onEditClick: (BorrowRecord) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(borrowRecords) { borrowRecord ->
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc muốn xóa phiếu mượn mã ${borrowRecord.maMuon}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteBorrowRecord(borrowRecord.maMuon)
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

            BorrowItem(
                borrowRecord = borrowRecord,
                onClick = { /* TODO: Điều hướng nếu cần */ },
                onEditClick = { onEditClick(borrowRecord) },
                onDeleteClick = { showDeleteDialog = true }
            )
        }
    }
}

@Composable
fun BorrowItem(
    borrowRecord: BorrowRecord,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val ngayMuon = dateFormat.format(Date(borrowRecord.ngayMuon))
    val ngayTra = borrowRecord.ngayTra?.let { dateFormat.format(Date(it)) } ?: "Chưa trả"

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
            Text("Mã mượn: ${borrowRecord.maMuon}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Mã sách: ${borrowRecord.maSach}", fontSize = 12.sp, color = Color.Gray)
            Text("Mã người dùng: ${borrowRecord.maNguoiDung}", fontSize = 12.sp, color = Color.Gray)
            Text("Trạng thái: ${borrowRecord.trangThaiMuonTra}", fontSize = 12.sp, color = Color.Gray)
            Text("Ngày mượn: $ngayMuon", fontSize = 12.sp, color = Color.Gray)
            Text("Ngày trả: $ngayTra", fontSize = 12.sp, color = Color.Gray)
        }
        Button(
            onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("Cập nhật", color = Color.White, fontSize = 12.sp)
        }
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(24.dp) // Thu nhỏ icon
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Xóa",
                tint = Color.Red,
                modifier = Modifier.size(16.dp) // Thu nhỏ icon bên trong
            )
        }
    }
}
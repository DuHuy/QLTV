package com.example.quanlythuviensach.screens.admin.reservation

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
import com.example.quanlythuviensach.models.Reservation
import com.example.quanlythuviensach.screens.admin.book.BookViewModel
import com.example.quanlythuviensach.screens.admin.user.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlythuviensach.database.DatabaseHelper
import com.example.quanlythuviensach.database.TaikhoanRepository
import com.example.quanlythuviensach.database.BookRepository

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReservationManagementScreen(navController: NavController, viewModel: ReservationViewModel) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(TaikhoanRepository(DatabaseHelper.getDatabase(context).taikhoanDao())))
    val bookViewModel: BookViewModel = viewModel(factory = BookViewModel.provideFactory(BookRepository(DatabaseHelper.getDatabase(context).sachDao())))

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Đang chờ", "Hoàn thành", "Đã hủy")
    // Loại bỏ biến searchQuery vì không cần tìm kiếm

    val reservations by viewModel.reservations.collectAsState()
    val users by userViewModel.users.collectAsState()
    val books by bookViewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddReservationDialog by remember { mutableStateOf(false) }
    var showEditReservationDialog by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Loại bỏ OutlinedTextField tìm kiếm
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
                val filteredReservations = when (selectedTab) {
                    0 -> reservations // Hiển thị tất cả
                    1 -> reservations.filter { it.trangThaiDat == "đang chờ" } // Đang chờ
                    2 -> reservations.filter { it.trangThaiDat == "hoàn thành" } // Hoàn thành
                    3 -> reservations.filter { it.trangThaiDat == "đã hủy" } // Đã hủy
                    else -> reservations
                } // Loại bỏ lọc theo searchQuery
                ReservationList(filteredReservations, navController, viewModel) { reservation ->
                    selectedReservation = reservation
                    showEditReservationDialog = true
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { showAddReservationDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Thêm", color = Color.White)
            }
        }
    }

    if (showAddReservationDialog) {
        AddReservationDialog(
            users = users,
            books = books,
            onDismiss = { showAddReservationDialog = false },
            onConfirm = { reservation ->
                viewModel.addReservation(reservation)
                showAddReservationDialog = false
            }
        )
    }

    if (showEditReservationDialog && selectedReservation != null) {
        EditReservationDialog(
            reservation = selectedReservation!!,
            onDismiss = {
                showEditReservationDialog = false
                selectedReservation = null
            },
            onConfirm = { updatedReservation ->
                viewModel.updateReservation(updatedReservation)
                showEditReservationDialog = false
                selectedReservation = null
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReservationDialog(
    users: List<com.example.quanlythuviensach.models.User>,
    books: List<com.example.quanlythuviensach.models.Book>,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {
    var selectedUser by remember { mutableStateOf<com.example.quanlythuviensach.models.User?>(null) }
    var selectedBook by remember { mutableStateOf<com.example.quanlythuviensach.models.Book?>(null) }
    var trangThaiDat by remember { mutableStateOf("đang chờ") }
    var ngayDatTruoc by remember { mutableStateOf(System.currentTimeMillis()) }
    var expandedUser by remember { mutableStateOf(false) }
    var expandedBook by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm yêu cầu đặt trước") },
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
                    value = trangThaiDat,
                    onValueChange = { trangThaiDat = it },
                    label = { Text("Trạng thái (đang chờ/hoàn thành/đã hủy)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedUser == null || selectedBook == null) return@Button
                    val newReservation = Reservation(
                        maDatTruoc = 0,
                        maNguoiDung = selectedUser?.maNguoiDung ?: 0,
                        maSach = selectedBook?.maSach ?: 0,
                        ngayDatTruoc = ngayDatTruoc,
                        trangThaiDat = trangThaiDat
                    )
                    onConfirm(newReservation)
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
fun EditReservationDialog(
    reservation: Reservation,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {
    var maNguoiDung by remember { mutableStateOf(reservation.maNguoiDung.toString()) }
    var maSach by remember { mutableStateOf(reservation.maSach.toString()) }
    var ngayDatTruoc by remember { mutableStateOf(reservation.ngayDatTruoc) }
    var trangThaiDat by remember { mutableStateOf(reservation.trangThaiDat) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật yêu cầu đặt trước") },
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
                    value = trangThaiDat,
                    onValueChange = { trangThaiDat = it },
                    label = { Text("Trạng thái (đang chờ/hoàn thành/đã hủy)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedReservation = reservation.copy(
                        maNguoiDung = maNguoiDung.toIntOrNull() ?: reservation.maNguoiDung,
                        maSach = maSach.toIntOrNull() ?: reservation.maSach,
                        trangThaiDat = trangThaiDat
                    )
                    onConfirm(updatedReservation)
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
fun ReservationList(
    reservations: List<Reservation>,
    navController: NavController,
    viewModel: ReservationViewModel,
    onEditClick: (Reservation) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(reservations) { reservation ->
            var showDeleteDialog by remember { mutableStateOf(false) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc muốn xóa yêu cầu đặt trước mã ${reservation.maDatTruoc}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteReservation(reservation.maDatTruoc)
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

            ReservationItem(
                reservation = reservation,
                onClick = { /* TODO: Điều hướng nếu cần */ },
                onEditClick = { onEditClick(reservation) },
                onDeleteClick = { showDeleteDialog = true }
            )
        }
    }
}

@Composable
fun ReservationItem(
    reservation: Reservation,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val ngayDatTruoc = dateFormat.format(Date(reservation.ngayDatTruoc))

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
            Text("Mã đặt trước: ${reservation.maDatTruoc}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Mã sách: ${reservation.maSach}", fontSize = 14.sp, color = Color.Gray)
            Text("Mã người dùng: ${reservation.maNguoiDung}", fontSize = 14.sp, color = Color.Gray)
            Text("Trạng thái: ${reservation.trangThaiDat}", fontSize = 14.sp, color = Color.Gray)
            Text("Ngày đặt: $ngayDatTruoc", fontSize = 14.sp, color = Color.Gray)
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
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Xóa",
                tint = Color.Red,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
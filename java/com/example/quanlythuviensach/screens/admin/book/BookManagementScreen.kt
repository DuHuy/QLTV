package com.example.quanlythuviensach.screens.admin.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quanlythuviensach.R
import com.example.quanlythuviensach.models.Book // Đảm bảo import thực thể Book từ models
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController


@Composable
fun BookManagementScreen(viewModel: BookViewModel = viewModel(), navController: NavHostController) {
    val books by viewModel.books.collectAsState() // Lấy danh sách sách từ ViewModel
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(searchQuery, onValueChange = { query ->
            searchQuery = query
            viewModel.searchBooks(query)
        }, navController)

        ActionButtons(
            onAddClick = {
                isEditing = false
                showDialog = true
            },
            onEditClick = {
                if (selectedBook != null) {
                    isEditing = true
                    showDialog = true
                } else {
                    // TODO: Hiển thị thông báo "Vui lòng chọn sách để sửa"
                }
            },
            onDeleteClick = {
                if (selectedBook != null) {
                    showDeleteConfirm = true
                } else {
                    // TODO: Hiển thị thông báo "Vui lòng chọn sách để xóa"
                }
            },
            onFilterClick = { /* TODO: Xử lý lọc nâng cao nếu cần */ },
            navController
        )

        BookListSection(
            books,
            onSelect = { book ->
                selectedBook = book // Lưu sách được chọn
            },
            navController = navController
        )
    }

    // Dialog để thêm/sửa sách
    if (showDialog) {
        var bookName by remember { mutableStateOf(if (isEditing) selectedBook?.tenSach ?: "" else "") }
        var author by remember { mutableStateOf(if (isEditing) selectedBook?.tacGia ?: "" else "") }
        var year by remember { mutableStateOf(if (isEditing) selectedBook?.namXuatBan?.toString() ?: "" else "") }
        var status by remember { mutableStateOf(if (isEditing) selectedBook?.tinhTrangSach ?: "Còn sách" else "Còn sách") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isEditing) "Sửa Sách" else "Thêm Sách") },
            text = {
                Column {
                    OutlinedTextField(
                        value = bookName,
                        onValueChange = { bookName = it },
                        label = { Text("Tên sách") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Tác giả") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Năm xuất bản") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Tình trạng") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (bookName.isNotBlank() && author.isNotBlank() && year.isNotBlank() && status.isNotBlank()) {
                        val yearInt = year.toIntOrNull()
                        if (yearInt != null && yearInt > 0) {
                            val newBook = Book(
                                maSach = if (isEditing) selectedBook?.maSach ?: 0 else 0,
                                tenSach = bookName,
                                tacGia = author,
                                namXuatBan = yearInt,
                                tinhTrangSach = status
                            )
                            if (isEditing) {
                                viewModel.updateBook(newBook)
                            } else {
                                viewModel.addBook(newBook)
                            }
                            showDialog = false
                            selectedBook = null
                        } else {
                            // TODO: Hiển thị thông báo lỗi nếu năm không hợp lệ
                        }
                    } else {
                        // TODO: Hiển thị thông báo lỗi nếu các trường trống
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog xác nhận xóa
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa sách '${selectedBook?.tenSach}' không?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedBook?.maSach?.let { viewModel.deleteBook(it) }
                    showDeleteConfirm = false
                    selectedBook = null
                }) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun SearchBar(searchQuery: String, onValueChange: (String) -> Unit, navController: NavHostController? = null) {
    TextField(
        value = searchQuery,
        onValueChange = onValueChange,
        placeholder = { Text("Tìm kiếm sách") },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    )
    // Ví dụ: Thêm nút để điều hướng
//    Button(onClick = { navController?.navigate("advanced_search") }) {
//        Text("Tìm kiếm")
//    }
}

@Composable
fun ActionButtons(
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFilterClick: () -> Unit,
    navController: NavHostController? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
            Text("Thêm", color = Color.White)
        }
        Button(onClick = onEditClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
            Text("Sửa", color = Color.White)
        }
        Button(onClick = onDeleteClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
            Text("Xóa", color = Color.White)
        }
//        Button(onClick = { onFilterClick(); navController?.navigate("filter_books") }) {
//            Text("Lọc")
//        }
    }
}

@Composable
fun BookListSection(books: List<Book>, onSelect: (Book) -> Unit, navController: NavHostController) {
    Text(
        "Danh sách sách",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelect(book) },
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.logodhtn),
                        contentDescription = "Biểu tượng sách",
                        modifier = Modifier.size(80.dp)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = book.tenSach,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tác giả: ${book.tacGia}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Năm: ${book.namXuatBan}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tình trạng: ${book.tinhTrangSach}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    // Nút xem chi tiết
                    Button(
                        onClick = { navController.navigate("book_detail/${book.maSach}") },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("Chi tiết", color = Color.White,
                        fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
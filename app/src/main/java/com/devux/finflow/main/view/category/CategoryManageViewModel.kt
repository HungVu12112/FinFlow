package com.devux.finflow.main.view.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.repository.category.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManageViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<CategoryEntity>> =
        categoryRepository.getAllCategories().asLiveData()

    private val _eventMessage = MutableLiveData<String>()
    val eventMessage: LiveData<String> get() = _eventMessage

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.insertCategory(category)
                postMessage("Thêm danh mục thành công")
            } catch (e: Exception) {
                postMessage("Lỗi khi thêm: ${e.message}")
            }
        }
    }

//    // 3. Cập nhật danh mục (Sửa tên, icon)
    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.updateCategory(category)
                postMessage("Cập nhật thành công")
            } catch (e: Exception) {
                postMessage("Lỗi khi cập nhật: ${e.message}")
            }
        }
    }

    // 4. Xóa danh mục
    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                categoryRepository.deleteCategory(category)
                postMessage("Đã xóa danh mục")
            } catch (e: Exception) {
                postMessage("Lỗi khi xóa: ${e.message}")
            }
        }
    }

    // Hàm tiện ích để gửi thông báo lên UI (cần chạy trên Main thread nếu dùng setValue)
    private fun postMessage(message: String) {
        _eventMessage.postValue(message)
    }
}
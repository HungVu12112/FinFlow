package com.devux.finflow.main.view.analytics

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.TransactionType
import com.devux.finflow.data.model.AnalyticsUiState
import com.devux.finflow.data.model.CategoryStatModel
import com.devux.finflow.data.repository.category.CategoryRepository
import com.devux.finflow.data.repository.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AnalyticsUiState>()
    val uiState: LiveData<AnalyticsUiState> = _uiState

    private val _currentMonth = MutableLiveData<Calendar>()
    val currentMonth: LiveData<Calendar> = _currentMonth

    init {
        _currentMonth.value = Calendar.getInstance()
        loadData()
    }

    fun nextMonth() {
        val cal = _currentMonth.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, 1)
        _currentMonth.value = cal
        loadData()
    }

    fun prevMonth() {
        val cal = _currentMonth.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        _currentMonth.value = cal
        loadData()
    }

    private fun loadData() {
        val cal = _currentMonth.value ?: Calendar.getInstance()
        // Tính ngày đầu và cuối tháng
        val startOfMonth = cal.clone() as Calendar
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)

        val endOfMonth = cal.clone() as Calendar
        endOfMonth.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23)
        endOfMonth.set(Calendar.MINUTE, 59)
        endOfMonth.set(Calendar.SECOND, 59)

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Lấy tất cả giao dịch trong tháng
            transactionRepository.getTransactionsByDateRange(
                startOfMonth.timeInMillis,
                endOfMonth.timeInMillis
            )
                .collect { transactions ->

                    var income = 0.0
                    var expense = 0.0
                    val categoryMap = mutableMapOf<Long, Double>() // Map<CategoryId, TotalAmount>

                    // 2. Tính toán tổng
                    for (trans in transactions) {
                        if (trans.amount > 0) { // Giả sử amount dương là thu, âm là chi hoặc ngược lại tùy logic bạn
                            if (trans.type == TransactionType.EXPENSE) { // Hoặc check theo logic của bạn
                                expense += trans.amount

                                // 1. Chuyển đổi an toàn: Nếu null hoặc không phải số -> trả về null
                                val catId = trans.categoryId?.toLongOrNull()

                                // 2. Chỉ tính toán nếu có ID hợp lệ
                                if (catId != null) {
                                    val current = categoryMap[catId] ?: 0.0
                                    categoryMap[catId] = current + trans.amount
                                }
                            }
                        }
                    }

                    // 3. Lấy thông tin danh mục và mapping
                    val allCategories =
                        categoryRepository.getAllCategories().first() // Lấy list category 1 lần
                    val statList = ArrayList<CategoryStatModel>()

                    // Danh sách màu sắc cho biểu đồ (Vibrant Colors)
                    val colors = listOf(
                        Color.parseColor("#EF5350"), Color.parseColor("#42A5F5"),
                        Color.parseColor("#66BB6A"), Color.parseColor("#FFA726"),
                        Color.parseColor("#AB47BC"), Color.parseColor("#FF7043")
                    )
                    var colorIndex = 0

                    categoryMap.forEach { (catId, amount) ->
                        val category = allCategories.find { it.id == catId }
                        if (category != null) {
                            val percent =
                                if (expense > 0) (amount / expense * 100).toFloat() else 0f
                            val color = colors[colorIndex % colors.size]

                            statList.add(
                                CategoryStatModel(
                                    categoryName = category.name,
                                    icon = category.icon,
                                    amount = amount,
                                    percentage = percent,
                                    color = color
                                )
                            )
                            colorIndex++
                        }
                    }

                    // Sắp xếp giảm dần theo số tiền
                    statList.sortByDescending { it.amount }

                    _uiState.postValue(
                        AnalyticsUiState(
                            totalIncome = income,
                            totalExpense = expense,
                            balance = income - expense,
                            expenseList = statList
                        )
                    )
                }
        }
    }
}
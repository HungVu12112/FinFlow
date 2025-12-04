package com.devux.finflow.main.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.R
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.data.model.BudgetUiModel
import com.devux.finflow.data.repository.budget.BudgetRepository
import com.devux.finflow.data.repository.category.CategoryRepository
import com.devux.finflow.data.repository.transaction.TransactionRepository
import com.devux.finflow.utils.TimeUtils
import com.tta.futurenest.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
) : BaseViewModel() {
    private val _currentTransactions = MutableLiveData<List<TransactionEntity>>()
    val currentTransactions: LiveData<List<TransactionEntity>> = _currentTransactions
    val allCategories = categoryRepository.getAllCategories().asLiveData()
    val allTransaction = transactionRepository.getAllTransactions().asLiveData()
    // LiveData cho tổng Thu/Chi hiển thị
    val totalIncome = MutableLiveData<Double>(0.0)
    val totalExpense = MutableLiveData<Double>(0.0)
    private val _homeBudgets = MutableLiveData<List<BudgetUiModel>>()
    val homeBudgets: LiveData<List<BudgetUiModel>> = _homeBudgets

    init {
        // Tự động load ngân sách tháng hiện tại khi mở màn hình
        loadCurrentMonthBudgets()
    }
    init {
        filterByMonth()
    }
    val totalBalance = MediatorLiveData<Double>().apply {
        // Mặc định là 0
        value = 0.0

        // Khi totalIncome thay đổi -> tính lại Balance
        addSource(totalIncome) { income ->
            val expense = totalExpense.value ?: 0.0
            value = income - expense
        }

        // Khi totalExpense thay đổi -> tính lại Balance
        addSource(totalExpense) { expense ->
            val income = totalIncome.value ?: 0.0
            value = income - expense
        }
    }
    fun filterByDay() {
        val (start, end) = TimeUtils.getStartAndEndOfDay()
        loadData(start, end)
    }

    fun filterByMonth() {
        val (start, end) = TimeUtils.getStartAndEndOfMonth()
        loadData(start, end)
    }

    fun filterByYear() {
        val (start, end) = TimeUtils.getStartAndEndOfYear()
        loadData(start, end)
    }

    fun filterCustom(start: Long, end: Long) {
        loadData(start, end)
    }

    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
    private fun loadData(start: Long, end: Long) {
        viewModelScope.launch {
            // 1. Lấy danh sách giao dịch
            transactionRepository.getTransactionsByDateRange(start, end).collect { list ->
                _currentTransactions.value = list
            }
        }

        viewModelScope.launch {
            // 2. Lấy Tổng Chi
            transactionRepository.getTotalAmountByDateRange(TransactionType.EXPENSE, start, end).collect { amount ->
                totalExpense.value = amount ?: 0.0
            }
        }

        viewModelScope.launch {
            // 3. Lấy Tổng Thu
            transactionRepository.getTotalAmountByDateRange(TransactionType.INCOME, start, end).collect { amount ->
                totalIncome.value = amount ?: 0.0
            }
        }
    }
    private fun loadCurrentMonthBudgets() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        // Lấy ngày đầu và cuối tháng để tính tổng chi tiêu
        val (startDate, endDate) = TimeUtils.getStartAndEndOfMonth()

        viewModelScope.launch(Dispatchers.IO) {
            // KẾT HỢP 3 LUỒNG DỮ LIỆU
            combine(
                budgetRepository.getBudgetsForMonth(currentMonth, currentYear), // 1. Danh sách hạn mức
                transactionRepository.getMonthlyExpenses(startDate, endDate),   // 2. Tổng chi tiêu thực tế
                categoryRepository.getAllCategories()                           // 3. Tên danh mục
            ) { budgets, expenses, categories ->

                val uiList = ArrayList<BudgetUiModel>()

                for (budget in budgets) {
                    // a. Tìm tên category
                    val category = categories.find { it.id == budget.categoryId }
                    val name = category?.name ?: "Danh mục khác"

                    // b. Tìm số tiền đã tiêu (so khớp ID)
                    // Lưu ý: categoryId trong TransactionExpenseTuple phải khớp kiểu dữ liệu (Long)
                    val expenseData = expenses.find { it.categoryId == budget.categoryId.toString() }
                    val spent = expenseData?.total ?: 0.0

                    // c. Tính toán
                    val limit = budget.amountLimit
                    val remaining = limit - spent
                    var progress = if (limit > 0) ((spent / limit) * 100).toInt() else 0

                    // d. Xác định màu sắc cảnh báo
                    val (colorRes, status) = when {
                        progress >= 100 -> Pair(R.color.red_500, "Vượt mức!")
                        progress >= 80 -> Pair(R.color.orange_500, "Cảnh báo")
                        else -> Pair(R.color.green_500, "An toàn")
                    }

                    // Giới hạn progress max là 100 để thanh bar không bị tràn (nếu muốn)
                    if (progress > 100) progress = 100

                    uiList.add(BudgetUiModel(
                        categoryId = budget.categoryId,
                        categoryName = name,
                        amountLimit = limit,
                        amountSpent = spent,
                        remainingAmount = remaining,
                        progress = progress,
                        statusColor = colorRes,
                        statusText = status
                    ))
                }
                uiList // Trả về list kết quả
            }.collect { resultList ->
                // Đẩy dữ liệu ra UI
                _homeBudgets.postValue(resultList)
            }
        }
    }
}
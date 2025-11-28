package com.devux.finflow.main.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.data.repository.category.CategoryRepository
import com.devux.finflow.data.repository.transaction.TransactionRepository
import com.devux.finflow.utils.TimeUtils
import com.tta.futurenest.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel() {
    private val _currentTransactions = MutableLiveData<List<TransactionEntity>>()
    val currentTransactions: LiveData<List<TransactionEntity>> = _currentTransactions
    val allCategories = categoryRepository.getAllCategories().asLiveData()
    val allTransaction = transactionRepository.getAllTransactions().asLiveData()
    // LiveData cho tổng Thu/Chi hiển thị
    val totalIncome = MutableLiveData<Double>(0.0)
    val totalExpense = MutableLiveData<Double>(0.0)
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
}
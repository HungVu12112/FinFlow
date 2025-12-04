package com.devux.finflow.main.view.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.BudgetEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.data.model.CategoryBudgetState
import com.devux.finflow.data.repository.budget.BudgetRepository
import com.devux.finflow.data.repository.category.CategoryRepository
import com.tta.futurenest.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : BaseViewModel() {

    private val _budgetList = MutableLiveData<List<CategoryBudgetState>>()
    val budgetList: LiveData<List<CategoryBudgetState>> = _budgetList

    fun loadBudgets() {
        val today = Calendar.getInstance()
        val month = today.get(Calendar.MONTH) + 1
        val year = today.get(Calendar.YEAR)

        viewModelScope.launch(Dispatchers.IO) {
            // Combine: Categories & Budgets
            combine(
                categoryRepository.getAllCategories(), // Flow<List<Category>>
                budgetRepository.getBudgetsForMonth(month, year) // Flow<List<Budget>>
            ) { categories, budgets ->
                // Chỉ lấy category loại EXPENSE (Chi tiêu) để đặt ngân sách
                val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }

                expenseCategories.map { category ->
                    // Tìm xem category này đã có budget chưa
                    val budget = budgets.find { it.categoryId == category.id }

                    CategoryBudgetState(
                        categoryId = category.id,
                        categoryName = category.name,
                        categoryIcon = category.icon,
                        currentLimit = budget?.amountLimit ?: 0.0,
                        budgetId = budget?.id ?: 0L
                    )
                }
            }.collect { list ->
                _budgetList.postValue(list)
            }
        }
    }

    // Hàm lưu ngân sách
    fun saveBudget(categoryId: Long, amount: Double) {
        val today = Calendar.getInstance()
        val month = today.get(Calendar.MONTH) + 1
        val year = today.get(Calendar.YEAR)

        viewModelScope.launch(Dispatchers.IO) {
            // 1. KIỂM TRA XEM ĐÃ CÓ BUDGET CHƯA
            val existingBudget = budgetRepository.getBudgetByCategory(categoryId, month, year)

            val budgetToSave = existingBudget?.// TRƯỜNG HỢP UPDATE: Giữ nguyên ID cũ
            copy(amountLimit = amount)
                ?: // TRƯỜNG HỢP INSERT MỚI: ID tự sinh (= 0)
                BudgetEntity(
                    categoryId = categoryId,
                    amountLimit = amount,
                    month = month,
                    year = year
                )

            // 2. Gọi hàm insert (Vì Dao dùng OnConflictStrategy.REPLACE nên nó sẽ tự xử lý Update nếu có ID)
            budgetRepository.insertBudget(budgetToSave)

            // Hoặc an toàn hơn, nếu số tiền = 0 thì xóa luôn
            if (amount <= 0 && existingBudget != null) {
                budgetRepository.deleteBudget(existingBudget.id)
            }
        }
    }
}
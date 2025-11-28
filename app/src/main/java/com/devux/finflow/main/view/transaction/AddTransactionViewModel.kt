package com.devux.finflow.main.view.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionEntity
import com.devux.finflow.data.repository.transaction.TransactionRepository
import com.devux.finflow.data.repository.category.CategoryRepository
import com.tta.futurenest.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel() {
    private val _categories = MutableLiveData<List<CategoryEntity>>(emptyList())
    val categories: LiveData<List<CategoryEntity>?> get() = _categories

    private val _transactions = MutableLiveData<List<TransactionEntity>>(emptyList())
    val transactions: LiveData<List<TransactionEntity>?> get() = _transactions

    private val _isShowCategory = MutableLiveData<Boolean>()
    val isShowCategory: LiveData<Boolean> get() = _isShowCategory

    private val _isShowType = MutableLiveData<Boolean>()
    val isShowType: LiveData<Boolean> get() = _isShowType

    init {
        _isShowCategory.value = false
        _isShowType.value = false
    }

    fun getAllCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    fun changeVisibleCategory(){
        if (_isShowCategory.value == true) {
            _isShowCategory.value = false
        } else {
            _isShowCategory.value = true
        }
    }

    fun changeVisibleType(){
        if (_isShowType.value == true) {
            _isShowType.value = false
        } else {
            _isShowType.value = true
        }
    }

    fun addTransaction(transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transactionEntity)
        }
    }

    fun getAllTransaction(){
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                _transactions.value = transactions
            }
        }
    }
}

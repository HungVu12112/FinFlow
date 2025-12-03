package com.devux.finflow.main.view.goals.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devux.finflow.comon.TransactionResult
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.data.GoalTransactionEntity
import com.devux.finflow.data.repository.goal.GoalRepository
import com.devux.finflow.utils.CurrencyUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _goal = MutableLiveData<GoalEntity>()
    val goal: LiveData<GoalEntity> = _goal
    private val _history = MutableLiveData<List<GoalTransactionEntity>>()
    val history: LiveData<List<GoalTransactionEntity>> = _history

    fun setInitialGoal(goal: GoalEntity) {
        _goal.value = goal
        // Load lịch sử ngay khi có ID
        loadHistory(goal.id)
    }

    fun loadGoal(goalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val goalEntity = goalRepository.getGoalById(goalId)
            goalEntity?.let {
                _goal.postValue(it)
                // Sau khi load Goal xong thì load luôn lịch sử của nó
                loadHistory(it.id)
            }
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.deleteGoal(goal)
        }
    }

    private fun loadHistory(goalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.getGoalHistory(goalId).collect { list ->
                _history.postValue(list)
            }
        }
    }

    // Logic gợi ý tiết kiệm
    fun getSmartSuggestion(goal: GoalEntity): String {
        val remainingAmount = goal.targetAmount - goal.currentAmount
        if (remainingAmount <= 0) return "Chúc mừng! Bạn đã hoàn thành mục tiêu."

        val diff = goal.deadline - System.currentTimeMillis()
        val daysLeft = TimeUnit.MILLISECONDS.toDays(diff)

        return if (daysLeft > 0) {
            val dailySaving = remainingAmount / daysLeft
            "Bạn cần tiết kiệm ${CurrencyUtils.formatCurrency(dailySaving)} đ mỗi ngày để đạt mục tiêu."
        } else {
            "Mục tiêu đã quá hạn. Hãy cố gắng hoàn thành sớm nhé!"
        }
    }

    // Sửa lại hàm này trả về TransactionResult
    fun validateAndAdjustAmount(amountStr: String, isDeposit: Boolean): TransactionResult {
        // 1. Validate đầu vào (TC01, TC02)
        if (amountStr.isEmpty()) return TransactionResult.EMPTY_INPUT

        // Xóa dấu chấm và parse
        val cleanAmount = amountStr.replace(".", "")
        val amount = cleanAmount.toDoubleOrNull() ?: 0.02

        if (amount <= 0) return TransactionResult.INVALID_AMOUNT

        val currentGoalValue =
            _goal.value ?: return TransactionResult.SUCCESS // Nên handle error ở đây nếu cần

        // 2. Validate Logic nghiệp vụ (TC03)
        if (!isDeposit && amount > currentGoalValue.currentAmount) {
            return TransactionResult.INSUFFICIENT_FUNDS
        }

        // 3. Nếu mọi thứ OK -> Thực hiện tính toán và Lưu DB
        executeTransaction(currentGoalValue, amount, isDeposit)
        return TransactionResult.SUCCESS
    }

    private fun executeTransaction(currentGoal: GoalEntity, amount: Double, isDeposit: Boolean) {
        val newAmount = if (isDeposit) {
            currentGoal.currentAmount + amount
        } else {
            currentGoal.currentAmount - amount
        }

        viewModelScope.launch(Dispatchers.IO) {
            // Update Goal
            val updatedGoal = currentGoal.copy(currentAmount = newAmount)
            goalRepository.updateGoal(updatedGoal)

            // Update History
            val transaction = GoalTransactionEntity(
                goalId = currentGoal.id,
                amount = amount,
                type = if (isDeposit) 1 else -1,
                date = System.currentTimeMillis()
            )
            goalRepository.addGoalTransaction(transaction)

            // Update UI
            _goal.postValue(updatedGoal)
            loadHistory(currentGoal.id)
        }
    }
}
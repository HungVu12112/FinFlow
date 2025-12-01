package com.devux.finflow.main.view.goals.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun loadGoal(goalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val goalEntity = goalRepository.getGoalById(goalId)
            goalEntity?.let { _goal.postValue(it) }
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.deleteGoal(goal)
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
    fun adjustAmount(amount: Double, isDeposit: Boolean) {
        val currentGoalValue = _goal.value ?: return

        // 1. Tính toán số tiền mới
        val newAmount = if (isDeposit) {
            currentGoalValue.currentAmount + amount
        } else {
            val temp = currentGoalValue.currentAmount - amount
            if (temp < 0) 0.0 else temp // Không cho âm tiền
        }

        viewModelScope.launch(Dispatchers.IO) {
            // 2. Cập nhật Goal chính
            val updatedGoal = currentGoalValue.copy(currentAmount = newAmount)
            goalRepository.updateGoal(updatedGoal)

            // 3. Ghi vào lịch sử
            val transaction = GoalTransactionEntity(
                goalId = currentGoalValue.id,
                amount = amount,
                type = if (isDeposit) 1 else -1
            )
            goalRepository.addGoalTransaction(transaction)

            // 4. Cập nhật lại LiveData để UI tự động đổi (Optional vì Room Flow tự làm rồi)
            // _goal.postValue(updatedGoal)
        }
    }
}
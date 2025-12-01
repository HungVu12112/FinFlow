package com.devux.finflow.main.view.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.data.repository.goal.GoalRepository
import com.tta.futurenest.view.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : BaseViewModel() {

    // Danh sách Goals (tự động cập nhật khi DB thay đổi)
    val goals: LiveData<List<GoalEntity>> = goalRepository.getAllGoals().asLiveData()

    // Thêm mục tiêu
    fun addGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.insertGoal(goal)
        }
    }

    // Cập nhật mục tiêu (Ví dụ: Nạp thêm tiền)
    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.updateGoal(goal)
        }
    }

    // Xóa mục tiêu
    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.deleteGoal(goal)
        }
    }

    // Logic tính toán: Số ngày còn lại
    fun calculateDaysLeft(deadline: Long): String {
        val diff = deadline - System.currentTimeMillis()
        if (diff < 0) return "Đã hết hạn"

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return if (days > 365) {
            "${days / 365} năm còn lại"
        } else {
            "$days ngày còn lại"
        }
    }

    // Logic tính toán: Phần trăm hoàn thành (0 - 100)
    fun calculateProgress(current: Double, target: Double): Int {
        if (target <= 0) return 0
        val percent = (current / target) * 100
        return percent.toInt().coerceAtMost(100) // Không vượt quá 100%
    }
}
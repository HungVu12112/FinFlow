package com.devux.finflow.main.view.goals.add_goal

import android.text.TextWatcher
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.databinding.FragmentAddGoalBinding
import com.devux.finflow.main.view.adapter.ColorSelectionAdapter
import com.devux.finflow.main.view.goals.GoalViewModel
import com.devux.finflow.utils.ColorProvider
import com.devux.finflow.utils.NumberTextWatcher
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddGoalFragment : BaseFragment<FragmentAddGoalBinding>(FragmentAddGoalBinding::inflate) {

    private val viewModel: GoalViewModel by viewModels()
    private lateinit var colorAdapter: ColorSelectionAdapter

    // Biến lưu trạng thái
    private var selectedDate: Long = 0L
    private var selectedColor: Int = ColorProvider.colorResources.first() // Mặc định màu đầu tiên

    override fun initView() {
        setupColorRecyclerView()
        setupTextWatchers()
    }

    override fun setAction() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveGoal()
        }
    }

    override fun setObserve() {
        // Không cần observe gì đặc biệt ở màn hình Add
    }

    private fun setupColorRecyclerView() {
        colorAdapter = ColorSelectionAdapter(ColorProvider.colorResources) { colorHex ->
            selectedColor = colorHex
            colorAdapter.setSelectedColor(colorHex)
        }
        binding.rvColors.adapter = colorAdapter
        binding.rvColors.layoutManager = GridLayoutManager(requireContext(), 6)

        // Chọn màu mặc định ban đầu
        colorAdapter.setSelectedColor(selectedColor)
    }

    private fun setupTextWatchers() {
        // Format tiền khi nhập (dùng lại class NumberTextWatcher bạn đã có)
        binding.etTargetAmount.addTextChangedListener(NumberTextWatcher(binding.etTargetAmount))
        binding.etCurrentAmount.addTextChangedListener(NumberTextWatcher(binding.etCurrentAmount))
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày hoàn thành")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.CustomMaterialCalendarTheme) // Dùng theme fix lỗi lag
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvDeadline.text = formatter.format(Date(selectedDate))
        }
    }

    private fun saveGoal() {
        val name = binding.etGoalName.text.toString().trim()

        // Xóa dấu chấm phân cách trước khi parse Double
        val targetStr = binding.etTargetAmount.text.toString().replace(".", "")
        val currentStr = binding.etCurrentAmount.text.toString().replace(".", "")

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên mục tiêu", Toast.LENGTH_SHORT).show()
            return
        }

        if (targetStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền mục tiêu", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == 0L) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày hoàn thành", Toast.LENGTH_SHORT).show()
            return
        }

        val targetAmount = targetStr.toDoubleOrNull() ?: 0.0
        val currentAmount = currentStr.toDoubleOrNull() ?: 0.0

        if (targetAmount <= 0) {
            Toast.makeText(requireContext(), "Số tiền mục tiêu phải lớn hơn 0", Toast.LENGTH_SHORT).show()
            return
        }
        val newGoal = GoalEntity(
            name = name,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            deadline = selectedDate,
            color = convertResToHex(selectedColor)
        )

        // Lưu và thoát
        viewModel.addGoal(newGoal)
        Toast.makeText(requireContext(), "Đã tạo kế hoạch thành công!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()    }
    private fun convertResToHex(resId: Int): String {
        val colorInt = ContextCompat.getColor(requireContext(), resId)
        return String.format("#%06X", (0xFFFFFF and colorInt))
    }
}
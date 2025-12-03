package com.devux.finflow.main.view.goals.add_goal

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.GoalEntity
import com.devux.finflow.databinding.FragmentAddGoalBinding
import com.devux.finflow.main.view.adapter.ColorSelectionAdapter
import com.devux.finflow.main.view.goals.GoalViewModel
import com.devux.finflow.utils.ColorProvider
import com.devux.finflow.utils.CurrencyUtils
import com.devux.finflow.utils.NumberTextWatcher
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddGoalFragment : BaseFragment<FragmentAddGoalBinding>(FragmentAddGoalBinding::inflate) {

    private val viewModel: GoalViewModel by viewModels()

    // Sử dụng Safe Args để nhận dữ liệu truyền sang
    private val args: AddGoalFragmentArgs by navArgs()

    private lateinit var colorAdapter: ColorSelectionAdapter

    // Biến lưu trạng thái
    private var selectedDate: Long = 0L
    private var selectedColorRes: Int =
        ColorProvider.colorResources.first() // Mặc định màu đầu tiên

    // Biến kiểm tra chế độ Edit
    private var isEditMode = false
    private var editingGoalId: Long = 0L

    override fun initView() {
        setupTextWatchers()
        checkEditModeAndFillData() // Kiểm tra xem có phải đang sửa không
        setupColorRecyclerView()   // Setup list màu sau khi đã xác định được màu cần chọn
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
        // Không cần observe
    }
    private fun checkEditModeAndFillData() {
        val goalToEdit = args.goal

        if (goalToEdit != null) {
            isEditMode = true
            editingGoalId = goalToEdit.id

            // 1. Cập nhật UI tiêu đề
            binding.toolbar.title = "Chỉnh sửa kế hoạch"
            binding.btnSave.text = "Cập nhật"

            // 2. Điền tên
            binding.etGoalName.setText(goalToEdit.name)

            // 3. Điền số tiền (Format đẹp: 1.000.000)
            binding.etTargetAmount.setText(CurrencyUtils.formatCurrencyNoSymbol(goalToEdit.targetAmount))
            binding.etCurrentAmount.setText(CurrencyUtils.formatCurrencyNoSymbol(goalToEdit.currentAmount))

            // 4. Điền ngày tháng
            selectedDate = goalToEdit.deadline
            updateDateText(selectedDate)

            // 5. Điền màu sắc (Map ngược từ Hex String -> Resource ID)
            selectedColorRes = getColorResFromHex(goalToEdit.color)
        }
    }

    private fun setupColorRecyclerView() {
        colorAdapter = ColorSelectionAdapter(ColorProvider.colorResources) { colorResId ->
            selectedColorRes = colorResId
            colorAdapter.setSelectedColor(colorResId)
        }
        binding.rvColors.adapter = colorAdapter
        binding.rvColors.layoutManager = GridLayoutManager(requireContext(), 6)

        // Set màu đang chọn (Mặc định hoặc màu cũ của Goal)
        colorAdapter.setSelectedColor(selectedColorRes)
    }

    private fun setupTextWatchers() {
        binding.etTargetAmount.addTextChangedListener(NumberTextWatcher(binding.etTargetAmount))
        binding.etCurrentAmount.addTextChangedListener(NumberTextWatcher(binding.etCurrentAmount))
    }

    private fun showDatePicker() {
        // Nếu đang sửa, mở lịch tại ngày deadline cũ, nếu không thì lấy ngày hôm nay
        val openDate =
            if (selectedDate > 0) selectedDate else MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn ngày hoàn thành")
            .setSelection(openDate)
            .setTheme(R.style.CustomMaterialCalendarTheme)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection
            updateDateText(selectedDate)
        }
    }

    private fun updateDateText(timestamp: Long) {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvDeadline.text = formatter.format(Date(timestamp))
    }

    private fun saveGoal() {
        val name = binding.etGoalName.text.toString().trim()

        // Xóa dấu chấm trước khi parse
        val targetStr = binding.etTargetAmount.text.toString().replace(".", "")
        val currentStr = binding.etCurrentAmount.text.toString().replace(".", "")

        // 1. Check Tên
        if (name.isEmpty()) {
            binding.etGoalName.error = "Vui lòng nhập tên mục tiêu"
            binding.etGoalName.requestFocus()
            return
        }
        if (name.length > 50) { // Giới hạn độ dài
            binding.etGoalName.error = "Tên mục tiêu quá dài (tối đa 50 ký tự)"
            binding.etGoalName.requestFocus()
            return
        }

        // 2. Check Tiền Mục tiêu
        if (targetStr.isEmpty()) {
            binding.etTargetAmount.error = "Vui lòng nhập số tiền"
            binding.etTargetAmount.requestFocus()
            return
        }
        val targetAmount = targetStr.toDoubleOrNull() ?: 0.0

        if (targetAmount <= 0) {
            binding.etTargetAmount.error = "Số tiền phải lớn hơn 0"
            return
        }
        // Giới hạn max (ví dụ 100 tỷ) để tránh lỗi hiển thị
        if (targetAmount > 100_000_000_000.0) {
            binding.etTargetAmount.error = "Số tiền quá lớn"
            return
        }

        // 3. Check Tiền Hiện có (Current Amount)
        val currentAmount = currentStr.toDoubleOrNull() ?: 0.0

        if (currentAmount >= targetAmount) {
            binding.etCurrentAmount.error = "Số tiền hiện có phải nhỏ hơn mục tiêu"
            Toast.makeText(requireContext(), "Bạn đã đủ tiền rồi, không cần lập kế hoạch nữa!", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Check Ngày (Deadline)
        if (selectedDate == 0L) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày hoàn thành", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra ngày quá khứ (Lấy thời điểm hiện tại trừ đi 1 chút sai số timezone nếu cần)
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        if (selectedDate < today) {
            Toast.makeText(requireContext(), "Ngày hoàn thành phải ở tương lai", Toast.LENGTH_SHORT).show()
            return
        }

        // --- NẾU TẤT CẢ OK THÌ LƯU ---

        val hexColor = convertResToHex(selectedColorRes) // Hàm convert bạn đã có

        if (isEditMode) {
            // Update
            val updatedGoal = GoalEntity(
                id = editingGoalId, // ID CŨ
                name = name,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = selectedDate,
                color = hexColor
            )
            viewModel.updateGoal(updatedGoal)
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
        } else {
            // Add New
            val newGoal = GoalEntity(
                name = name,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = selectedDate,
                color = hexColor
            )
            viewModel.addGoal(newGoal)
            Toast.makeText(requireContext(), "Tạo kế hoạch thành công!", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    // --- CÁC HÀM TIỆN ÍCH CHUYỂN ĐỔI MÀU ---

    // 1. Chuyển từ R.color.xxx (Int) -> "#RRGGBB" (String)
    private fun convertResToHex(resId: Int): String {
        val colorInt = ContextCompat.getColor(requireContext(), resId)
        return String.format("#%06X", (0xFFFFFF and colorInt))
    }

    // 2. Chuyển từ "#RRGGBB" (String) -> R.color.xxx (Int)
    // Hàm này cần thiết để highlight đúng màu khi mở chế độ Edit
    private fun getColorResFromHex(hexColor: String): Int {
        // Duyệt qua danh sách ColorProvider để tìm xem mã hex nào khớp
        for (resId in ColorProvider.colorResources) {
            val colorInt = ContextCompat.getColor(requireContext(), resId)
            val hex = String.format("#%06X", (0xFFFFFF and colorInt))

            if (hex.equals(hexColor, ignoreCase = true)) {
                return resId
            }
        }
        // Nếu không tìm thấy (do đổi bảng màu hoặc lỗi), trả về màu đầu tiên
        return ColorProvider.colorResources.first()
    }
}
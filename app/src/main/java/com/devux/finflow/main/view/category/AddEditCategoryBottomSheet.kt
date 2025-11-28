package com.devux.finflow.main.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.databinding.FragmentAddEditCategoryBottomSheetBinding
import com.devux.finflow.main.view.adapter.IconSelectionAdapter
import com.devux.finflow.utils.IconProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditCategoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentAddEditCategoryBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryManageViewModel by viewModels()

    private lateinit var iconAdapter: IconSelectionAdapter

    // Dữ liệu nhận vào
    private var categoryToEdit: CategoryEntity? = null
    private var transactionType: TransactionType = TransactionType.EXPENSE

    // Trạng thái
    private var selectedIcon: String = "ic_food" // Icon mặc định
    private var isEditMode = false
    private var selectedIconRes: Int = 0

    companion object {
        const val TAG = "AddEditCategoryBottomSheet"

        fun newInstance(
            category: CategoryEntity? = null,
            type: TransactionType = TransactionType.EXPENSE
        ): AddEditCategoryBottomSheet {
            val fragment = AddEditCategoryBottomSheet()
            // Bạn có thể dùng Bundle để truyền dữ liệu nếu không muốn dùng biến static/setter
            // Nhưng ở đây mình set biến public cho đơn giản trong ví dụ
            fragment.categoryToEdit = category
            fragment.transactionType = type
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkEditMode()
        initViews()
        setupIconList()
        setupActions()
    }

    private fun checkEditMode() {
        if (categoryToEdit != null) {
            isEditMode = true
            selectedIcon = categoryToEdit!!.icon
            transactionType = categoryToEdit!!.type
        } else {
            // Nếu thêm mới, chọn icon mặc định khác nhau cho Thu/Chi cho hợp lý
            selectedIcon =
                if (transactionType == TransactionType.EXPENSE) "ic_food" else "ic_salary"
        }
    }

    private fun initViews() {
        if (isEditMode) {
            binding.tvTitle.text = "Chỉnh sửa danh mục"
            binding.etName.setText(categoryToEdit!!.name)
            binding.btnSave.text = "Cập nhật"
        } else {
            binding.tvTitle.text = "Thêm danh mục mới"
            binding.btnSave.text = "Thêm mới"
        }
    }

    private fun setupIconList() {
        // Kiểm tra xem IconProvider.icons có dữ liệu không
        if (IconProvider.icons.isEmpty()) {
            // Nếu list rỗng, RecyclerView sẽ không hiện gì cả.
            // Hãy chắc chắn bạn đã thêm R.drawable.xxx vào IconProvider
            return
        }

        // Khởi tạo giá trị mặc định nếu chưa chọn
        if (selectedIconRes == 0) {
            selectedIconRes = IconProvider.icons.first()
        }

        iconAdapter = IconSelectionAdapter(IconProvider.icons) { iconRes ->
            selectedIconRes = iconRes
            iconAdapter.setSelectedIcon(iconRes)
        }

        binding.rvIcons.adapter = iconAdapter
        binding.rvIcons.layoutManager = GridLayoutManager(requireContext(), 5)

        // Scroll tới icon đang chọn (để người dùng thấy ngay)
        val index = IconProvider.icons.indexOf(selectedIconRes)
        if (index != -1) {
            iconAdapter.setSelectedIcon(selectedIconRes)
            binding.rvIcons.scrollToPosition(index)
        }
    }

    private fun setupActions() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                binding.tilName.error = "Vui lòng nhập tên danh mục"
                return@setOnClickListener
            } else {
                binding.tilName.error = null
            }

            val iconName = getResourceName(selectedIconRes)

            if (isEditMode) {
                val updatedCategory = categoryToEdit!!.copy(
                    name = name,
                    icon = iconName
                )
                viewModel.updateCategory(updatedCategory)
                Toast.makeText(requireContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show()
            } else {
                val newCategory = CategoryEntity(
                    name = name,
                    icon = iconName,
                    type = transactionType
                )
                viewModel.addCategory(newCategory)
                Toast.makeText(requireContext(), "Đã thêm mới", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }

    private fun getResourceId(iconName: String): Int {
        val resId = resources.getIdentifier(iconName, "drawable", requireContext().packageName)
        return if (resId != 0) resId else IconProvider.icons.first() // Fallback
    }


    private fun getResourceName(resId: Int): String {
        return try {
            resources.getResourceEntryName(resId)
        } catch (e: Exception) {
            "ic_food"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
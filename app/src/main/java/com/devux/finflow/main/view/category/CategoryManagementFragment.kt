package com.devux.finflow.main.view.category

import android.app.AlertDialog
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.databinding.FragmentCategoryManagementBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryManagementFragment : BaseFragment<FragmentCategoryManagementBinding>(
    FragmentCategoryManagementBinding::inflate
) {
    private val viewModel: CategoryManageViewModel by viewModels()
    private lateinit var adapter: CategoryManageAdapter
    private var fullCategoryList: List<CategoryEntity> = emptyList()
    private var currentTabPosition = 0
    override fun initView() {
        setupRecyclerView()
        setupTabLayout()
    }

    override fun setAction() {
        // Nút Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Nút Thêm mới (+)
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryBottomSheet()
        }
    }

    override fun setObserve() {
        viewModel.categories.observe(viewLifecycleOwner) { list ->
            if (list != null) {
                fullCategoryList = list
                filterListByTab(currentTabPosition)
            }
        }

        // Lắng nghe thông báo lỗi/thành công
        viewModel.eventMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryManageAdapter { category ->
            showActionDialog(category)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoryManagementFragment.adapter

            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun setupTabLayout() {
        val colorRed = ContextCompat.getColor(requireContext(), R.color.orange_900)
        val colorGreen = ContextCompat.getColor(requireContext(), R.color.green_900)
        adapter.setIconTintColor(colorRed)
        binding.tabLayout.post {
            val indicator = binding.tabLayout.tabSelectedIndicator
            if (indicator != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(indicator).mutate(), colorRed)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabPosition = tab?.position ?: 0

                // Đổi màu Indicator
                val indicatorDrawable = binding.tabLayout.tabSelectedIndicator
                if (indicatorDrawable != null) {
                    val wrappedDrawable = DrawableCompat.wrap(indicatorDrawable).mutate()
                    if (currentTabPosition == 0) {
                        DrawableCompat.setTint(wrappedDrawable, colorRed)
                        adapter.setIconTintColor(colorRed)
                    } else {
                        DrawableCompat.setTint(wrappedDrawable, colorGreen)
                        adapter.setIconTintColor(colorGreen)
                    }
                }

                // Lọc lại danh sách
                filterListByTab(currentTabPosition)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filterListByTab(position: Int) {
        val targetType = if (position == 0) TransactionType.EXPENSE else TransactionType.INCOME
        val filteredList = fullCategoryList.filter { it.type == targetType }
        adapter.submitList(filteredList)
    }

    private fun showAddCategoryBottomSheet() {
        val targetType =
            if (currentTabPosition == 0) TransactionType.EXPENSE else TransactionType.INCOME

        // Gọi BottomSheet với tham số type (category = null)
        val bottomSheet = AddEditCategoryBottomSheet.newInstance(type = targetType)
        bottomSheet.show(childFragmentManager, AddEditCategoryBottomSheet.TAG)
    }

    private fun showEditCategoryBottomSheet(category: CategoryEntity) {
        // Gọi BottomSheet với tham số category
        val bottomSheet = AddEditCategoryBottomSheet.newInstance(category = category)
        bottomSheet.show(childFragmentManager, AddEditCategoryBottomSheet.TAG)
    }

    private fun showActionDialog(category: CategoryEntity) {
        val options = arrayOf("Chỉnh sửa", "Xóa")

        AlertDialog.Builder(requireContext()).setTitle("Tùy chọn: ${category.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditCategoryBottomSheet(category) // Gọi BottomSheet Sửa
                    1 -> showDeleteConfirmDialog(category) // Gọi Dialog Xóa
                }
            }.show()
    }

    private fun showDeleteConfirmDialog(category: CategoryEntity) {
        AlertDialog.Builder(requireContext()).setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục '${category.name}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteCategory(category)
            }.setNegativeButton("Hủy", null).show()
    }
}
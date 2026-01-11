package com.devux.finflow.main.view.analytics

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.data.model.CategoryStatModel
import com.devux.finflow.databinding.FragmentAnalyticsBinding
import com.devux.finflow.utils.CurrencyUtils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AnalyticsFragment :
    BaseFragment<FragmentAnalyticsBinding>(FragmentAnalyticsBinding::inflate) {

    private val viewModel: AnalyticsViewModel by viewModels()
    private lateinit var adapter: AnalyticsAdapter // (Bạn tự tạo adapter đơn giản từ item layout trên)

    override fun initView() {
        setupPieChart()
        setupRecyclerView()

        binding.btnPrevMonth.setOnClickListener { viewModel.prevMonth() }
        binding.btnNextMonth.setOnClickListener { viewModel.nextMonth() }
    }

    override fun setAction() {

    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setUsePercentValues(true)

            // Tạo hiệu ứng Donut (Lỗ tròn)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 55f
            transparentCircleRadius = 60f

            // Animation xoay
            animateY(1400, Easing.EaseInOutQuad)

            // Text ở giữa
            setDrawCenterText(true)
            setCenterTextSize(16f)
        }
    }

    override fun setObserve() {
        // Observe Tháng
        viewModel.currentMonth.observe(viewLifecycleOwner) { calendar ->
            val fmt = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            binding.tvCurrentMonth.text = "Tháng ${fmt.format(calendar.time)}"
        }

        // Observe Dữ liệu
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // 1. Cập nhật số liệu tổng
            binding.tvTotalIncome.text = CurrencyUtils.formatCurrency(state.totalIncome)
            binding.tvTotalExpense.text = CurrencyUtils.formatCurrency(state.totalExpense)
            binding.tvBalance.text = CurrencyUtils.formatCurrency(state.balance)

            // 2. Cập nhật Biểu đồ
            updateChart(state.expenseList, state.totalExpense)

            // 3. Cập nhật List
            adapter.submitList(state.expenseList)
            val colorRed = ContextCompat.getColor(requireContext(), R.color.orange_900)
            adapter.setIconTintColor(colorRed)
            // Text ở giữa biểu đồ
            binding.pieChart.centerText =
                "Tổng Chi\n${CurrencyUtils.formatCurrency(state.totalExpense)}"
        }
    }

    private fun updateChart(list: List<CategoryStatModel>, total: Double) {
        if (list.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.centerText = "Chưa có dữ liệu"
            return
        }

        val entries = list.map { PieEntry(it.amount.toFloat(), it.categoryName) }
        val colors = list.map { it.color }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.valueTextSize = 0f // Ẩn số trên biểu đồ cho đỡ rối (nhìn list bên dưới)

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.invalidate() // Vẽ lại
    }

    private fun setupRecyclerView() {
        adapter = AnalyticsAdapter()
        binding.rvAnalytics.adapter = adapter
        binding.rvAnalytics.layoutManager = LinearLayoutManager(requireContext())
    }
}
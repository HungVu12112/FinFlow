package com.devux.finflow.main.view.goals

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.databinding.FragmentGoalsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalsFragment : BaseFragment<FragmentGoalsBinding>(FragmentGoalsBinding::inflate) {

    private val viewModel: GoalViewModel by viewModels()
    private lateinit var goalAdapter: GoalAdapter

    override fun initView() {
        setupRecyclerView()
        setupToolbar()
    }

    override fun setAction() {
        // Nút thêm mới mục tiêu
        binding.fabAddGoal.setOnClickListener {
            goalAdapter = GoalAdapter { goal ->
                // Chuyển sang màn hình chi tiết, truyền object goal
                val action = GoalsFragmentDirections.actionGoalsFragmentToGoalDetailFragment(goal)
                findNavController().navigate(action)
            }
        }
    }

    override fun setObserve() {
        // Lắng nghe dữ liệu từ ViewModel
        viewModel.goals.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                // Hiển thị view "Chưa có dữ liệu" nếu cần
                binding.rvGoals.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                binding.rvGoals.visibility = View.VISIBLE
                binding.tvEmptyState.visibility = View.GONE
                goalAdapter.submitList(list)
            }
        }
    }

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter { goal ->
            findNavController().navigate(R.id.action_goalsFragment_to_goalDetailFragment)
        }

        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupToolbar() {
        // Setup toolbar nếu cần
    }

}
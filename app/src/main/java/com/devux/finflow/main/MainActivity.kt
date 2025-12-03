package com.devux.finflow.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.devux.finflow.R
import com.devux.finflow.base.BaseActivity
import com.devux.finflow.data.CategoryEntity
import com.devux.finflow.data.TransactionType
import com.devux.finflow.databinding.ActivityMainBinding
import com.devux.finflow.main.view.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.addTransactionFragment,R.id.categoryManagementFragment,R.id.addGoalFragment,R.id.goalDetailFragment
                    -> changeVisibleBottomNavigation(false)
                else -> changeVisibleBottomNavigation(true)
            }
        }
        setupBottomNavAnimation(binding.bottomNavigation)
    }

    override fun addEvent() {
        super.addEvent()
        binding.fab.setOnClickListener {
            navHostFragment.navController.navigate(R.id.addTransactionFragment)
        }
    }

    override fun initData() {
        super.initData()
        viewModel.allCategories.observe(this) { categories ->
            if (categories != null) {
                if (categories.isEmpty()) {
                    insertSampleData()
                }
                viewModel.allCategories.removeObservers(this)
            }
        }
    }

    fun changeVisibleBottomNavigation(isVisible: Boolean) {
        if (isVisible) {
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
            binding.bottomBackground.visibility = View.VISIBLE
        } else {
            binding.bottomNavigation.visibility = View.GONE
            binding.bottomAppBar.visibility = View.GONE
            binding.fab.visibility = View.GONE
            binding.bottomBackground.visibility = View.GONE
            changHeightBottomBar(0)
        }
    }

    fun changHeightBottomBar(height: Int) {
        val params = binding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = height
        binding.navHostFragment.layoutParams = params
    }

    private fun setupBottomNavAnimation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            // Animate icon của item được chọn
            val view = bottomNav.findViewById<View>(item.itemId)
            view?.let {
                val scaleUp = AnimationUtils.loadAnimation(this, R.anim.nav_item_scale_up)
                it.startAnimation(scaleUp)
            }

            // Navigate đến destination
            when (item.itemId) {
                R.id.homeFragment, R.id.analyticsFragment,
                R.id.goalsFragment, R.id.profileSettingsFragment -> {
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(item.itemId)
                    true
                }

                else -> false
            }
        }
        bottomNav.setOnItemReselectedListener { item ->
            val view = bottomNav.findViewById<View>(item.itemId)
            view?.let {
                val scaleDown = AnimationUtils.loadAnimation(this, R.anim.nav_item_scale_down)
                val scaleUp = AnimationUtils.loadAnimation(this, R.anim.nav_item_scale_up)
                it.startAnimation(scaleDown)
                it.postDelayed({ it.startAnimation(scaleUp) }, 200)
            }
        }
    }

    private fun insertSampleData() {
        val sampleCategories = listOf(
            CategoryEntity(name = "Lương", icon = "ic_salary", type = TransactionType.INCOME),
            CategoryEntity(name = "Lợi nhuận", icon = "ic_profit", type = TransactionType.INCOME),
            CategoryEntity(name = "Thưởng", icon = "ic_bonus", type = TransactionType.INCOME),
            CategoryEntity(
                name = "Thu nhập khác",
                icon = "ic_other_income",
                type = TransactionType.INCOME
            ),
            CategoryEntity(name = "Ăn uống", icon = "ic_food", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Đi lại", icon = "ic_transport", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Thuê nhà", icon = "ic_house", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Sức khỏe", icon = "ic_heart", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Cà phê", icon = "ic_cafe", type = TransactionType.EXPENSE),
            CategoryEntity(
                name = "Giải trí",
                icon = "ic_entertainment",
                type = TransactionType.EXPENSE
            ),
            CategoryEntity(
                name = "Mua sắm",
                icon = "ic_shopping",
                type = TransactionType.EXPENSE
            )
        )

        // 4. Gọi ViewModel để chèn
        sampleCategories.forEach { category ->
            viewModel.insertCategory(category)
        }
    }
}
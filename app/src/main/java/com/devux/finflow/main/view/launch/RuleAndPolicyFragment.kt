package com.devux.finflow.main.view.launch

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.databinding.FragmentRuleAndPolicyBinding
import com.devux.finflow.helper.PreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RuleAndPolicyFragment(

) : BaseFragment<FragmentRuleAndPolicyBinding>(
    FragmentRuleAndPolicyBinding::inflate
) {

    @Inject
    lateinit var sharedPreferences: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.cbPolicy.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.btnNext.isEnabled = isChecked
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }

    override fun initView() {}

    override fun setAction() {
        binding.btnNext.setOnClickListener {
            sharedPreferences.setFirstLaunch()
            findNavController().navigate(R.id.action_ruleAndPolicyFragment_to_loginFragment)
        }
    }

    override fun setObserve() {
    }
}
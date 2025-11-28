package com.devux.finflow.main.view.launch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.devux.finflow.R
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.databinding.FragmentSplashBinding
import com.devux.finflow.helper.PreferencesHelper
import com.devux.finflow.helper.UserManager
import com.devux.finflow.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    @Inject
    lateinit var sharedPreferences: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.drawable.splash_zoom_in)
        binding.iconApp.startAnimation(animation)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!sharedPreferences.isFirstLaunch()) {
                findNavController().navigate(R.id.action_splashFragment_to_ruleAndPolicyFragment)
            } else {
                if (UserManager.currentSelectedUser != -1L) {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        }, 1800)
    }

    override fun initView() {}


    override fun setAction() {
    }

    override fun setObserve() {
    }
}
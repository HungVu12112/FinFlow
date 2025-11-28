package com.devux.finflow.main.view.launch

import android.content.Intent
import android.os.Bundle
import com.devux.finflow.FinFlowApplication.Companion.isConnectInternet
import com.devux.finflow.databinding.FragmentLoginBinding
import com.devux.finflow.main.MainActivity
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.comon.visible
import com.devux.finflow.helper.PreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.jvm.java
@AndroidEntryPoint
class   LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    @Inject
    lateinit var sharedPreferences: PreferencesHelper
    private var isFromMain = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun initView() {
        isFromMain = requireActivity().intent.getBooleanExtra("isFromMain", false)
        if (sharedPreferences.getIsLogin() && !isFromMain) {
            binding.tvUsingAppWithoutLogin.visible()
        }
    }


    override fun setAction()= with(binding) {
        tvUsingAppWithoutLogin.setOnClickListener {
//            Show Alert Dialog
        }
        startActivity(Intent(requireContext(), MainActivity::class.java))
        btnLogin.setOnClickListener {
            if (isConnectInternet) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
//                Sign In With Google
            } else {
//                Show Toast No Internet
            }
        }
    }

    override fun setObserve() {
    }

}
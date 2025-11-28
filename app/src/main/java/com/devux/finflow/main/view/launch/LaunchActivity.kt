package com.devux.finflow.main.view.launch

import android.os.Bundle
import com.devux.finflow.databinding.ActivityLaunchBinding
import com.devux.finflow.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchActivity : BaseActivity<ActivityLaunchBinding>(ActivityLaunchBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
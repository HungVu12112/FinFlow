package com.devux.finflow.main.view.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devux.finflow.base.BaseFragment
import com.devux.finflow.databinding.FragmentGoalsBinding

class GoalsFragment : BaseFragment<FragmentGoalsBinding>(FragmentGoalsBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {}


    override fun setAction() {
    }

    override fun setObserve() {
    }

}
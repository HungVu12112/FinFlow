package com.devux.finflow.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.devux.finflow.comon.LoadingDialogFragment

abstract class BaseFragment<T : ViewBinding>(private val inflate: (LayoutInflater) -> T) :
    Fragment() {

    val binding: T by lazy { inflate(layoutInflater) }
    private var isLoading = false
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    abstract fun initView()

    abstract fun setAction()

    abstract fun setObserve()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setAction()
        setObserve()
    }

    fun updateStatusErrorMessage(message: String?, et: EditText, tv: TextView) {
        var isShow = false
        if (message.isNullOrBlank().not()) {
            isShow = true
            tv.text = message
        }
        et.isActivated = isShow
        tv.visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun startLoading() {
        if (isLoading.not()) {
            isLoading = true
            loadingDialog.show(childFragmentManager, LoadingDialogFragment::class.java.simpleName)
        }
    }

    fun finishLoading() {
        if (isLoading) {
            loadingDialog.dismiss()
            isLoading = false
        }
    }

    private fun setActionBar(isVisible: Boolean) {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            if (isVisible) {
                show()
                setDisplayHomeAsUpEnabled(true)
            } else {
                hide()
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }
}

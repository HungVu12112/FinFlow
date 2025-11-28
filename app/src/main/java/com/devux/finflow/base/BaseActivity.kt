package com.devux.finflow.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.viewbinding.ViewBinding
import com.devux.finflow.FinFlowApplication.Companion.isConnectInternet
import com.devux.finflow.comon.Logger
import com.devux.finflow.comon.NONE

abstract class BaseActivity<T : ViewBinding>(private val inflater: (LayoutInflater) -> T) :
    AppCompatActivity() {
    protected val binding: T by lazy { inflater(layoutInflater) }
    private lateinit var dialog: Dialog
    private lateinit var mConnectivityManager: ConnectivityManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallBack)

        setupLoading()
        initViewModel()
        initView()
        addEvent()
        addObservers()
        initData()
    }
    private val mNetworkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Logger.d("Base network on available")
            isConnectInternet = true
        }

        override fun onLost(network: Network) {
            Logger.d("Base network on lost ")
            isConnectInternet = false
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, NONE)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    open fun initViewModel() {}
    open fun initView() {}
    open fun addEvent() {}
    open fun addObservers() {}
    open fun initData() {}


    private fun setupLoading() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setContentView(ProgressBar(this))
    }

    fun showLoading() {
        dialog.show() // to show this dialog
    }

    fun hideLoading() {
        dialog.dismiss() // to hide this dialog
    }
}

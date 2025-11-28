package com.tta.futurenest.view.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devux.finflow.comon.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    protected fun <T> launchAsync(
        request: suspend CoroutineScope.() -> DataResult<T>,
        onSuccess: (T) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        showLoading()
        viewModelScope.launch {
            when (val response = request(this)) {
                is DataResult.Success -> {
                    onSuccess(response.data)
                    hideLoading()
                }
                is DataResult.Error -> {
                    onError(response.exception)
                    hideLoading()
                }
            }
        }
    }

    private fun showLoading() {
        _isLoading.value = true
    }

    private fun hideLoading() {
        _isLoading.value = false
    }
}

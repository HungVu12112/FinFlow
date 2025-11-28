package com.devux.finflow.comon

import android.app.Application

class BaseApplication : Application() {
    companion object {
        var jsessionid: String? = null
    }
}
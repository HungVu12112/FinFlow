package com.devux.finflow.comon

import android.util.Patterns
import android.view.View

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}
private fun validateUrl(url : String) : Boolean{  ///// validate các đường dẫn như miền : "http://192.168.51.141"
    val pattern = Patterns.WEB_URL
    return pattern.matcher(url).matches()
}



package com.devux.finflow.helper

import android.annotation.SuppressLint
import android.content.Context
import com.devux.finflow.R
import com.devux.finflow.comon.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object UserManager {
    // Quản lý đăng nhập Google
    @SuppressLint("StaticFieldLeak")
    lateinit var googleClient: GoogleSignInClient

    private lateinit var prefs: PreferencesHelper

    const val UNKNOWN_USER = -1L
    const val USER_GUEST_SERVER_ID = "USER_GUEST_SERVER_ID"

    /**
     * Khởi tạo UserManager với context và PreferencesHelper từ DI
     */
    fun init(context: Context, preferencesHelper: PreferencesHelper) {
        this.prefs = preferencesHelper

        val googleClientId = context.getString(R.string.default_web_client_id)
        Logger.d("Google Client Id : $googleClientId")

        if (!::googleClient.isInitialized) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(googleClientId)
                .build()

            googleClient = GoogleSignIn.getClient(context, gso)
        }
    }

    /**
     * Xóa toàn bộ dữ liệu cache (in-RAM). Gọi khi logout.
     */
    fun removeCacheData() {
        googleClient.signOut()
    }

    var isLogin: Boolean
        get() = prefs.getIsLogin()
        set(value) = prefs.saveIsLogin(value)

    var currentSelectedUser: Long
        get() = prefs.getCurrentSelectedUser()
        set(value) = prefs.setCurrentSelectedUser(value)

    fun hasGuestAccount(): Boolean = currentSelectedUser != UNKNOWN_USER

    fun logout() {
        removeCacheData()
        isLogin = false
        currentSelectedUser = UNKNOWN_USER
        prefs.saveToken("")
    }
}

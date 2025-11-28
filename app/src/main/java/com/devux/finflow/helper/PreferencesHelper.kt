package com.devux.finflow.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

@Suppress("PrivatePropertyName")
class PreferencesHelper(context: Context) {
    private val mApplicationContext = context.applicationContext
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    private val TAG = PreferencesHelper::class.java.simpleName

    fun registerOnSharedPreferenceChangeListener(preferenceChangedListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangedListener)
    }

    fun unregisterOnSharedPreferenceChangeListener(preferenceChangedListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangedListener)
    }

    fun getContext(): Context {
        return LocaleManager.setLocale(mApplicationContext)
    }

    fun saveToken(token: String) {
        setString(TOKEN_LOGIN, token)
    }

    fun getToken(): String {
        return getString(TOKEN_LOGIN)
    }

    fun saveIsLogin(isLogin: Boolean) {
        setBoolean(IS_LOGIN, isLogin)
    }

    fun getIsLogin(): Boolean {
        return getBoolean(IS_LOGIN)
    }

    fun clear() {
        sharedPreferences.edit() { clear() }
    }

    fun isFirstLaunch(): Boolean {
        return getBoolean(IS_FIRST_LAUNCH, false)
    }

    fun setFirstLaunch() {
        setBoolean(IS_FIRST_LAUNCH, true)
    }

    fun setCurrentSelectedUser(id: Long) {
        setLong(CURRENT_SELECTED_USER, id)
    }

    fun getCurrentSelectedUser(): Long {
        return getLong(CURRENT_SELECTED_USER, -1L)
    }

    // key để có số lần chat
    fun setNumberOfChat(numberOfChat: Int) {
        setInt(NUMBER_OF_CHAT, numberOfChat)
    }

    fun getNumberOfChat(): Int {
        return getInt(NUMBER_OF_CHAT, 3)
    }

    /**
     * ======= ======= ======= ======= ======= ======= ======= ======= ======= =======
     * SharedPreferences helper methods
     * ======= ======= ======= ======= ======= ======= ======= ======= ======= =======
     * */
    // String
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().apply {
            putString(key, value)
            apply()
        }
    }

    // Boolean
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

    // Int
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().apply {
            putInt(key, value)
            apply()
        }
    }

    // Long
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().apply {
            putLong(key, value)
            apply()
        }
    }

    // Float
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun setLong(key: String, value: Float) {
        sharedPreferences.edit().apply {
            putFloat(key, value)
            apply()
        }
    }
}

const val TOKEN_LOGIN = "TOKEN_LOGIN"
const val IS_LOGIN = "IS_LOGIN"
const val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
const val CURRENT_SELECTED_USER = "CURRENT_SELECTED_USER"
const val NUMBER_OF_CHAT = "NUMBER_OF_CHAT"
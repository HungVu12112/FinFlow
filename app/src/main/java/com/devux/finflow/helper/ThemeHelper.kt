package com.devux.finflow.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.devux.finflow.R

class ThemeHelper(private val context: Context) {
    companion object {
        const val PREFERENCE_NAME = "app_theme_pref"
        const val THEME_KEY = "current_theme"

        const val THEME_BLUE_OCEAN = 0
        const val THEME_GREEN_NATURE = 1
        const val THEME_PURPLE_ELEGANT = 2
        const val THEME_ORANGE_WARM = 3
        const val THEME_TEAL_MODERN = 4
        const val THEME_TEAL_BREEZE = 5 // Legacy theme

        // Dark theme variants
        const val THEME_BLUE_OCEAN_DARK = 6
        const val THEME_GREEN_NATURE_DARK = 7

        // Default theme
        const val THEME_DEFAULT = 8
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getCurrentTheme(): Int {
        return preferences.getInt(THEME_KEY, THEME_DEFAULT)
    }

    fun setTheme(themeId: Int) {
        preferences.edit().putInt(THEME_KEY, themeId).apply()
    }

    fun applyTheme(activity: AppCompatActivity) {
        when (getCurrentTheme()) {
            THEME_BLUE_OCEAN -> activity.setTheme(R.style.Theme_BlueOcean)
            THEME_GREEN_NATURE -> activity.setTheme(R.style.Theme_GreenNature)
            THEME_PURPLE_ELEGANT -> activity.setTheme(R.style.Theme_PurpleElegant)
            THEME_ORANGE_WARM -> activity.setTheme(R.style.Theme_OrangeWarm)
            THEME_TEAL_MODERN -> activity.setTheme(R.style.Theme_TealModern)
            THEME_TEAL_BREEZE -> activity.setTheme(R.style.Theme_TealBreeze)
            THEME_BLUE_OCEAN_DARK -> activity.setTheme(R.style.Theme_BlueOcean_Dark)
            THEME_GREEN_NATURE_DARK -> activity.setTheme(R.style.Theme_GreenNature_Dark)
            THEME_DEFAULT -> activity.setTheme(R.style.Theme_MoneySave)
            else -> activity.setTheme(R.style.Theme_MoneySave)
        }
    }

    fun getThemeName(themeId: Int): String {
        return when (themeId) {
            THEME_BLUE_OCEAN -> "Blue Ocean"
            THEME_GREEN_NATURE -> "Green Nature"
            THEME_PURPLE_ELEGANT -> "Purple Elegant"
            THEME_ORANGE_WARM -> "Orange Warm"
            THEME_TEAL_MODERN -> "Teal Modern"
            THEME_TEAL_BREEZE -> "Teal Breeze"
            THEME_BLUE_OCEAN_DARK -> "Blue Ocean Dark"
            THEME_GREEN_NATURE_DARK -> "Green Nature Dark"
            THEME_DEFAULT -> "Default"
            else -> "Unknown"
        }
    }

    fun getAvailableThemes(): List<Pair<Int, String>> {
        return listOf(
            THEME_DEFAULT to "Default",
            THEME_BLUE_OCEAN to "Blue Ocean",
            THEME_GREEN_NATURE to "Green Nature",
            THEME_PURPLE_ELEGANT to "Purple Elegant",
            THEME_ORANGE_WARM to "Orange Warm",
            THEME_TEAL_MODERN to "Teal Modern",
            THEME_TEAL_BREEZE to "Teal Breeze (Legacy)",
            THEME_BLUE_OCEAN_DARK to "Blue Ocean Dark",
            THEME_GREEN_NATURE_DARK to "Green Nature Dark"
        )
    }

    fun isDarkTheme(themeId: Int): Boolean {
        return when (themeId) {
            THEME_BLUE_OCEAN_DARK, THEME_GREEN_NATURE_DARK -> true
            else -> false
        }
    }
//    bước 1 : Khởi tạo file
//    ThemeHelper(this).applyTheme(this)
//
//    bước 2 : chọn màu nền
//    fun onThemeSelected(themeId: Int) {
//        val themeHelper = ThemeHelper(this)
//        themeHelper.setTheme(themeId) // Lưu theme được chọn
//
//        // Cập nhật lại giao diện bằng cách khởi động lại activity
//        recreate() // hoặc finish() + startActivity(...)
//    }
}
package com.devux.finflow.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LocaleManager {
    const val LANGUAGE_SELECTED = "LANGUAGE_SELECTED"

    @JvmField
    var MODE_AUTO = "auto"
    fun setLocale(c: Context): Context {
        return updateResources(c, getLanguage(c))
    }

    @JvmStatic
    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(c, language)
        return updateResources(c, language)
    }

    fun setDefaultLocale(c: Context): Context {
        return updateResources(c, MODE_AUTO)
    }

    @JvmStatic
    fun getLanguage(context: Context?): String {
        if (context == null) return MODE_AUTO
        val prefs: SharedPreferences =
            context.getSharedPreferences("${context.packageName}.lang", Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_SELECTED, MODE_AUTO) ?: MODE_AUTO
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(context: Context, language: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("${context.packageName}.lang", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(LANGUAGE_SELECTED, language)
        editor.apply()
    }

    internal fun updateResources(context: Context, language: String): Context {
        val locale: Locale = if (language == MODE_AUTO) {
            Resources.getSystem().configuration.locale
        } else {
            if (language == "zh-rCN" || language == "zh") {
                Locale.SIMPLIFIED_CHINESE
            } else if (language == "zh-rTW") {
                Locale.TRADITIONAL_CHINESE
            } else {
                val spk = language.split("-".toRegex()).toTypedArray()
                if (spk.size > 1) {
                    Locale(spk[0], spk[1])
                } else {
                    Locale(spk[0])
                }
            }
        }
        Locale.setDefault(locale)
        return updateResourcesLocaleLegacy(context, locale)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    @JvmStatic
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales[0] else config.locale
    }

    fun formatDate(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }
}
package com.devux.finflow.utils // Đổi package name cho đúng của bạn

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyUtils {

    fun formatCurrency(amount: Double): String {
        // Tạo định dạng số với Locale Việt Nam hoặc tùy chỉnh
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.groupingSeparator = '.' // Dùng dấu chấm phân cách hàng nghìn
        symbols.decimalSeparator = ',' // Dùng dấu phẩy phân cách thập phân (nếu có)

        // Pattern #,### nghĩa là cứ 3 số thì gom nhóm
        val decimalFormat = DecimalFormat("#,###", symbols)

        return decimalFormat.format(amount)
    }
}
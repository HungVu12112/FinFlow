package com.devux.finflow.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class NumberTextWatcher(private val editText: EditText) : TextWatcher {

    private val df: DecimalFormat
    private val dfnd: DecimalFormat
    private var hasFractionalPart: Boolean = false

    init {
        // Cấu hình định dạng: Dấu chấm (.) phân cách hàng nghìn
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','

        // Pattern #,### nghĩa là cứ 3 số thì gom nhóm
        df = DecimalFormat("#,###.##", symbols)
        df.isDecimalSeparatorAlwaysShown = true
        dfnd = DecimalFormat("#,###", symbols)
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return

        // 1. Tạm thời gỡ listener để tránh vòng lặp vô tận khi setText
        editText.removeTextChangedListener(this)

        try {
            val iniLen = editText.text.length

            // 2. Xóa hết các dấu chấm cũ để lấy số thô (Raw number)
            // Ví dụ: "1.000" -> "1000"
            val v = s.toString().replace(".", "")

            // 3. Parse và format lại
            val n = v.toDoubleOrNull() ?: 0.0
            val cp = editText.selectionStart

            if (hasFractionalPart) {
                editText.setText(df.format(n))
            } else {
                editText.setText(dfnd.format(n))
            }

            // 4. Đặt lại con trỏ chuột đúng vị trí (tránh bị nhảy về đầu)
            val endLen = editText.text.length
            val sel = (cp + (endLen - iniLen))
            if (sel > 0 && sel <= editText.text.length) {
                editText.setSelection(sel)
            } else {
                // Trường hợp con trỏ bị đẩy ra ngoài
                editText.setSelection(editText.text.length)
            }
        } catch (nfe: NumberFormatException) {
            // Bỏ qua lỗi
        } catch (e: Exception) {
            // Bỏ qua lỗi
        }

        // 5. Gắn lại listener
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.let {
            // Kiểm tra xem có phần thập phân không (dấu phẩy)
            hasFractionalPart = it.toString().contains(",")
        }
    }
}
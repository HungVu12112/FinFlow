package com.devux.finflow.comon

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.devux.finflow.R

class LoadingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.view_progress)
        dialog.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            statusBarColor = requireActivity().getColor(R.color.black)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0f)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return dialog
    }
}
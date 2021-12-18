package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogSearchBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import java.lang.Exception

class SearchDialog {

    private lateinit var binding: DialogSearchBinding
    private lateinit var dialog: Dialog

    interface SearchListener {
        fun searchType(searchType: Int)
    }

    lateinit var listener: SearchListener

    fun show(searchListener: SearchListener) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogSearchBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.window?.attributes?.windowAnimations = R.style.ExpandAnimation
        TypefaceUtil.overrideFonts(dialog.window?.decorView, MyApplication.IraSanSMedume)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.TOP or Gravity.RIGHT
        }
        if (wlp != null) {
            wlp.windowAnimations = R.style.ExpandAnimation
        }
        dialog.window?.attributes = wlp
        if (wlp != null) {
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        }
        dialog.setCancelable(true)
        KeyBoardHelper.hideKeyboard()

        this.listener = searchListener

        binding.llCustomerName.setOnClickListener {
            listener.searchType(2)
            dismiss()
        }

        binding.llMobile.setOnClickListener {
            listener.searchType(1)
            dismiss()
        }

        binding.llAddress.setOnClickListener {
            listener.searchType(3)
            dismiss()
        }

        dialog.show()
    }

    private fun dismiss() {
        try {
            if (dialog != null) {
                if (dialog.isShowing)
                    dialog.dismiss()
                KeyBoardHelper.hideKeyboard()
            }
//            dialog = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
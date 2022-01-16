package ir.food.operatorAndroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogCancelOrderBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.webService.CancelOrder

class CancelDialogOrder {

    lateinit var dialog: Dialog
    lateinit var binding: DialogCancelOrderBinding

    interface CancelOrderDialog {
        fun onSuccess(b: Boolean)
    }

    lateinit var listener: CancelOrderDialog


    fun show(orderId: String, listener: CancelOrderDialog) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCancelOrderBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        this.listener = listener

        binding.imgClose.setOnClickListener {
            MyApplication.handler.postDelayed({
                dismiss()
                KeyBoardHelper.hideKeyboard()
            }, 200)
        }

        binding.btnCancelOrder.setOnClickListener {
            
            if(binding.edtReason.text.trim().isEmpty()){
                binding.edtReason.error = "دلیل کنسلی را بنویسید"
                return@setOnClickListener
            }

            GeneralDialog()
                .message("ایا از لغو سفارش اطمینان دارید؟")
                .firstButton("بله") {
                    binding.vfCancelOrder.displayedChild = 1
                    CancelOrder().callCancelAPI(orderId,binding.edtReason.text.trim().toString() , object : CancelOrder.CancelOrder {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSuccess(b: Boolean) {
                            binding.vfCancelOrder.displayedChild = 0
                            dismiss()
                            listener.onSuccess(b)
                        }
                    })
                }
                .secondButton("خیر") { }
                .cancelable(false)
                .show()
        }

        dialog.show()
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "CallDialog class, dismiss method")
        }
    }

}
package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogRegisterComplaintBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONObject

class RegisterComplaintDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogRegisterComplaintBinding

    fun show(originId: Int, origin: String, destId: Int, dest: String) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogRegisterComplaintBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

//        binding.btnSubmit.setOnClickListener {
//            val originAdrs = binding.edtSourceAddress.text
//            val destAdrs = binding.edtDestAddress.text
//
//            editAddress(originId,originAdrs.toString(),destId,destAdrs.toString())
//        }
//
//        binding.imgClose.setOnClickListener {
//            MyApplication.handler.postDelayed({
//                dismiss()
//                KeyBoardHelper.hideKeyboard()
//            }, 200)
//
//        }

        dialog.show()

    }

    private fun editAddress(originId: Int, origin: String, destId: Int, dest: String) {
//        binding.vfLoader.displayedChild = 1
        RequestHelper.builder(EndPoints.EDIT_ADDRESS)
            .listener(driverLockCallBack)
            .addParam("sourceAddressId", originId)
            .addParam("sourceAddress", origin)
            .addParam("destinationAddressId", destId)
            .addParam("destinationAddress", dest)
            .put()
    }

    private val driverLockCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
//                        binding.vfLoader.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            GeneralDialog().message(message).firstButton("باشه") {}.show()
                            dismiss()
                        }else{
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        binding.vfLoader.displayedChild = 0
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
//                    binding.vfLoader.displayedChild = 0
                }
            }
        }

    private fun dismiss() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
            AvaCrashReporter.send(e, "EditAddressDialog class, dismiss method")
        }
    }


}
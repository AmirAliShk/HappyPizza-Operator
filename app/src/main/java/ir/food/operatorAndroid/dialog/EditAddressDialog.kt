package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogEditAddressBinding
import ir.food.operatorAndroid.databinding.DialogRegisterComplaintBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.AddressModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONObject

class EditAddressDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogEditAddressBinding
    lateinit var listener: Listener

    interface Listener {
        fun address(address: String?)
    }

    fun show(orderId: String, address: String, listener: Listener) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogEditAddressBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(false)
        this.listener = listener

        binding.edtAddress.setText(address)

        binding.btnSubmit.setOnClickListener {
            val adrs = binding.edtAddress.text.toString()

            if (adrs.isEmpty()) {
                MyApplication.Toast("متن ادرس را وارد کنید", Toast.LENGTH_LONG)
                binding.edtAddress.requestFocus()
                return@setOnClickListener
            }
            changeAddress(adrs, orderId)
        }

        binding.imgClose.setOnClickListener {
            MyApplication.handler.postDelayed({
                dismiss()
                KeyBoardHelper.hideKeyboard()
            }, 200)
        }

        dialog.show()
    }

    private fun changeAddress(adrs: String, id: String) {
        binding.vfLoader.displayedChild = 1
        RequestHelper.builder(EndPoints.EDIT_ADDRESS)
            .listener(changeAddressCallBack)
            .addParam("orderId", id)
            .addParam("adrs", adrs)
            .put()
    }

    private val changeAddressCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfLoader.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
                                listener.address(binding.edtAddress.text.toString())
                                GeneralDialog().message(message).firstButton("باشه") { dismiss() }
                                    .show()
                            } else {
                                GeneralDialog().message(message).secondButton("باشه") {}.show()
                            }
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfLoader.displayedChild = 0
                        AvaCrashReporter.send(
                            e,
                            "EditAddressDialog class, changeAddressCallBack method"
                        )
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfLoader.displayedChild = 0
                }
            }
        }

    private fun dismiss() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditAddressDialog class, dismiss method")
        }
    }
}
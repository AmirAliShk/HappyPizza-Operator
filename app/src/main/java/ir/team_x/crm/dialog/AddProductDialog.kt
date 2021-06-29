package ir.team_x.crm.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.DialogAddProductBinding
import ir.team_x.crm.dialog.ErrorDialog.dialog
import ir.team_x.crm.helper.KeyBoardHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONObject

class AddProductDialog {
    private lateinit var dialog: Dialog
    private lateinit var binding: DialogAddProductBinding

    fun show() {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAddProductBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)



        dialog.show()

    }

//    private fun finish(serviceId: Int, price: String) {
//        RequestHelper.builder(EndPoints.)
//            .listener(finishCallBack)
//            .addParam("serviceId", serviceId)
//            .addParam("price", price)
//            .post()
//    }
//
//    private val finishCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
//        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
//            MyApplication.handler.post {
//                try {
//                    val jsonObject = JSONObject(args[0].toString())
//                    val success = jsonObject.getBoolean("success")
//                    val message = jsonObject.getString("message")
//                    if (success) {
//                        val dataArr = jsonObject.getJSONArray("data")
//                        val result = dataArr.getJSONObject(0).getBoolean("result")
//                        if (result) {
//                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
//                            dismiss()
//                            MyApplication.currentActivity.onBackPressed()
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
//            MyApplication.handler.post {
//
//            }
//        }
//    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
        }
    }
}
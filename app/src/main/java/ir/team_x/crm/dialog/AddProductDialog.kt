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
import ir.team_x.crm.helper.KeyBoardHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONObject

class AddProductDialog {
    private lateinit var dialog: Dialog
    private lateinit var binding: DialogAddProductBinding

    interface Data{
        fun name(name : String)
        fun price(price : String)
        fun description(description : String)
    }

    lateinit var listener: Data

    fun show(fromWhere : String, listener : Data) {
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

        binding.btnSubmit.setOnClickListener { addProduct() }

        dialog.show()

    }

    private fun addProduct() {
        LoadingDialog.makeLoader()
        RequestHelper.builder(EndPoints.PRODUCT)
            .addParam("name", binding.edtProductName.text.toString())
            .addParam("sellingPrice", binding.edtPrice.text.toString())
            .addParam("description", binding.edtDescription.text.toString())
            .listener(addProductCallBack)
            .post()
    }

    private val addProductCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"محصول شما با موفقیت ثبت شد"}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        LoadingDialog.dismiss()
                        GeneralDialog()
                            .message(message)
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                        dismiss()
                    }

                } catch (e: Exception) {
                    LoadingDialog.dismiss()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                LoadingDialog.dismiss()
            }
        }
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
        }
    }
}
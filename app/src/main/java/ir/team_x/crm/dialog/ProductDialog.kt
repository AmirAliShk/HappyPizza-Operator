package ir.team_x.crm.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Toast
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.DialogProductBinding
import ir.team_x.crm.helper.KeyBoardHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.model.ProductsModel
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONObject

class ProductDialog {
    private lateinit var dialog: Dialog
    private lateinit var binding: DialogProductBinding
    private lateinit var model: ProductsModel

    interface Refresh {
        fun refresh(refresh: Boolean)
    }

    lateinit var listener: Refresh

    fun show(model: ProductsModel?, fromWhere: String, listener: Refresh) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogProductBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)
        this.listener = listener

        binding.edtProductName.requestFocus()

        if (fromWhere == "addProduct") {
            binding.rgStatus.visibility = View.GONE
        }

        if (model != null) {
            this.model = model
            if (model.active) {
                binding.rbActive.isChecked = true
            } else {
                binding.rbDeActive.isChecked = true
            }
            binding.edtProductName.setText(model.name)
            binding.edtPrice.setText(model.sellingPrice)
            binding.edtDescription.setText(model.description)
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.edtProductName.text.trim().isEmpty() || binding.edtPrice.text.isEmpty() || binding.edtPrice.text.toString() == "0") {
                MyApplication.Toast("لطفا تمام موارد را وارد کنید.", Toast.LENGTH_SHORT)
            } else {
                if (fromWhere == "addProduct") {
                    addProduct()
                } else {
                    editProduct()
                }
            }
        }

        binding.imgClose.setOnClickListener { dismiss() }

        dialog.setOnDismissListener {
            listener.refresh(true)
        }

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

    private fun editProduct() {
        if (binding.rbActive.isChecked) {
            model.active = true
        } else if (binding.rbDeActive.isChecked) {
            model.active = false
        }
        LoadingDialog.makeLoader()
        RequestHelper.builder(EndPoints.PRODUCT)
            .addParam("_id", model.id)
            .addParam("active", model.active)
            .addParam("name", binding.edtProductName.text.toString())
            .addParam("sellingPrice", binding.edtPrice.text.toString())
            .addParam("description", binding.edtDescription.text.toString())
            .listener(addProductCallBack)
            .put()
    }

    private val addProductCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"محصول شما با موفقیت ثبت شد"}
//                    {"success":false,"message":"محصول وارد شده، موجود است"}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    LoadingDialog.dismiss()

                    GeneralDialog()
                        .message(message)
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .show()
                    dismiss()

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
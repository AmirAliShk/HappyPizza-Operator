package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogOverlayPermissionBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.webService.GetAppInfo

class OverlayPermissionDialog {

    var dialog: Dialog = Dialog(MyApplication.currentActivity)
    lateinit var binding: DialogOverlayPermissionBinding

    fun show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
//        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogOverlayPermissionBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        dialog.window?.attributes = wlp
        wlp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)

        binding.btnGoToSetting.setOnClickListener {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.data = Uri.parse("package:" + MyApplication.currentActivity.packageName)
            MyApplication.currentActivity.startActivityForResult(myIntent, 107)
            dialog.dismiss()
        }

        binding.btnDismiss.setOnClickListener {
            GetAppInfo().callAppInfoAPI()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
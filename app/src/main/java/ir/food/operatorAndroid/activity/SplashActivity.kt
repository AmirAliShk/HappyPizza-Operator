package ir.food.operatorAndroid.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivitySplashBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.OverlayPermissionDialog
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.webService.GetAppInfo
import org.acra.ACRA

class SplashActivity : AppCompatActivity() {
    var TAG = SplashActivity::class.java
    private lateinit var binding: ActivitySplashBinding
    private val permission = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        TypefaceUtil.overrideFonts(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        ACRA.errorReporter.putCustomData("LineCode", MyApplication.prefManager.userCode.toString())
        ACRA.errorReporter.putCustomData("projectId", MyApplication.prefManager.pushId.toString())
        MyApplication.handler.postDelayed(
            {
                checkPermission()
            }, 500
        )
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val hasAudioPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            if ((hasAudioPermission != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1
                )
            } else if (!Settings.canDrawOverlays(MyApplication.context)) {
                OverlayPermissionDialog().show()
            } else {
                GetAppInfo().callAppInfoAPI()
            }
        } else {
            GetAppInfo().callAppInfoAPI()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 107) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)
                GetAppInfo().callAppInfoAPI()
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        MyApplication.prefManager.isAppRun = true;
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.isAppRun = false
        KeyBoardHelper.hideKeyboard()
    }

    override fun onBackPressed() {
        GeneralDialog()
            .message("آیا از خروج خود اطمینان دارید؟")
            .firstButton("بله") {
                finish()
            }
            .secondButton("خیر") {}
            .show()
    }
}
package ir.food.operatorAndroid.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivitySplashBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.ServiceHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService

class Splash : AppCompatActivity() {
    var TAG = Splash::class.java
    private lateinit var binding: ActivitySplashBinding
    private val permission = arrayOf(Manifest.permission.RECORD_AUDIO)

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
        }

        MyApplication.prefManager.pushToken = "pizzaOperatorAABMohsenX"
        MyApplication.prefManager.pushId = 12
        MyApplication.prefManager.setUserCode("1")

        startVoipService()

        MyApplication.handler.postDelayed(
            {
            checkPermission()
            }, 1500
        )

    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val hasAudioPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            if (hasAudioPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1
                )
            } else {
//                GetAppInfo().callAppInfoAPI()
                MyApplication.currentActivity.startActivity(
                    Intent(
                        MyApplication.currentActivity,
                        MainActivity::class.java
                    )
                )
                MyApplication.currentActivity.finish()
            }
        }else{
//            GetAppInfo().callAppInfoAPI()
            MyApplication.currentActivity.startActivity(
                Intent(
                    MyApplication.currentActivity,
                    MainActivity::class.java
                )
            )
            MyApplication.currentActivity.finish()
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

    // This thread will periodically check if the Service is ready, and then call onServiceReady
    class ServiceWaitThread : Thread() {
        override fun run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30)
                } catch (e: InterruptedException) {
                    AvaCrashReporter.send(
                        e,
                        "GetAppInfo class, ServiceWaitThread onResponse method"
                    )
                    throw RuntimeException("waiting thread sleep() has been interrupted")
                }
            }
            // As we're in a thread, we can't do UI stuff in it, must post a runnable in UI thread
//            MyApplication.handler.post { run }
//            MyApplication.handler.post { ContinuProssecing().runMainActivity() }
        }
    }

    fun startVoipService() {
        if (LinphoneService.isReady()) {
//            ContinuProssecing().runMainActivity()
        } else {
            // If it's not, let's start it
            ServiceHelper.start(MyApplication.context, LinphoneService::class.java)
            // And wait for it to be ready, so we can safely use it afterwards
            ServiceWaitThread().start()
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            GeneralDialog()
                .message("آیا از خروج خود اطمینان دارید؟")
                .firstButton("بله") {
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }
}
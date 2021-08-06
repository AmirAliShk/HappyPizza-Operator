package ir.food.operatorAndroid.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivitySplashBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.fragment.LogInFragment
import ir.food.operatorAndroid.fragment.SignUpFragment
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.webService.GetAppInfo

class Splash : AppCompatActivity() {
    var TAG = Splash::class.java
    private lateinit var binding: ActivitySplashBinding

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

        MyApplication.prefManager.setPushToken("pizzaOperatorAABMohsenX")
        MyApplication.prefManager.pushId = 12
        MyApplication.prefManager.setUserCode("1")

        MyApplication.handler.postDelayed(
            {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, LogInFragment())
                    .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                    .setAddToBackStack(false)
                    .add()
            }/*GetAppInfo()::callAppInfoAPI*/, 1500
        )

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
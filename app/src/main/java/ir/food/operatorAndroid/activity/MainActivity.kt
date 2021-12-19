package ir.food.operatorAndroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivityMainBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.sip.LinphoneService
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.ProxyConfig
import org.linphone.core.RegistrationState

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity.javaClass.simpleName
    }

    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false
    lateinit var core: Core

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        TypefaceUtil.overrideFonts(binding.root, MyApplication.iranSance)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.page_background)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        MyApplication.configureAccount()
        core = LinphoneService.getCore()

        binding.imgLogout.setOnClickListener {
            GeneralDialog().message("ایا از خروج از حساب کاربری خود اطمینان دارید؟")
                .firstButton("بله") {
                    MyApplication.prefManager.authorization = ""
                    MyApplication.currentActivity.startActivity(
                        Intent(
                            MyApplication.currentActivity,
                            Splash::class.java
                        )
                    )
                    MyApplication.currentActivity.finish()
                }.secondButton("خیر") {}
                .show()
        }

        binding.llRegisterOrder.setOnClickListener {

            MyApplication.currentActivity.startActivity(
                Intent(
                    MyApplication.currentActivity,
                    RegisterOrderActivity::class.java
                )
            )
            MyApplication.currentActivity.finish()
        }
    }

    private val mListener = object : CoreListenerStub() {
        override fun onRegistrationStateChanged(
            lc: Core,
            proxy: ProxyConfig,
            state: RegistrationState,
            message: String
        ) {
            if (core.defaultProxyConfig != null && core.defaultProxyConfig == proxy) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state))
            } else if (core.defaultProxyConfig == null) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state))
            }
            try {
                binding.imgSipStatus.setOnClickListener {
                    val core: Core = LinphoneService.getCore()
                    if (core != null) {
                        core.refreshRegisters()
                    }
                }
            } catch (ise: IllegalStateException) {
                ise.printStackTrace()
            }
        }
    }

    private fun getStatusIconResource(state: RegistrationState): Int {
        try {
            val core = LinphoneService.getCore()
            val defaultAccountConnected =
                core != null && core.defaultProxyConfig != null && core.defaultProxyConfig.state == RegistrationState.Ok
            if (state == RegistrationState.Ok && defaultAccountConnected) {
                return R.drawable.ic_led_connected
            } else if (state == RegistrationState.Progress) {
                return R.drawable.ic_led_inprogress
            } else if (state == RegistrationState.Failed) {
                return R.drawable.ic_led_error
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return R.drawable.ic_led_error
    }


    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        MyApplication.prefManager.isAppRun = true;
        core.addListener(mListener)
        val lpc = core.defaultProxyConfig
        if (lpc != null) {
            mListener.onRegistrationStateChanged(core, lpc, lpc.state, "")
        }
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.isAppRun = false
        KeyBoardHelper.hideKeyboard()
        if (core != null) {
            core.removeListener(mListener)
        }
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
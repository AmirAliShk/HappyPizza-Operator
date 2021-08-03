package ir.food.operatorAndroid.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivityMainBinding
import ir.food.operatorAndroid.fragment.OrdersListFragment
import ir.food.operatorAndroid.fragment.RegisterOrderFragment
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false

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
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        binding.llRegisterOrder.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, RegisterOrderFragment())
                .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                .add()
        }

        binding.llOrdersList.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, OrdersListFragment())
                .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                .add()
        }

    }

    override fun onBackPressed() {
        try {
            KeyBoardHelper.hideKeyboard()
            if (fragmentManager.backStackEntryCount > 0 || supportFragmentManager.backStackEntryCount > 0) {
                super.onBackPressed()
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    this.doubleBackToExitPressedOnce = true
                    MyApplication.Toast(
                        getString(R.string.txt_please_for_exit_reenter_back),
                        Toast.LENGTH_SHORT
                    )
                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 1500)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
}
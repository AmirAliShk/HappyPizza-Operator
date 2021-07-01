package ir.team_x.crm.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.R
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivityMainBinding
import ir.team_x.crm.fragment.ProductsFragment
import ir.team_x.crm.fragment.RegisterOrderFragment
import ir.team_x.crm.helper.FragmentHelper
import ir.team_x.crm.helper.KeyBoardHelper
import ir.team_x.crm.helper.TypefaceUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        TypefaceUtil.overrideFonts(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.darkGray)
            window.navigationBarColor = this.resources.getColor(R.color.darkGray)
        }

        binding.imgMenu.setOnClickListener { binding.draw.openDrawer(Gravity.RIGHT) }

        binding.txtRegisterOrder.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, RegisterOrderFragment())
                .replace()
            binding.draw.closeDrawers()
        }

        binding.txtProduct.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, ProductsFragment())
                .replace()
            binding.draw.closeDrawers()
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
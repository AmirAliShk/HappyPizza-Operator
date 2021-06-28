package ir.team_x.crm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivitySplashBinding
import ir.team_x.crm.fragment.LogInFragment
import ir.team_x.crm.helper.FragmentHelper
import ir.team_x.crm.okHttp.RequestHelper

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        try {
            if (MyApplication.prefManager.refreshToken.equals("")) {
                FragmentHelper
                    .toFragment(this, LogInFragment())
                    .setAddToBackStack(false)
                    .add()
            } else {
                appInfo()
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }

    private fun appInfo() {
        RequestHelper.builder(EndPoints.APP_INFO)
            .addParam("versionCode", "1")
            .addParam("os", "Android")
            .listener(appInfoCallBack)
            .post()
    }

    private val appInfoCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                TODO("Not yet implemented")
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                super.onFailure(reCall, e)
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
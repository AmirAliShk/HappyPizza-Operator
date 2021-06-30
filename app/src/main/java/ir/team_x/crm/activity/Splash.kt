package ir.team_x.crm.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.app.MyApplication.context
import ir.team_x.crm.databinding.ActivitySplashBinding
import ir.team_x.crm.dialog.GeneralDialog
import ir.team_x.crm.fragment.LogInFragment
import ir.team_x.crm.helper.AppVersionHelper
import ir.team_x.crm.helper.FragmentHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        TypefaceUtil.overrideFonts(binding.root)

        try {
            if (MyApplication.prefManager.idToken.equals("")) {
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
            .addParam("versionCode", AppVersionHelper(context).verionCode)
            .addParam("os", "Android")
            .listener(appInfoCallBack)
            .post()
    }

    private val appInfoCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            MyApplication.currentActivity.startActivity(
                                Intent(
                                    MyApplication.currentActivity,
                                    MainActivity::class.java
                                )
                            )
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { appInfo() }
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { appInfo() }
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { appInfo() }
                }
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
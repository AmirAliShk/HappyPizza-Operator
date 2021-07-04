package ir.team_x.crm.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.crm.R
import ir.team_x.crm.activity.MainActivity
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentLogInBinding
import ir.team_x.crm.dialog.GeneralDialog
import ir.team_x.crm.helper.FragmentHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

class LogInFragment : Fragment() {

    lateinit var binding: FragmentLogInBinding
    var mobile = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = this.resources.getColor(R.color.darkGray)
            window?.navigationBarColor = this.resources.getColor(R.color.darkGray)
        }
        TypefaceUtil.overrideFonts(binding.root)
        mobile = binding.edtMobileOrEmail.text.toString()
        password = binding.edtPassword.text.toString()

        binding.btnLogIn.setOnClickListener {
            if (binding.edtMobileOrEmail.text.toString() == "" || binding.edtPassword.text.toString() == "") {
                MyApplication.Toast("لطفا اطلاعات را وارد کنید.", Toast.LENGTH_SHORT)
            } else {
                login()
            }
        }

        binding.txtSignUp.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, SignUpFragment())
                .setAddToBackStack(false)
                .add()
        }

        return binding.root
    }

    private fun login() {
        binding.vfLogIn.displayedChild = 1
        RequestHelper.builder(EndPoints.LOG_IN)
            .addParam("mobileOrEmail", binding.edtMobileOrEmail.text.toString())
            .addParam("password", binding.edtPassword.text.toString())
            .listener(loginCallBack)
            .post()
    }

    private val loginCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfLogIn.displayedChild = 0
//{"success":true,"message":"کاربر با موفقیت وارد شد","data":{"idToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjBkOWJlNGY4ZTJiN2QyOTdjMmU0NjUwIiwidXNlcl9hY3RpdmUiOnRydWUsInVzZXJfZW1wbG95ZXIiOiI2MGQ5YmU0ZjhlMmI3ZDI5N2MyZTQ2NTAiLCJpYXQiOjE2MjQ4ODMwMTAsImV4cCI6MTY0NjQ4MzAxMCwiYXVkIjoiYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIifQ.LmSGVrGdlArOdfpwMQGF9f7e4xgs44bjZ9ZdBXF_8iU","accessToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6InVzZXIiLCJpYXQiOjE2MjQ4ODMwMTAsImV4cCI6MTY1MDgwMzAxMCwiYXVkIjoiYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIifQ.SRgJvlVA_fggm6KX2D45v_S7Z1tW7h8g3uT4hEfiohw"}}
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        val dataObject = response.getJSONObject("data")
                        if (success) {
                            MyApplication.prefManager.idToken = dataObject.getString("idToken")
                            MyApplication.prefManager.authorization =
                                dataObject.getString("accessToken")
                            MyApplication.currentActivity.startActivity(
                                Intent(
                                    MyApplication.currentActivity,
                                    MainActivity::class.java
                                )
                            )
                            MyApplication.currentActivity.finish()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { login() }
                        }
                    } catch (e: JSONException) {
                        binding.vfLogIn.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { login() }
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    binding.vfLogIn.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { login() }
                }
                super.onFailure(reCall, e)
            }
        }

}
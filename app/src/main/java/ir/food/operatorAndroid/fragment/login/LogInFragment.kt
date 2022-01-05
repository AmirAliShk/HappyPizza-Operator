package ir.food.operatorAndroid.fragment.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentLoginBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.webService.GetAppInfo
import org.json.JSONObject

class LogInFragment : Fragment() {

    companion object {
        val TAG = LogInFragment.javaClass.simpleName
    }

    lateinit var binding: FragmentLoginBinding
    lateinit var mobile: String
    lateinit var verificationCode: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        binding.btnLogin.setOnClickListener {
            mobile = binding.edtMobile.text.toString()
            verificationCode = binding.edtVerificationCode.text.toString()
            when {
                mobile.isEmpty() || mobile.length < 10 -> {
                    MyApplication.Toast("موبایل را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtMobile.requestFocus()
                }
                verificationCode.isEmpty() || verificationCode.length < 4 -> {
                    MyApplication.Toast("کد تایید را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtVerificationCode.requestFocus()
                }
                else -> {
                    login()
                }
            }
        }

        binding.txtSignup.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, SignUpFragment())
                .setAddToBackStack(false)
                .replace()
        }

        binding.btnSendCode.setOnClickListener {
            mobile = binding.edtMobile.text.toString()
            when {
                mobile.isEmpty() || mobile.length < 10 -> {
                    MyApplication.Toast("موبایل را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtMobile.requestFocus()
                }
                else -> {
                    requestVerificationCode()
                }
            }
        }

        return binding.root
    }

    private fun login() {
        binding.vfLogin.displayedChild = 1
        RequestHelper.builder(EndPoints.LOG_IN)
            .addParam("mobile", if (mobile.startsWith("0")) mobile else "0$mobile")
            .addParam("scope", "operator")
            .addParam("code", verificationCode)
            .listener(loginCallBack)
            .post()
    }

    private val loginCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":false,"message":"کاربر در دسترس نمی باشد","data":{}}
                    binding.vfLogin.displayedChild = 0
                    val splashJson = JSONObject(args[0].toString())
                    val success = splashJson.getBoolean("success")
                    val message = splashJson.getString("message")
                    if (!success) {
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
                    } else {
                        val dataObj = splashJson.getJSONObject("data")
                        if (dataObj.getBoolean("status")) {
                            MyApplication.prefManager.idToken = dataObj.getString("idToken")
                            MyApplication.prefManager.authorization =
                                dataObj.getString("accessToken")
                            GetAppInfo().callAppInfoAPI()
                            KeyBoardHelper.hideKeyboard()
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfLogin.displayedChild = 0
                    AvaCrashReporter.send(e, "$TAG class, loginCallBack method")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfLogin.displayedChild = 0
            }
        }
    }

    private fun requestVerificationCode() {
        binding.vfSendCode.displayedChild = 1
        RequestHelper.builder(EndPoints.LOGIN_VERIFICATION_CODE)
            .addParam("mobile", if (mobile.startsWith("0")) mobile else "0$mobile")
            .addParam("scope", "operator")
            .listener(verificationCodeCallBack)
            .post()
    }

    private val verificationCodeCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfSendCode.displayedChild = 0
                        val splashJson = JSONObject(args[0].toString())
                        val success = splashJson.getBoolean("success")
                        val message = splashJson.getString("message")

                        MyApplication.Toast(message, Toast.LENGTH_LONG)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfSendCode.displayedChild = 0
                        AvaCrashReporter.send(e, "$TAG class, verificationCodeCallBack method")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfSendCode.displayedChild = 0
                }
            }
        }
}
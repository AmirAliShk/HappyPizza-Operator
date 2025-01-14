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
import ir.food.operatorAndroid.databinding.FragmentSignUpBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.webService.GetAppInfo
import org.json.JSONObject
import java.lang.Exception

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    lateinit var nameFamily: String
    lateinit var mobile: String
    lateinit var verificationCode: String
    lateinit var password: String
    lateinit var repeatPassword: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

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

        binding.btnSignup.setOnClickListener {
            nameFamily = binding.edtName.text.toString()
            mobile = binding.edtMobile.text.toString()
            verificationCode = binding.edtVerificationCode.text.toString()
            password = binding.edtPassword.text.toString()
            repeatPassword = binding.edtRepeatPassword.text.toString()
            when {
                nameFamily.isEmpty() -> {
                    MyApplication.Toast("نام و نام خانوادگی را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtName.requestFocus()
                }
                mobile.isEmpty() || mobile.length < 10 -> {
                    MyApplication.Toast("موبایل را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtMobile.requestFocus()
                }
                verificationCode.isEmpty() || verificationCode.length < 4 -> {
                    MyApplication.Toast("کد تایید را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtVerificationCode.requestFocus()
                }
                password.isEmpty() -> {
                    MyApplication.Toast("رمز عبور را وارد کنید", Toast.LENGTH_SHORT)
                    binding.edtPassword.requestFocus()
                }
                repeatPassword.isEmpty() -> {
                    MyApplication.Toast("رمز عبور را تکرار کنید", Toast.LENGTH_SHORT)
                    binding.edtRepeatPassword.requestFocus()
                }
                repeatPassword != password -> {
                    MyApplication.Toast("تکرار رمز عبور اشتباه وارد شده است", Toast.LENGTH_SHORT)
                    binding.edtRepeatPassword.requestFocus()
                }
                else -> {
                    signUp()
                }
            }
        }

        binding.txtLogin.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, LogInFragment())
                .setAddToBackStack(false)
                .replace()
        }

        return binding.root
    }

    private fun signUp() {
        binding.vfSignup.displayedChild = 1
        RequestHelper.builder(EndPoints.SIGN_UP)
            .addParam("password", password)
            .addParam("family", nameFamily)
            .addParam("mobile", if (mobile.startsWith("0")) mobile else "0$mobile")
            .addParam("code", verificationCode)
            .addParam("scope", "operator")
            .listener(signupCallBack)
            .post()
    }

    private val signupCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfSignup.displayedChild = 0
                    val splashJson = JSONObject(args[0].toString())
                    val success = splashJson.getBoolean("success")
                    val message = splashJson.getString("message")
                    if (success) {
                        val dataObj = splashJson.getJSONObject("data")
                        if (dataObj.getBoolean("status")) {
                            GeneralDialog().message(message).firstButton("باشه") {
                                GetAppInfo().callAppInfoAPI()
                            }.cancelable(false).show()
                            MyApplication.prefManager.idToken = dataObj.getString("idToken")
                            MyApplication.prefManager.authorization =
                                dataObj.getString("accessToken")
                            KeyBoardHelper.hideKeyboard()
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfSignup.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfSignup.displayedChild = 0
            }
        }
    }

    private fun requestVerificationCode() {
        binding.vfSendCode.displayedChild = 1
        RequestHelper.builder(EndPoints.VERIFICATION_CODE)
            .addParam("mobile", if (mobile.startsWith("0")) mobile else "0$mobile")
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
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    binding.vfSendCode.displayedChild = 0
                }
            }
        }
}
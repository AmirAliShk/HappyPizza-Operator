package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentSignUpBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = this.resources.getColor(R.color.darkGray)
            window?.navigationBarColor = this.resources.getColor(R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

//        binding.btnSignUp.setOnClickListener {
//            if (binding.edtPassword.text.toString().isEmpty() || binding.edtName.text.toString()
//                    .isEmpty() || binding.edtEmail.text.toString()
//                    .isEmpty() || binding.edtEmail.text.toString()
//                    .isEmpty() || binding.edtCompanyName.text.toString().isEmpty()
//            ) {
//                MyApplication.Toast("لطفا اطلاعات را وارد کنید.", Toast.LENGTH_SHORT)
//            } else {
                signUp()
//            }
//        }
//        binding.txtLogIn.setOnClickListener {
//            FragmentHelper
//                .toFragment(MyApplication.currentActivity, LogInFragment())
//                .setAddToBackStack(false)
//                .add()
//        }
        return binding.root
    }

    private fun signUp() {
//        binding.vfSignUp.displayedChild = 1
//        RequestHelper.builder(EndPoints.SIGN_UP)
//            .addParam("family", binding.edtName.text.toString())
//            .addParam("mobile", binding.edtMobile.text.toString())
//            .listener(signUpCallBack)
//            .post()
    }

    private val signUpCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
//                        binding.vfSignUp.displayedChild = 0
////                        {"success":true,"message":"کاربر با موفقیت ثبت شد"}
//                        val response = JSONObject(args[0].toString())
//                        val success = response.getBoolean("success")
//                        val message = response.getString("message")
//                        if (success) {
//                            FragmentHelper
//                                .toFragment(MyApplication.currentActivity, LogInFragment())
//                                .setAddToBackStack(false)
//                                .replace()
//                        } else {
//                            GeneralDialog()
//                                .message(message)
//                                .firstButton("باشه") { GeneralDialog().dismiss() }
//                                .secondButton("تلاش مجدد") { signUp() }
//                                .show()
//                        }
                    } catch (e: JSONException) {
//                        binding.vfSignUp.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { signUp() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
//                    binding.vfSignUp.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { signUp() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}
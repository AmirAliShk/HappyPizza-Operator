package ir.team_x.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentSignUpBinding
import ir.team_x.crm.dialog.GeneralDialog
import ir.team_x.crm.helper.FragmentHelper
import ir.team_x.crm.okHttp.RequestHelper
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

        binding.btnSignIn.setOnClickListener {
            if (binding.edtPassword.text.toString().isEmpty() || binding.edtName.text.toString()
                    .isEmpty() || binding.edtEmail.text.toString()
                    .isEmpty() || binding.edtEmail.text.toString()
                    .isEmpty() || binding.edtCompanyName.text.toString().isEmpty()
            ) {
                MyApplication.Toast("لطفا اطلاعات را وارد کنید.", Toast.LENGTH_SHORT)
            } else {
                signUp()
            }
        }
        binding.txtLogIn.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, LogInFragment())
                .setAddToBackStack(false)
                .add()
        }
        return binding.root
    }

    private fun signUp() {
        RequestHelper.builder(EndPoints.SIGN_UP)
            .addParam("password", binding.edtPassword.text.toString())
            .addParam("family", binding.edtName.text.toString())
            .addParam("email", binding.edtEmail.text.toString())
            .addParam("mobile", binding.edtMobile.text.toString())
            .addParam("company", binding.edtCompanyName.text.toString())
            .listener(signUpCallBack)
            .post()
    }

    private val signUpCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
//                        {"success":true,"message":"کاربر با موفقیت ثبت شد"}
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            FragmentHelper
                                .toFragment(MyApplication.currentActivity, LogInFragment())
                                .setAddToBackStack(false)
                                .replace()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { signUp() }
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { signUp() }
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post{
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { signUp() }
                }
                super.onFailure(reCall, e)
            }
        }
}
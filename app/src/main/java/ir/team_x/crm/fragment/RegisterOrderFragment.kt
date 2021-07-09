package ir.team_x.crm.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentRegisterOrderBinding
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class RegisterOrderFragment : Fragment() {

    lateinit var binding: FragmentRegisterOrderBinding
    private var customer: JSONObject = JSONObject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterOrderBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = this.resources.getColor(R.color.darkGray)
            window?.navigationBarColor = this.resources.getColor(R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.btnSubmit.setOnClickListener {
            registerOrder()
        }

        return binding.root
    }

    private fun registerOrder() {
        customer.put("family", binding.edtName.text.toString())
        customer.put("mobile", binding.edtMobile.text.toString())
        customer.put("birthday", binding.edtBirthday.text.toString())

        RequestHelper.builder(EndPoints.REGISTER_ORDER)
            .addParam("customer", customer)
            .addParam("reminder", binding.edtReminder.text.toString())
            .addParam("products", "")
            .listener(registerOrderCallback)
            .post()
    }

    private val registerOrderCallback: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {

                    } catch (e: JSONException) {

                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {

                }
                super.onFailure(reCall, e)
            }

        }
}
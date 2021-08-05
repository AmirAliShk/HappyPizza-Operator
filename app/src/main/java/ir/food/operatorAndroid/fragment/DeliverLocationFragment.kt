package ir.food.operatorAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentDeliverLocationBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class DeliverLocationFragment : Fragment() {

    lateinit var binding: FragmentDeliverLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDeliverLocationBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }

    private fun getLocation(id: String) {
        RequestHelper.builder(EndPoints.GET_DELIVERY_LOCATION)
            .listener(locationCallBack)
            .addParam("orderId", id)
            .post()
    }

    private val locationCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
                            }
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                }
            }
        }

}
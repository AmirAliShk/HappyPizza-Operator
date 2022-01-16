package ir.food.operatorAndroid.webService

import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONObject

class CancelOrder {

    interface CancelOrder {
        fun onSuccess(b: Boolean)
    }

    lateinit var listener: CancelOrder

    fun callCancelAPI(orderId: String, reason:String, listener: CancelOrder) {
        this.listener = listener
        RequestHelper.builder(EndPoints.CANCEL_ORDER)
            .listener(cancelOrderCallBack)
            .addParam("orderId", orderId)
            .addParam("reason",reason.trim())
            .delete()
    }

    private val cancelOrderCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val data = jsonObject.getString("data")
                            listener.onSuccess(true)
                        } else {
                            listener.onSuccess(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "CancelOrder class, CallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { reCall }
                        .cancelable(false)
                        .show()
                }
            }
        }

}
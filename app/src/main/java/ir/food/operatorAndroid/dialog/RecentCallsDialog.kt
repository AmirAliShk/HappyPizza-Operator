package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.WindowManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import com.downloader.PRDownloader
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.RecentCallsAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogRecentCallsBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.helper.VoiceDownloader
import ir.food.operatorAndroid.model.RecentCallsModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class RecentCallsDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogRecentCallsBinding
    var fromPassengerCalls = false
    lateinit var tell: String
    private lateinit var dismissInterface: DismissInterface
    lateinit var mobile: String

    interface DismissInterface {
        fun onDismiss(b: Boolean)
    }

    var mAdapter: RecentCallsAdapter? = null
    var recentCallsModels: ArrayList<RecentCallsModel>? = null
    fun show(
        tell: String,
        mobile: String,
        sip: Int,
        fromPassengerCalls: Boolean,
        dismissInterface: DismissInterface?
    ) {
        var tell = tell
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing()) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogRecentCallsBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.window!!.attributes.windowAnimations = R.style.ExpandAnimation
        TypefaceUtil.overrideFonts(dialog.window!!.decorView)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog.window!!.attributes = wlp
        dialog.setCancelable(false)
        this.fromPassengerCalls = fromPassengerCalls
        this.tell = tell
        this.mobile = mobile
        if (dismissInterface != null) {
            this.dismissInterface = dismissInterface
        }
        if (fromPassengerCalls) {
            binding.vfHeader.displayedChild = 1
            if (binding.rgSearchType.checkedRadioButtonId == R.id.rbTell) {
                if (tell.length == 10 && !tell.startsWith("0")) {
                    tell = "0$tell"
                    getRecentCalls("/src", tell, "/4")
                } else if (tell.length == 8) {
                    tell = "051$tell"
                    getRecentCalls("/src", tell, "/4")
                } else {
                    binding.vfDownload.displayedChild = 2
                }
            } else if (binding.rgSearchType.checkedRadioButtonId == R.id.rbMobile) {
                getRecentCalls("/src", if (mobile.startsWith("0")) mobile else "0$mobile", "/4")
            }
        } else {
            binding.vfHeader.displayedChild = 0
            getRecentCalls("/dst", sip.toString() + "", "/1")
        }

        binding.imgClose.setOnClickListener { dismiss() }

        binding.rbTell.setOnClickListener {
            if (tell.length == 10 && !tell.startsWith("0")) {
                tell = "0$tell"
                getRecentCalls("/src", tell, "/4")
            } else if (tell.length == 8) {
                tell = "051$tell"
                getRecentCalls("/src", tell, "/4")
            } else {
                binding.vfDownload.displayedChild = 2
            }
        }

        binding.rbMobile.setOnClickListener {
            if (binding.rgSearchType.checkedRadioButtonId == R.id.rbMobile) {
                getRecentCalls("/src", if (mobile.startsWith("0")) mobile else "0$mobile", "/4")
            }
        }

        dialog.show()
    }

    private fun getRecentCalls(type: String, num: String?, dateInterval: String) {
        binding.vfDownload.displayedChild = 0
        RequestHelper.builder(EndPoints.GET_ORDERS_LIST + "/" + num!!.trim { it <= ' ' } + type + dateInterval)//todo
            .listener(recentCallsCallBack)
            .get()
    }

    private var recentCallsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any) {
            MyApplication.handler.post {
                try {
                    recentCallsModels = ArrayList<RecentCallsModel>()
                    val listenObj = JSONObject(args[0].toString())
                    val success = listenObj.getBoolean("success")
                    val message = listenObj.getString("message")
                    if (success) {
                        binding.vfDownload.displayedChild = 1
                        val dataArr = listenObj.getJSONArray("data")
                        for (i in 0 until dataArr.length()) {
                            val dataObj = dataArr.getJSONObject(i)
                            if (dataObj.getString("disposition") != "ANSWERED") continue
                            val model: RecentCallsModel
                            if (!fromPassengerCalls) {
                                model = RecentCallsModel(
                                    dataObj.getString("starttime"),
                                    "",
                                    dataObj.getString("voiceId"),
                                    dataObj.getString("src"),
                                    null
                                )
                            } else {
                                model = RecentCallsModel(
                                    dataObj.getString("starttime"),
                                    "",
                                    dataObj.getString("voiceId"),
                                    null,
                                    dataObj.getString("dst")
                                )
                            }
                            recentCallsModels!!.add(model)
                        }
                        if (recentCallsModels!!.size == 0) {
                            binding.vfDownload.displayedChild = 2
                        } else {
                            binding.vfDownload.displayedChild = 1
                            mAdapter = RecentCallsAdapter(recentCallsModels!!)
                            binding.listRecentCalls.adapter = mAdapter
                        }
                    } else {
                        binding.vfDownload.displayedChild = 3
                    }
                    //                    "id": "6044cfee3214a60468e2a298",
//                     "src": "09376148583",
//                     "starttime": "2021-03-07T13:06:53.890Z",
//                     "voiceId": "1615122413.10363140",
//                     "duration": 1,
//                     "disposition": "NO ANSWER", "BUSY", "ANSWERED", " "
//                     "dst": "1880",
//                     "type": "incoming",
//                     "queueName": "1880",
//                     "endtime": "2021-03-07T13:06:54.890Z"
                } catch (e: Exception) {
                    binding.vfDownload.displayedChild = 3
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post { binding.vfDownload.displayedChild = 3 }
            super.onFailure(reCall, e)
        }
    }

    private fun dismiss() {
        dismissInterface.onDismiss(true)
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e("TAG", "dismiss: " + e.message)
            AvaCrashReporter.send(e, "ReserveDialog class, dismiss method")
        }
        PRDownloader.cancelAll()
        PRDownloader.shutDown()
        VoiceDownloader().pauseVoice()
    }
}
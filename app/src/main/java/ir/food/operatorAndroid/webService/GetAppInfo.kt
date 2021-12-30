package ir.food.operatorAndroid.webService

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.ContinueProcessing
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.app.MyApplication.context
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.fragment.login.LogInFragment
import ir.food.operatorAndroid.helper.AppVersionHelper
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.ScreenHelper
import ir.food.operatorAndroid.helper.ServiceHelper
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONException
import org.json.JSONObject

class GetAppInfo {

    @SuppressLint("HardwareIds")
    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.authorization == "") {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, LogInFragment())
                    .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                    .setAddToBackStack(false)
                    .add()
            } else {
                val android_id = Settings.Secure.getString(
                    MyApplication.currentActivity.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                val deviceInfo: JSONObject? = null
                deviceInfo?.put("MODEL", Build.MODEL);
                deviceInfo?.put("HARDWARE", Build.HARDWARE);
                deviceInfo?.put("BRAND", Build.BRAND);
                deviceInfo?.put("DISPLAY", Build.DISPLAY);
                deviceInfo?.put("BOARD", Build.BOARD);
                deviceInfo?.put("SDK_INT", Build.VERSION.SDK_INT);
                deviceInfo?.put("BOOTLOADER", Build.BOOTLOADER);
                deviceInfo?.put("DEVICE", Build.DEVICE);
                deviceInfo?.put(
                    "DISPLAY_HEIGHT",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).height
                )
                deviceInfo?.put(
                    "DISPLAY_WIDTH",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).width
                )
                deviceInfo?.put(
                    "DISPLAY_SIZE",
                    ScreenHelper.getScreenSize(MyApplication.currentActivity)
                )
                deviceInfo?.put("ANDROID_ID", android_id)
                RequestHelper.builder(EndPoints.APP_INFO)
                    .addParam("versionCode", AppVersionHelper(context).versionCode)
                    .addParam("os", "Android")
                    .listener(appInfoCallBack)
                    .post()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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
                            val data: JSONObject = response.getJSONObject("data")
                            val statusMessage = data.getString("statusMessage")

                            if (data.getInt("userStatus") == 0) { // it means every thing is ok
                                val updateAvailable = data.getBoolean("update")
                                val forceUpdate = data.getBoolean("isForce")
                                val updateUrl = data.getString("updateUrl")
                                val sipNumber = data.getString("sipNumber")
                                val sipPassword = data.getString("sipPassword")
                                val sipServer = data.getString("sipServer")

                                MyApplication.prefManager.pushToken = data.getString("pushToken")
                                MyApplication.prefManager.pushId = data.getInt("pushId")
                                MyApplication.prefManager.userCode = data.getString("userId")
                                MyApplication.prefManager.sipNumber = sipNumber
                                MyApplication.prefManager.sipPassword = sipPassword
                                MyApplication.prefManager.sipServer = sipServer
                                MyApplication.prefManager.queueStatus = data.getInt("activeInQueue") == 1

                                if (updateAvailable) {
                                    update(forceUpdate, updateUrl)
                                    return@post
                                }

                                startVoipService()

                                MyApplication.handler.postDelayed({
                                    if (sipNumber != MyApplication.prefManager.sipNumber ||
                                        sipPassword != MyApplication.prefManager.sipPassword ||
                                        !sipServer.equals(MyApplication.prefManager.sipServer)
                                    ) {
                                        if (sipNumber != "0") {
                                            MyApplication.configureAccount();
                                        }
                                    }
                                }, 500)

                            } else if (data.getInt("userStatus") == 1 || data.getInt("userStatus") == 4) { // 1 = means use deleted so we logout..., and 4 = means the job changed.
                                GeneralDialog()
                                    .message(statusMessage)
                                    .secondButton("بستن") {
                                        MyApplication.prefManager.cleanPrefManger()
                                        MyApplication.currentActivity.finish()
                                    }
                                    .show()
                            } else {
                                GeneralDialog()
                                    .message(statusMessage)
                                    .secondButton("بستن") { MyApplication.currentActivity.finish() }
                                    .show()
                            }

                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { callAppInfoAPI() }
                                .cancelable(false)
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "GetAppInfo class, appInfoCallBack method")
                    }
                }
            }
        }

    fun update(isForce: Boolean, url: String) {
        if (isForce) {
            GeneralDialog()
                .message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید")
                .firstButton("به روز رسانی") {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    MyApplication.currentActivity.startActivity(i)
                    MyApplication.currentActivity.finish()
                }.secondButton("بستن") {
                    MyApplication.currentActivity.finish()
                }.cancelable(false).show()
        } else {
            GeneralDialog()
                .message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید")
                .firstButton("به روز رسانی") {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    MyApplication.currentActivity.startActivity(i)
                    MyApplication.currentActivity.finish()
                }.secondButton("فعلا نه") {
                    startVoipService()
                }.cancelable(false).show()
        }
    }

    // This thread will periodically check if the Service is ready, and then call onServiceReady
    class ServiceWaitThread : Thread() {
        override fun run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30)
                } catch (e: InterruptedException) {
                    AvaCrashReporter.send(
                        e,
                        "GetAppInfo class, ServiceWaitThread onResponse method"
                    )
                    throw RuntimeException("waiting thread sleep() has been interrupted")
                }
            }
            // As we're in a thread, we can't do UI stuff in it, must post a runnable in UI thread
//            MyApplication.handler.post { run }
            MyApplication.handler.post { ContinueProcessing().runMainActivity() }
        }
    }

    fun startVoipService() {
        if (LinphoneService.isReady()) {
            ContinueProcessing().runMainActivity()
        } else {
            // If it's not, let's start it
            ServiceHelper.start(context, LinphoneService::class.java)
            // And wait for it to be ready, so we can safely use it afterwards
            ServiceWaitThread().start()
        }
    }
}
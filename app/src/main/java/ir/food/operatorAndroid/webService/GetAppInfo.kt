package ir.food.operatorAndroid.webService

import android.content.Intent
import android.net.Uri
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.activity.MainActivity
import ir.food.operatorAndroid.app.ContinuProssecing
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.app.MyApplication.context
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.fragment.login.LogInFragment
import ir.food.operatorAndroid.helper.AppVersionHelper
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.ServiceHelper
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONException
import org.json.JSONObject


class GetAppInfo {

    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.authorization == "") {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, LogInFragment())
                    .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                    .setAddToBackStack(false)
                    .add()
            } else {
//                JSONObject deviceInfo = new JSONObject();
//                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(MyApplication.currentActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
//                deviceInfo.put("MODEL", Build.MODEL);
//                deviceInfo.put("HARDWARE", Build.HARDWARE);
//                deviceInfo.put("BRAND", Build.BRAND);
//                deviceInfo.put("DISPLAY", Build.DISPLAY);
//                deviceInfo.put("BOARD", Build.BOARD);
//                deviceInfo.put("SDK_INT", Build.VERSION.SDK_INT);
//                deviceInfo.put("BOOTLOADER", Build.BOOTLOADER);
//                deviceInfo.put("DEVICE", Build.DEVICE);
//                deviceInfo.put("DISPLAY_HEIGHT", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getHeight());
//                deviceInfo.put("DISPLAY_WIDTH", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getWidth());
//                deviceInfo.put("DISPLAY_SIZE", ScreenHelper.getScreenSize(MyApplication.currentActivity));
//                deviceInfo.put("ANDROID_ID", android_id);
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
                            val status = data.getBoolean("status")

                            val updateAvailable = data.getBoolean("update")
                            val forceUpdate = data.getBoolean("isForce")
                            val updateUrl = data.getString("updateUrl")
                            val sipNumber = data.getString("sipNumber")
                            val sipPassword = data.getString("sipPassword")
                            val sipServer = data.getString("sipServer")

                            MyApplication.prefManager.pushToken = data.getString("pushToken")
                            MyApplication.prefManager.pushId = data.getInt("pushId")
                            MyApplication.prefManager.userCode = "1"
                            MyApplication.prefManager.sipNumber = sipNumber
                            MyApplication.prefManager.sipPassword = sipPassword
                            MyApplication.prefManager.sipServer = sipServer

                            if (updateAvailable) {
                                updatePart(forceUpdate, updateUrl)
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

                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { callAppInfoAPI() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {

                }
            }
        }

    private fun updatePart(isForce: Boolean, url: String) {
        val generalDialog = GeneralDialog()
        if (isForce) {
            generalDialog.title("به روز رسانی")
            generalDialog.cancelable(false)
            generalDialog.message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید")
            generalDialog.firstButton("به روز رسانی") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                MyApplication.currentActivity.startActivity(i)
                MyApplication.currentActivity.finish()
            }
            generalDialog.secondButton("بستن برنامه") { MyApplication.currentActivity.finish() }
            generalDialog.show()
        } else {
            generalDialog.title("به روز رسانی")
            generalDialog.cancelable(false)
            generalDialog.message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید")
            generalDialog.firstButton("به روز رسانی") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                MyApplication.currentActivity.startActivity(i)
                MyApplication.currentActivity.finish()
            }
            generalDialog.secondButton("فعلا نه") {
                MyApplication.currentActivity.startActivity(
                    Intent(
                        MyApplication.currentActivity,
                        MainActivity::class.java
                    )
                )
                MyApplication.currentActivity.finish()
            }
            generalDialog.show()
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
            MyApplication.handler.post { ContinuProssecing().runMainActivity() }
        }
    }

    fun startVoipService() {
        if (LinphoneService.isReady()) {
            ContinuProssecing().runMainActivity()
        } else {
            // If it's not, let's start it
            ServiceHelper.start(MyApplication.context, LinphoneService::class.java)
            // And wait for it to be ready, so we can safely use it afterwards
            ServiceWaitThread().start()
        }
    }


}
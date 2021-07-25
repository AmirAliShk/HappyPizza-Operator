package ir.food.operatorAndroid.webService

import android.content.Intent
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.activity.MainActivity
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.app.MyApplication.context
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.fragment.LogInFragment
import ir.food.operatorAndroid.helper.AppVersionHelper
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

class GetAppInfo {

    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.refreshToken == "") {
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
                            MyApplication.currentActivity.startActivity(
                                Intent(
                                    MyApplication.currentActivity,
                                    MainActivity::class.java
                                )
                            )
                            MyApplication.currentActivity.finish()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { callAppInfoAPI() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { callAppInfoAPI() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { callAppInfoAPI() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}
package ir.food.operatorAndroid.push;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.food.operatorAndroid.app.EndPoints;
import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.helper.AppVersionHelper;
import ir.food.operatorAndroid.okHttp.RequestHelper;

public class AvaCrashReporter {

    public static void send(Exception e, Object... arg
    ) {
//    try {
//      if (!MyApplication.prefManager.isCatchCrashReportEnable()) return;
//    } catch (Exception ee) {
//      ee.printStackTrace();
//    }
        try {
            JSONObject customData = new JSONObject();
            customData.put("projectId", MyApplication.prefManager.getPushId());
            customData.put("IS_CATCH", true);
            customData.put("CATCH_LINE_NUMBER", AvaSocket.getSocketParams());
            customData.put("CATCH_ID", arg.length > 0 ? arg[0] : 0);
            customData.put("CATCH_INPUT_PARAMS", arg.length > 1 ? arg[1] : 0);
            RequestHelper.builder(EndPoints.ACRA_PATH)
                    .addParam("APP_VERSION_CODE", new AppVersionHelper(MyApplication.context).getVersionCode())
                    .addParam("APP_VERSION_NAME", new AppVersionHelper(MyApplication.context).getVersionName())
                    .addParam("PACKAGE_NAME", MyApplication.context.getPackageName())
                    .addParam("PHONE_MODEL", Build.MODEL)
                    .addParam("BRAND", Build.BRAND)
                    .addParam("ANDROID_VERSION", Build.VERSION.RELEASE)
                    .addParam("TOTAL_MEM_SIZE", "")
                    .addParam("AVAILABLE_MEM_SIZE", "")
                    .addParam("IS_SILENT", "")
                    .addParam("CUSTOM_DATA", customData)
                    .addParam("STACK_TRACE", Log.getStackTraceString(e))
                    .addParam("INITIAL_CONFIGURATION", "")
                    .addParam("USER_APP_START_DATE", "")
                    .addParam("USER_CRASH_DATE", "")
                    .addParam("DEVICE_FEATURES", "")
                    .post();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
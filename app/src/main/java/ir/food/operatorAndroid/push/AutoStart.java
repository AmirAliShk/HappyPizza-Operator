package ir.food.operatorAndroid.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.food.operatorAndroid.app.MyApplication;
import ir.food.operatorAndroid.helper.NetworkUtil;
import ir.food.operatorAndroid.helper.ServiceHelper;

public class AutoStart extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        try {

            int status = NetworkUtil.getConnectivityStatusString(context);

            if (intent == null) return;
            if (intent.getAction() == null) return;
            switch (intent.getAction()) {
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                        AvaLog.i("start push service by connection changed");
                        ServiceHelper.start(context, AvaService.class);
                    }
                    break;
                case "android.intent.action.BOOT_COMPLETED":
                    AvaLog.i("start push service by boot completed");
                    ServiceHelper.start(context, AvaService.class);
                    break;
                case "ir.food.operatorAndroid.PUSH_SERVICE_DESTROY":
                    MyApplication.handler.postDelayed(() -> {
                        if (!ServiceHelper.isRunning(context, AvaService.class)) {
                            ServiceHelper.start(context, AvaService.class);
                            AvaLog.i("start push service by on destroy listener");
                        }
                    }, 15000);
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, 112);

        }
    }

}


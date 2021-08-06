package ir.food.operatorAndroid.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import ir.food.operatorAndroid.app.MyApplication;


public class ServiceHelper {

    // Method to start the service
    public static void start(Context activity, Class<?> serviceClass) {
        try {
            if (activity != null)
                if (!isRunning(activity, serviceClass))
                    activity.startService(new Intent(activity, serviceClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop the service
    public static void stop(Context activity, Class<?> serviceClass) {
        try {
            if (activity != null)
                activity.stopService(new Intent(activity, serviceClass));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // Method to stop the service
    public static void restart(Context context, Class<?> serviceClass) {
        stop(context, serviceClass);
        //you must set delay between stop and start because maybe conflict together
        MyApplication.handler.postDelayed(() -> start(context, serviceClass), 500);
    }

    //Check the service is running
    public static boolean isRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

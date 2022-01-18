package ir.food.operatorAndroid.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ir.food.operatorAndroid.app.MyApplication;

public class NetworkStatus {

    /////this method ping ip of google dns server if return 0 its mean the device conncetd to the internet if return other data no connection to internet  

    //    public static int readNetworkStatus() {
    //
    //        ConnectivityManager connectivityManager = (ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    //
    //        boolean isConnected = false;
    //        try {
    //            isConnected = networkInfo.isConnected();
    //        }
    //        catch (Exception e) {}
    //        if (isConnected) {
    //            Runtime runtime = Runtime.getRuntime();
    //            try {
    //                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
    //                int exitValue = ipProcess.waitFor();
    //                int exit = ipProcess.exitValue();
    //
    //                if (exit == 0) {
    //                    return 1;
    //                } else {
    //                    return 0;
    //                }
    //            }
    //            catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //            catch (InterruptedException e) {
    //                e.printStackTrace();
    //            }
    //
    //        }
    //        return 0;
    //    }

//    private static boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null;
//    }
//
//
//    public static int readNetworkStatus() {
//
//        if (isNetworkAvailable()) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection)
//                        (new URL("http://clients3.google.com/generate_204")
//                                .openConnection());
//                urlc.setRequestProperty("User-Agent", "Android");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//                if ((urlc.getResponseCode() == 204 && urlc.getContentLength() == 0)) {
//                    return 1;
//                }
//            }
//
//            catch (IOException e) {
//                e.printStackTrace();
//                Log.e("TAG", "Error checking internet connection", e);
//            }
//
//        } else {
//            Log.d("TAG", "No network available!");
//        }
//
//        return 0;
//    }


    public static boolean readNetworkStatus() {
        ConnectivityManager connectivity = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
}
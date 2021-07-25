package ir.food.operatorAndroid.helper;

import android.content.Intent;

import ir.food.operatorAndroid.activity.MainActivity;
import ir.food.operatorAndroid.app.MyApplication;

public class ContinueProcessing {

    public static void runMainActivity() {
//        if this not checked, the program will be closed when login fragment open from mainActivity

//            if (MyApplication.currentActivity.toString().contains(MainActivity.TAG)) {
//                FragmentHelper
//                        .taskFragment(MyApplication.currentActivity, LogInFragment.TAG)
//                        .remove();
//                return;
//            }
        MyApplication.handler.postDelayed(() -> {
            MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
        },200);

    }
}

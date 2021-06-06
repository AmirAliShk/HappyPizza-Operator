package ir.team_x.crm.helper;

import android.content.Intent;

import ir.team_x.crm.activity.MainActivity;
import ir.team_x.crm.app.MyApplication;

public class ContinueProcessing {

    public static void runMainActivity() {
        //if this not checked, the program will be closed when login fragment open from mainActivity

//            if (MyApplication.currentActivity.toString().contains(MainActivity.TAG)) {
//                FragmentHelper
//                        .taskFragment(MyApplication.currentActivity, LoginFragment.TAG)
//                        .remove();
//                return;
//            }
        MyApplication.handler.post(() -> {
            MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
        });

    }
}

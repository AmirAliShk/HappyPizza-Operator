package ir.food.operatorAndroid.app

import android.content.Intent
import ir.food.operatorAndroid.activity.MainActivity
import ir.food.operatorAndroid.fragment.login.LogInFragment
import ir.food.operatorAndroid.helper.FragmentHelper

class ContinueProcessing {
    fun runMainActivity() {
        //if this not checked, the program will be closed when login fragment open from mainActivity
        if (MyApplication.currentActivity.toString().contains(MainActivity.TAG)) {
            FragmentHelper
                .taskFragment(MyApplication.currentActivity, LogInFragment.TAG)
                .remove()
//            FragmentHelper
//                .taskFragment(MyApplication.currentActivity, CheckVerificationFragment.TAG)
//                .remove()
            return
        }
        if (MyApplication.prefManager.userCode != "0") {
            MyApplication.avaStart()
        }
        MyApplication.handler.post {
            MyApplication.currentActivity.startActivity(
                Intent(
                    MyApplication.currentActivity,
                    MainActivity::class.java
                )
            )
            MyApplication.currentActivity.finish()
        }
    }
}
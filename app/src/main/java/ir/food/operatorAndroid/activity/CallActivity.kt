package ir.food.operatorAndroid.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivityCallBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.linphone.core.Address
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub

class CallActivity : AppCompatActivity() {

    companion object;

    private val TAG = CallActivity.javaClass.simpleName
    lateinit var binding: ActivityCallBinding
    lateinit var call: Call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.navigationBarColor = resources.getColor(R.color.pageBackground)
//            window.statusBarColor = resources.getColor(R.color.colorPrimary)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        TypefaceUtil.overrideFonts(binding.root)
        KeyBoardHelper.hideKeyboard()

        binding.ripple.startRippleAnimation()

        binding.imgAccept.setOnClickListener {
            try {
                val core = LinphoneService.getCore()
                call = core.currentCall
                val calls = core.calls
                if (call != null) {
                    call.accept()
                } else {
                    if (calls.isNotEmpty()) {
                        calls[0].accept()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                AvaCrashReporter.send(e, "$TAG class, onAcceptPress")
            }
        }

        binding.imgReject.setOnClickListener {
            val mCore = LinphoneService.getCore()
            val currentCall: Call = mCore.currentCall
            var flagTerminate = false
            for (call in mCore.calls) {
                if (call != null && call.conference != null) {
//        if (mCore.isInConference()) {
//          displayConferenceCall(call);
//          conferenceDisplayed = true;
//        } else if (!pausedConferenceDisplayed) {
//          displayPausedConference();
//          pausedConferenceDisplayed = true;
//        }
                } else if (call != null && call !== currentCall) {
                    val state: Call.State = call.state
                    if (state === Call.State.Paused || state === Call.State.PausedByRemote || state === Call.State.Pausing) {
                        call.terminate()
                        flagTerminate = true
                    }
                } else if (call != null && call === currentCall) {
                    flagTerminate = true
                    call.terminate()
                }
            }
            if (!flagTerminate) {
                finish()
            }
        }
    }

    private fun gotoCalling() {
        val intent = Intent(this, RegisterOrderActivity::class.java)
//        if (MyApplication.prefManager.getActivityStatus() === 1) {  //you are enable in trip register queue
//            intent = Intent(this, ServiceRegisterActivity::class.java)
//        } else if (MyApplication.prefManager.getActivityStatus() === 2) { // you are enable in support queue (800)
//            intent = Intent(this, SupportActivity::class.java)
//            intent.putExtra("comeFromCallActivity", true)
//        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        // This flag is required to start an Activity from a Service context
    }

    var mCoreListener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            if (state == Call.State.End || state == Call.State.Released) {
                finish()
            } else if (state == Call.State.Connected) {
                gotoCalling()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        MyApplication.prefManager.isAppRun = true
        try {
            val core = LinphoneService.getCore()
            core?.addListener(mCoreListener)
            val calls = core!!.calls
            for (callList in calls) {
                if (callList.state == Call.State.IncomingReceived) {
                    call = callList
                    val address: Address = callList.remoteAddress
                    binding.txtCallerNum.text = address.username
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, onResume method")
        }
    }

    override fun onStart() {
        super.onStart()
        MyApplication.prefManager.isAppRun = true
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.prefManager.isAppRun = false
    }

    override fun onPause() {
        MyApplication.prefManager.isAppRun = false
        val core = LinphoneService.getCore()
        core?.removeListener(mCoreListener)
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        MyApplication.prefManager.isAppRun = false
    }
}
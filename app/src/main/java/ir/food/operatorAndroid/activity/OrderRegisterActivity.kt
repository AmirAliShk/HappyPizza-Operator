package ir.food.operatorAndroid.activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivityOrderRegisterBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.LoadingDialog
import ir.food.operatorAndroid.fragment.OrdersListFragment
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.NumberValidation
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONObject
import org.linphone.core.Address
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import java.lang.System.exit

class OrderRegisterActivity : AppCompatActivity() {

    companion object {
        var isRunning = false
    }

    lateinit var binding: ActivityOrderRegisterBinding
    var mCallQualityUpdater: Runnable? = null
    var mDisplayedQuality = -1
    lateinit var call: Call
    lateinit var core: Core
    var voipId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }
        refreshQueueStatus()

        binding.btnSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, OrdersListFragment()).replace()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnActivate.setOnClickListener { enterTheQueue() }

        binding.btnDeActivate.setOnClickListener { exitTheQueue() }

        binding.rlAccept.setOnClickListener {
            call = core.currentCall
            val calls = core.calls
            val i = calls.size
            if (call != null) {
                call.accept()
            } else if (calls.isNotEmpty()) {
                calls[0].accept()
            }
        }

        binding.rlReject.setOnClickListener {
            val mCore = LinphoneService.getCore()
            val currentCall = mCore.currentCall
            for (call in mCore.calls) {
                if (call != null && call.conference != null) {
                } else if (call != null && call !== currentCall) {
                    val state = call.state
                    if (state == Call.State.Paused || state == Call.State.PausedByRemote || state == Call.State.Pausing) {
                        call.terminate()
                    }
                } else if (call != null && call === currentCall) {
                    call.terminate()
                }
            }
        }

    }

    private fun enterTheQueue() {
        KeyBoardHelper.hideKeyboard()
        GeneralDialog()
            .cancelable(false)
            .message("مطمئنی میخوای وارد صف بشی؟")
            .firstButton("مطمئنم") {
                enterTheQueue(MyApplication.prefManager.sipNumber)
            }
            .secondButton("نیستم", null)
            .show()
    }

    private fun exitTheQueue() {
        KeyBoardHelper.hideKeyboard()
        GeneralDialog()
            .title("هشدار")
            .cancelable(false)
            .message("مطمئنی میخوای خارج بشی؟")
            .firstButton("مطمئنم") {
                if (MyApplication.prefManager.isCallIncoming) {
                    MyApplication.Toast(getString(R.string.exit), Toast.LENGTH_SHORT)
                } else {
                    exitQueue()
                }
            }
            .secondButton("نیستم", null)
            .show()
    }

    var mCoreListener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            c: Call,
            state: Call.State,
            message: String
        ) {
            call = c
            if (state == Call.State.IncomingReceived) {
                showCallIncoming()
            } else if (state == Call.State.Released) {
                binding.imgCallOption.setImageResource(R.drawable.ic_call_dialog_disable)
                showTitleBar()
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater)
                    mCallQualityUpdater = null
                }
            } else if (state == Call.State.Connected) {
                startCallQuality()
                binding.imgCallOption.setImageResource(R.drawable.ic_call_dialog_enable)
                val address = call.remoteAddress
                if (voipId == "0") {
                    binding.edtMobile.setText(NumberValidation.removePrefix(address.username))
                }
                showTitleBar()
            } else if (state == Call.State.Error) {
                showTitleBar()
                binding.imgCallQuality.visibility = View.INVISIBLE
            } else if (state == Call.State.End) {
                binding.imgCallQuality.visibility = View.INVISIBLE
                showTitleBar()
                if (mCallQualityUpdater != null) {
                    LinphoneService.removeFromUIThreadDispatcher(mCallQualityUpdater)
                    mCallQualityUpdater = null
                }
            }
        }
    }

    private fun enterTheQueue(sipNumber: String) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.ENTER_QUEUE)
            .addParam("sipNumber", sipNumber)
            .listener(enterTheQueueCallBack)
            .post()
    }

    private val enterTheQueueCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        LoadingDialog.dismissCancelableDialog()
//                        {"success":true,"message":"عملیات با موفقیت انجام شد.","data":{"queues":"100,200"}}
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            MyApplication.prefManager.queueStatus = true
                            refreshQueueStatus()
                            GeneralDialog()
                                .message("شما با موفقیت وارد صف شدید")
                                .firstButton("باشه") {}
                                .cancelable(false)
                                .show()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") {}
                                .cancelable(false)
                                .show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        LoadingDialog.dismissCancelableDialog()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    LoadingDialog.dismissCancelableDialog()
                }
            }
        }

    private fun exitQueue() {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.EXIT_QUEUE)
            .listener(exitTheQueueCallBack)
            .addParam("", JSONObject())
            .delete()
    }

    private val exitTheQueueCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        LoadingDialog.dismissCancelableDialog()
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            MyApplication.prefManager.setQueueStatus(false)
                            refreshQueueStatus()
                            GeneralDialog()
                                .message("شما با موفقیت از صف خارج شدید")
                                .firstButton("باشه") {}
                                .cancelable(false)
                                .show()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") {}
                                .cancelable(false)
                                .show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LoadingDialog.dismissCancelableDialog()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    LoadingDialog.dismissCancelableDialog()
                }
            }
        }

    private fun refreshQueueStatus() {
        if (MyApplication.prefManager.getQueueStatus()) {
            binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge)
            binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"))
            binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"))
        } else {
            binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge)
            binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"))
            binding.btnDeActivate.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private fun startCallQuality() {
        if (mCallQualityUpdater == null)
            LinphoneService.dispatchOnUIThreadAfter(
                object : Runnable {
                    val mCurrentCall = LinphoneService.getCore().currentCall;
                    override fun run() {
                        if (mCurrentCall == null) {
                            mCallQualityUpdater = null;
                            return
                        }
                        val newQuality = mCurrentCall.currentQuality
                        updateQualityOfSignalIcon(newQuality);

                        if (MyApplication.prefManager.getConnectedCall())
                            LinphoneService.dispatchOnUIThreadAfter(this, 1000);
                    }
                }, 1000
            )
    }

    private fun updateQualityOfSignalIcon(quality: Float) {
        val iQuality = quality.toInt()
        var imageRes = R.drawable.ic_quality_0
        if (iQuality == mDisplayedQuality) return
        when {
            quality >= 4 -> { // Good Quality
                imageRes = R.drawable.ic_quality_4
            }
            quality >= 3 -> { // Average quality
                imageRes = R.drawable.ic_quality_3
            }
            quality >= 2 -> { // Low quality
                imageRes = R.drawable.ic_quality_2
            }
            quality >= 1 -> { // Very low quality
                imageRes = R.drawable.ic_quality_1
            }
        }
        binding.imgCallQuality.visibility = View.VISIBLE
        binding.imgCallQuality.setImageResource(imageRes)
        mDisplayedQuality = iQuality
    }

    private fun showCallIncoming() {
        binding.mRipplePulseLayout.startRippleAnimation()
        call = core.currentCall
        val address: Address = call.remoteAddress
        binding.txtCallerNum.text = address.username
        binding.rlNewInComingCall.visibility = View.VISIBLE
        binding.rlActionBar.visibility = View.GONE
    }

    private fun showTitleBar() {
        binding.mRipplePulseLayout.stopRippleAnimation()
        binding.rlNewInComingCall.visibility = View.GONE
        binding.rlActionBar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        showTitleBar()

        if (MyApplication.prefManager.connectedCall) {
            startCallQuality()
            binding.imgCallOption.setImageResource(R.drawable.ic_call_dialog_enable)
            val calls = core.calls
            for (call in calls) {
                if (call != null && call.state == Call.State.StreamsRunning) {
                    if (voipId == "0") {
                        val address = call.remoteAddress
                        binding.edtMobile.setText(NumberValidation.removePrefix(address.username))
//                        MyApplication.handler.postDelayed({ onPressDownload() }, 600)
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
        isRunning = true
        core = LinphoneService.getCore()
        core.addListener(mCoreListener)
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        core.removeListener(mCoreListener)
    }

    override fun onBackPressed() {
        KeyBoardHelper.hideKeyboard()
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            GeneralDialog()
                .message("آیا از خروج خود اطمینان دارید؟")
                .firstButton("بله") {
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }
}

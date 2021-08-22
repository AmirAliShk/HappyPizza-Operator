package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogCallBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.linphone.core.*

class CallDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogCallBinding

    var call: Call? = null
    lateinit var core: Core
    lateinit var callAddress: Address

    interface CallDialogInterface {
        fun onDismiss()
        fun onCallReceived()
        fun onCallTransferred()
        fun onCallEnded()
    }

    lateinit var callDialogInterface: CallDialogInterface

    fun show(callDialogInterface: CallDialogInterface, isFromSupport: Boolean) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCallBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        core = LinphoneService.getCore()
        call = core.currentCall
        core.addListener(coreListener)
        binding.vfCall.displayedChild = if (call == null) 0 else 1 // todo uncomment this later

        this.callDialogInterface = callDialogInterface
        core.addListener(coreListener)

        this.callDialogInterface = callDialogInterface

        if (isFromSupport) {
            binding.vfCall.displayedChild = 1
        }

        binding.llTest.setOnClickListener {
            val addressToCall: Address = core.interpretUrl("998")
            val params: CallParams = core.createCallParams(null)
            params.enableVideo(false)
            core.inviteAddressWithParams(addressToCall, params)
            callAddress = addressToCall

            setCancelable(false)
            binding.vfCall.displayedChild = 1
        }

        binding.llRecentCall.setOnClickListener {

        }

        binding.llEndCall.setOnClickListener {
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
            dismiss()
        }

        binding.imgClose.setOnClickListener {
            MyApplication.handler.postDelayed({
                dismiss()
                KeyBoardHelper.hideKeyboard()
            }, 200)

        }

        dialog.show()

    }

    private var coreListener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(lc: Core, _call: Call, state: Call.State, message: String) {
            super.onCallStateChanged(lc, _call, state, message)
            call = _call
            when (state) {
                Call.State.IncomingReceived -> {
                    callDialogInterface.onCallReceived()
                }
                Call.State.Released -> {
                    dismiss()
                }
                Call.State.Connected -> {
                }
                Call.State.End -> {
                    callDialogInterface.onCallEnded()
                    dismiss()
                }
            }
        }
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            core.removeListener(coreListener)
        } catch (e: Exception) {
            AvaCrashReporter.send(e, "CallDialog class, dismiss method")
        }
    }

    private fun setCancelable(v: Boolean) {
        dialog.setCancelable(v)
        binding.imgClose.visibility = if (v) View.VISIBLE else View.GONE
    }

}
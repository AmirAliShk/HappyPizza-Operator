package ir.food.operatorAndroid.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.PendingCartAdapter
import ir.food.operatorAndroid.adapter.SpinnerAdapter
import ir.food.operatorAndroid.app.DataHolder
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.Keys
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ActivityRegisterOrderBinding
import ir.food.operatorAndroid.dialog.CallDialog
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.LoadingDialog
import ir.food.operatorAndroid.fragment.OrdersListFragment
import ir.food.operatorAndroid.helper.*
import ir.food.operatorAndroid.model.CallModel
import ir.food.operatorAndroid.model.PendingCartModel
import ir.food.operatorAndroid.model.ProductsModel
import ir.food.operatorAndroid.model.ProductsTypeModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.linphone.core.Address
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class RegisterOrderActivity : AppCompatActivity() {

    companion object {
        var isRunning = false
    }

    lateinit var binding: ActivityRegisterOrderBinding
    var mCallQualityUpdater: Runnable? = null
    var mDisplayedQuality = -1
    lateinit var call: Call
    lateinit var core: Core
    var voipId = "0"
    var queue = "0"
    var isEnableView = false
    var productsModels: ArrayList<ProductsModel> = ArrayList()
    var typesModels: ArrayList<ProductsTypeModel> = ArrayList()
    var pendingCartModels: ArrayList<PendingCartModel> = ArrayList()
    var tempProductsModels: ArrayList<PendingCartModel> = ArrayList()
    var productTypes: String = ""
    var productId: String = ""
    var sum = 0
    private var pendingCartAdapter =
        PendingCartAdapter(pendingCartModels, object : PendingCartAdapter.TotalPrice {
            override fun collectTotalPrice(s: Int) {
                sum = 0
                if (s == 0) {
                    binding.txtSumPrice.text = "۰ تومان"
                    return
                }
                for (i in 0 until s) {
                    sum += Integer.valueOf(pendingCartModels[i].price)
                    binding.txtSumPrice.text =
                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"
                }
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.page_background)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        MyApplication.configureAccount()
        refreshQueueStatus()
        binding.orderList.adapter = pendingCartAdapter

        binding.btnSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, OrdersListFragment(""))
                .replace()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnActivate.setOnClickListener { enterTheQueue() }

        binding.btnDeActivate.setOnClickListener { exitTheQueue() }

        binding.imgCallOption.setOnClickListener {
            CallDialog().show(object : CallDialog.CallDialogInterface {
                override fun onDismiss() {
                    core.addListener(mCoreListener)
                }

                override fun onCallReceived() {
                    showCallIncoming()
                }

                override fun onCallTransferred() {
//                    MyApplication.handler.postDelayed({ clearData(binding.root) }, 100)
                }

                override fun onCallEnded() {
                }
            }, false)
        }

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

        binding.imgDownload.setOnClickListener {
            if (binding.edtMobile.text.toString() == "") {
                MyApplication.Toast("شماره موبایل را وارد کنید", Toast.LENGTH_LONG)
                binding.edtMobile.requestFocus()
                return@setOnClickListener
            }
            getCustomer(binding.edtMobile.text.toString())
        }

        binding.imgClear.setOnClickListener { clearData() }

        binding.imgAddOrder.setOnClickListener {
            if (productId.isEmpty()) {
                return@setOnClickListener
            }
            for (i in 0 until JSONArray(MyApplication.prefManager.productsList).length()) {
                if (productsModels[i].id == productId) {
                    val pendingCart = PendingCartModel(
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getString("_id"),
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getString("name"),
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("price"),
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("name")
                    )
                    pendingCartModels.add(pendingCart)
                    pendingCartAdapter.notifyDataSetChanged()
                    sum += Integer.valueOf(
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("price")
                    )
                    binding.txtSumPrice.text =
                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"
                }
            }
        }

        getProductsAndLists()

    }

    private fun getProductsAndLists() {
        RequestHelper.builder(EndPoints.GET_PRODUCTS)
            .listener(productsCallBack)
            .get()
    }

    private val productsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    productsModels.clear()
                    typesModels.clear()
//{"success":true,"message":"محصولات سفارش با موفقیت ارسال شد","data":{"products":[{"_id":"61091b0ca9335b389819e894","size":[{"name":"medium","price":"75000","discount":"15000"}],"name":"رست بیف","description":"گوشت گوساله . پنیر . قارچ . فلفل دلمه ای . پیازجه","type":{"_id":"610916826f9446153c5e268d","name":"پیتزا"}}],"types":[{"_id":"610916826f9446153c5e268d","name":"پیتزا"}],"status":true}}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val data = jsonObject.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (status) {
                            MyApplication.prefManager.productsList =
                                data.getJSONArray("products").toString()
                            MyApplication.prefManager.productsTypeList =
                                data.getJSONArray("types").toString()
                            initProductTypeSpinner()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            super.onFailure(reCall, e)
        }
    }

    private fun initProductTypeSpinner() {
        val typesList = ArrayList<String>()
        try {
            typesList.add(0, "نوع محصول")
            val typesArr = JSONArray(MyApplication.prefManager.productsTypeList)
            for (i in 0 until typesArr.length()) {
                val types = ProductsTypeModel(
                    typesArr.getJSONObject(i).getString("_id"),
                    typesArr.getJSONObject(i).getString("name")
                )
                typesModels.add(types)

                typesList.add(i + 1, typesArr.getJSONObject(i).getString("name"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (binding.spProductType == null) return

        try {
            binding.spProductType.adapter =
                SpinnerAdapter(MyApplication.context, R.layout.item_spinner, typesList)
            binding.spProductType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            productTypes = ""
                            return
                        }
                        tempProductsModels.clear()
                        productTypes = typesModels[position - 1].id
                        initProductSpinner(productTypes)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initProductSpinner(type: String) {
        val productsList = ArrayList<String>()
        val productsArr = JSONArray(MyApplication.prefManager.productsList)
        try {
            productsList.add(0, "محصولات")
            for (i in 0 until productsArr.length()) {
                val products = ProductsModel(
                    productsArr.getJSONObject(i).getString("_id"),
                    productsArr.getJSONObject(i).getJSONArray("size"),
                    productsArr.getJSONObject(i).getString("name"),
                    productsArr.getJSONObject(i).getString("description"),
                    productsArr.getJSONObject(i).getJSONObject("type")
                )
                productsModels.add(products)
                if (productsModels[i].type.getString("_id").equals(type)) {
                    val pendingCart = PendingCartModel(
                        productsArr.getJSONObject(i).getString("_id"),
                        productsArr.getJSONObject(i).getString("name"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("price"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("name")
                    )
                    tempProductsModels.add(pendingCart)
                    productsList.add(productsArr.getJSONObject(i).getString("name"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (binding.spProduct == null) return

        try {
            binding.spProduct.adapter =
                SpinnerAdapter(MyApplication.context, R.layout.item_spinner, productsList)
            binding.spProduct.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            productId = ""
                            return
                        }
                        productId = tempProductsModels[position - 1].id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
                    exitQueue(MyApplication.prefManager.sipNumber)
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

    private fun enterTheQueue(sipNumber: Int) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.ENTER_QUEUE)
            .addParam("sipNumber", sipNumber)
            .addParam("state", 1)
            .listener(enterTheQueueCallBack)
            .put()
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

    private fun getCustomer(mobileNo: String) {
        binding.vfDownload.displayedChild = 1
        RequestHelper.builder(EndPoints.GET_CUSTOMER)
            .addPath(if (mobileNo.startsWith("0")) mobileNo else "0$mobileNo")
            .listener(getCustomerCallBack)
            .get()
    }

    private val getCustomerCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfDownload.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val success = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (success) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
                                val customerObj = dataObj.getJSONObject("customer")
                                binding.edtMobile.setText(customerObj.getString("mobile"))
                                binding.edtCustomerName.setText(customerObj.getString("family"))

                                val orderStatus = dataObj.getJSONObject("orderStatus")

                                when (orderStatus.getInt("status")) {
                                    0 ->//new order
                                    {

                                    }
                                    1 -> {//customer is lock
                                        binding.txtLockCustomer.visibility = View.VISIBLE
                                    }
                                    2 ->//recently add order
                                    {
                                        val msg =
                                            " کاربر ${orderStatus.getInt("orderInterval")} دقیقه پیش سفارشی ثبت کرده است \n وضعیت سفارش : ${
                                                orderStatus.getString(
                                                    "orderState"
                                                )
                                            }"
                                        binding.vfDownload.displayedChild = 0

                                        GeneralDialog()
                                            .message(msg)
                                            .cancelable(false)
                                            .firstButton("بستن", null)
                                            .secondButton("پشتیبانی") {
                                                FragmentHelper.toFragment(
                                                    MyApplication.currentActivity,
                                                    OrdersListFragment(customerObj.getString("mobile"))
                                                ).replace()
                                            }
                                            .show()
                                    }
                                }
                            } else {
                                //new customer
                                MyApplication.Toast(message, Toast.LENGTH_LONG)
                                binding.txtNewCustomer.visibility = View.VISIBLE
                            }
                        } else {
                            MyApplication.Toast(message, Toast.LENGTH_LONG)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfDownload.displayedChild = 0
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfDownload.displayedChild = 0
                }
            }
        }

    private fun exitQueue(sipNumber: Int) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.ENTER_QUEUE)
            .listener(exitTheQueueCallBack)
            .addParam("sipNumber", sipNumber)
            .addParam("state", 0)
            .put()
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
                            MyApplication.prefManager.queueStatus = false
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

    private fun clearData() {
        binding.edtMobile.requestFocus()
        binding.txtLockCustomer.visibility = View.GONE
        binding.txtNewCustomer.visibility = View.GONE
        binding.edtMobile.setText("")
        binding.edtCustomerName.setText("")
        binding.edtAddress.setText("")
        binding.edtDescription.setText("")
        initProductTypeSpinner()
        voipId = "0"
    }

    private fun enableViews() {
        binding.edtCustomerName.isEnabled = true
        binding.edtAddress.isEnabled = true
        binding.edtDescription.isEnabled = true
        binding.spProductType.isEnabled = true
        binding.spProduct.isEnabled = true
    }

    private fun disableViews() {
        binding.edtCustomerName.isEnabled = false
        binding.edtAddress.isEnabled = false
        binding.edtDescription.isEnabled = false
        binding.spProductType.isEnabled = false
        binding.spProduct.isEnabled = false
    }

    //receive push notification from local broadcast
    var pushReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getStringExtra(Keys.KEY_MESSAGE)
            parseNotification(result)?.let { handleCallerInfo(it) }
        }
    }

    //receive userStatus from local broadcast
    var userStatusReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val messageUserStatus = intent.getStringExtra(Keys.KEY_MESSAGE_USER_STATUS)
            val userStatus = intent.getBooleanExtra(Keys.KEY_USER_STATUS, false)
            if (!userStatus) {
                binding.btnDeActivate.setBackgroundResource(R.drawable.bg_pink_edge)
                binding.btnActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"))
                MyApplication.prefManager.queueStatus = false
            } else {
                binding.btnActivate.setBackgroundResource(R.drawable.bg_green_edge)
                binding.btnDeActivate.setBackgroundColor(Color.parseColor("#00FFB2B2"))
                MyApplication.prefManager.queueStatus = true
            }
        }
    }

    private fun handleCallerInfo(callModel: CallModel) {
        try {
            if (voipId == "0") {
                //show CallerId
                if (callModel == null) {
                    return
                }
                val participant: String =
                    PhoneNumberValidation.removePrefix(callModel.participant)
                queue = callModel.queue
                voipId = callModel.voipId
                DataHolder.getInstance().setVoipId(voipId)
                if (binding.edtMobile == null) return
                if (participant == null) return
                binding.edtMobile.setText(participant)
                MyApplication.handler.postDelayed({ binding.imgDownload.callOnClick() }, 400)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "TripRegisterActivity class, handleCallerInfo method")
        }
    }

    /**
     * @param info have below format
     * sample : {"type":"callerInfo","exten":"456","participant":"404356734579","queue":"999","voipId":"1584260434.9922480"}
     */
    private fun parseNotification(info: String?): CallModel? {
        if (info == null) return null
        try {
            val `object` = JSONObject(info)
            val strMessage = `object`.getString("message")
            val messages = JSONObject(strMessage)
            val typee = messages.getString("type")
            if (typee == "callerInfo") {
                val message = JSONObject(strMessage)
                return CallModel(
                    message.getString("type"),
                    message.getInt("exten"),
                    message.getString("participant"),
                    message.getString("queue"),
                    message.getString("voipId")
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            AvaCrashReporter.send(
                e,
                "TripRegisterActivity class, parseNotification method ,info : $info"
            )
            return null
        }
        return null
    }

    private fun refreshQueueStatus() {
        if (MyApplication.prefManager.queueStatus) {
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

                        if (MyApplication.prefManager.connectedCall)
                            LinphoneService.dispatchOnUIThreadAfter(this, 1000);
                    }
                }, 1000
            )
    }

    private fun updateQualityOfSignalIcon(quality: Float) {
        val iQuality = quality.toInt()
        var imageRes = 0
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
        binding.imgCallQuality.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        showTitleBar()
        MyApplication.prefManager.isAppRun = true;
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

        if (MyApplication.prefManager.isCallIncoming) {
            showCallIncoming()
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
        MyApplication.prefManager.isAppRun = false;
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
                    MyApplication.currentActivity.startActivity(
                        Intent(
                            MyApplication.currentActivity,
                            MainActivity::class.java
                        )
                    )
                    MyApplication.currentActivity.finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }
}
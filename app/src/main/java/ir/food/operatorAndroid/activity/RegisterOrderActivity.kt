package ir.food.operatorAndroid.activity

import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.PendingCartAdapter
import ir.food.operatorAndroid.adapter.SpinnerAdapter
import ir.food.operatorAndroid.app.*
import ir.food.operatorAndroid.databinding.ActivityRegisterOrderBinding
import ir.food.operatorAndroid.dialog.AddressDialog
import ir.food.operatorAndroid.dialog.CallDialog
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.LoadingDialog
import ir.food.operatorAndroid.fragment.OrdersListFragment
import ir.food.operatorAndroid.helper.*
import ir.food.operatorAndroid.model.*
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.linphone.core.*
import java.util.*
import kotlin.collections.ArrayList

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
    var productsModels: ArrayList<ProductsModel> = ArrayList()
    var typesModels: ArrayList<ProductsTypeModel> = ArrayList()
    var pendingCartModels: ArrayList<PendingCartModel> = ArrayList()
    var tempProductsModels: ArrayList<PendingCartModel> = ArrayList()
    var addressModels: ArrayList<AddressModel> = ArrayList()
    private lateinit var cartJArray: JSONArray
    var customerAddresses = ""
    var customerAddressId = "0"
    var productTypes: String = ""
    var productId: String = ""
    var tempAddressId = "0" // it is a temp variable for save addressId for first time.
    var originAddress =
        "" // it is a temp variable for save address for first time. if you change the editText content it never will change
    var sum = 0
    var isSame = false
    var addressChangeCounter =
        0 // this variable count the last edition of edtAddress. if more than 50% of address changed station set to 0
    var addressLength = 0
    private var pendingCartAdapter =
        PendingCartAdapter(pendingCartModels, object : PendingCartAdapter.TotalPrice {
            override fun collectTotalPrice(s: Int) {
                sum = 0
                if (s == 0) {
                    binding.txtSumPrice.text = "۰ تومان"
                    return
                }
                for (i in 0 until s) {
                    sum += Integer.valueOf(pendingCartModels[i].price.toInt() * pendingCartModels[i].quantity)
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
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSLight)
        MyApplication.configureAccount()
        refreshQueueStatus()
        binding.orderList.adapter = pendingCartAdapter
        disableViews()
        getProductsAndLists()

        MyApplication.handler.postDelayed({
            binding.edtMobile.requestFocus()
            KeyBoardHelper.showKeyboard(MyApplication.context)
        }, 500)

        binding.btnSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, OrdersListFragment(""))
                .replace()
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.vfAddressList.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
            customerAddressDialog()
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
                getCustomer(binding.txtCallerNum.text.toString())
            } else if (calls.isNotEmpty()) {
                calls[0].accept()
                getCustomer(binding.txtCallerNum.text.toString())
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
            if (binding.edtMobile.text.toString() == "" || binding.edtMobile.text.toString().length < 10) {
                MyApplication.Toast("شماره موبایل را وارد کنید", Toast.LENGTH_LONG)
                binding.edtMobile.requestFocus()
                return@setOnClickListener
            }
            getCustomer(binding.edtMobile.text.toString())
        }

        binding.imgClear.setOnClickListener {
            GeneralDialog()
                .message("میخواهید اطلاعات صفحه را پاک کنید؟")
                .firstButton("بله") { clearData() }
                .secondButton("نه", null)
                .show()
        }

        binding.imgAddOrder.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
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
                            .getString("name"),
                        1
                    )
                    if (pendingCartModels.size == 0) {
                        pendingCartModels.add(pendingCart)
                    } else {
                        for (j in 0 until pendingCartModels.size) {
                            if (pendingCartModels[j].id == productId) {
                                pendingCartModels[j].quantity++
                                isSame = true
                                break
                            }
                        }
                        if (!isSame) {
                            pendingCartModels.add(pendingCart)
                        } else {
                            isSame = false
                        }
                    }

                    sum += Integer.valueOf(
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("price")
                    )
                    binding.txtSumPrice.text =
                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"
                    break
                }
            }

            pendingCartAdapter.notifyDataSetChanged()
        }

        binding.txtSendMenu.setOnClickListener {
            if (binding.edtMobile.text.toString().trim()
                    .isEmpty() || (if (binding.edtMobile.text.toString().trim()
                        .startsWith("0")
                ) binding.edtMobile.text.toString()
                    .trim() else "0${binding.edtMobile.text.toString().trim()}").length < 11
            ) {
                MyApplication.Toast("لطفا شماره همراه را وارد نمایید.", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            sendMenu()
        }

        binding.edtMobile.setOnEditorActionListener { _, actionId, _ ->
            if (actionId === EditorInfo.IME_ACTION_NEXT) {
                if (binding.edtCustomerName.text.toString().isEmpty())
                    if (binding.edtMobile.text.toString() == "" || binding.edtMobile.text.toString().length < 10) {
                        MyApplication.Toast("شماره موبایل را وارد کنید", Toast.LENGTH_LONG)
                        binding.edtMobile.requestFocus()
                        return@setOnEditorActionListener false
                    }
                getCustomer(binding.edtMobile.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.edtMobile.text.toString() == "" || binding.edtMobile.text.toString().length < 10) {
                MyApplication.Toast("لطفا شماره موبایل را وارد کنید.", Toast.LENGTH_SHORT)
                binding.edtMobile.requestFocus()
                return@setOnClickListener
            }
            if (binding.edtCustomerName.text.toString() == "") {
                MyApplication.Toast("لطفا نام را وارد کنید.", Toast.LENGTH_SHORT)
                binding.edtCustomerName.requestFocus()
                return@setOnClickListener
            }
            if (binding.edtAddress.text.toString() == "") {
                MyApplication.Toast("لطفا آدرس را وارد کنید.", Toast.LENGTH_SHORT)
                binding.edtAddress.requestFocus()
                return@setOnClickListener
            }
            if (binding.edtStationCode.text.toString() == "" || binding.edtStationCode.text.toString() == "0") {
                MyApplication.Toast("لطفا ایستگاه را وارد کنید.", Toast.LENGTH_SHORT)
                binding.edtStationCode.requestFocus()
                return@setOnClickListener
            }
            if (pendingCartModels.size == 0) {
                MyApplication.Toast("لطفا محصول را انتخاب کنید.", Toast.LENGTH_SHORT)
                binding.spProductType.callOnClick()
                return@setOnClickListener
            }
            // this condition is for when you select an address that has credit, then you change(remove) 50 percent of that, so it is not a credit address any more
            val addressPercent: Int = addressLength * 50 / 100
            if (addressChangeCounter >= addressPercent) {
                binding.edtStationCode.setText("0")
            }

            customerAddressId = if (originAddress.trim() != binding.edtAddress.text.toString().trim()) {
                "0"
            } else {
                tempAddressId
            }

            cartJArray = JSONArray()
            for (i in 0 until pendingCartModels.size) {
                val cartJObj = JSONObject()
                cartJObj.put("_id", pendingCartModels[i].id)
                cartJObj.put("quantity", pendingCartModels[i].quantity)
                cartJObj.put("size", pendingCartModels[i].size)

                cartJArray.put(cartJObj)
            }
            GeneralDialog()
                .cancelable(false)
                .message("آیا از ثبت سفارش اطمینان دارید؟")
                .firstButton("بله") {
                    submitOrder()
                }
                .secondButton("خیر", null)
                .show()
        }

        setCursorEnd(window.decorView.rootView)

        binding.edtAddress.addTextChangedListener(addressTW)

//        binding.edtMobile.addTextChangedListener(tellTW)
    }

//    private val tellTW = object : TextWatcher {
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            disableViews()
//        }
//
//        override fun afterTextChanged(p0: Editable?) {
//            if (NumberValidation.havePrefix(p0.toString())) {
//                NumberValidation.removePrefix(p0.toString())
//            }
//            if (NumberValidation.isValid(p0.toString())) {
//                binding.edtMobile.text = p0
//            } else {
//                binding.edtAddress.setText("")
//                binding.edtDescription.setText("")
//                addressChangeCounter = 0
//                binding.edtAddress.setText("")
//                binding.edtStationCode.setText("")
//            }
//        }
//    }

    private var addressTW: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            addressChangeCounter += 1
        }

        override fun afterTextChanged(editable: Editable) {
            if (editable.toString().isEmpty()) {
                customerAddressId = "0"
                addressLength = 0
                binding.edtStationCode.text.clear()
            }
        }
    }

    private fun setCursorEnd(v: View?) {
        try {
            if (v is ViewGroup) {
                val vg = v
                for (i in 0 until vg.childCount) {
                    val child = vg.getChildAt(i)
                    setCursorEnd(child)
                }
            } else if (v is EditText) {
                val e = v
                e.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
                    if (b) MyApplication.handler.postDelayed(
                        { e.setSelection(e.text.length) },
                        200
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "RegisterOrderActivity class, setCursorEnd method")
            // ignore
        }
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
                            initProductSpinner("")
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
                            .getString("name"), 1
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

    private fun enterTheQueue(sipNumber: String) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.ENTER_QUEUE)
            .addParam("sipNumber", sipNumber)
            .addParam("sipPassword", MyApplication.prefManager.sipPassword)
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

    private fun getCustomer(mobileNo: String) {
        addressModels.clear()
        customerAddresses = ""
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
                        val clipboard =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip =
                            ClipData.newPlainText("tell", binding.edtMobile.text.toString())
                        clipboard.setPrimaryClip(clip)

                        binding.vfDownload.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val success = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (success) {
                            val dataObj = jsonObject.getJSONObject("data")
                            enableViews()
                            binding.edtCustomerName.requestFocus()
                            KeyBoardHelper.showKeyboard(MyApplication.context)
                            if (dataObj.getBoolean("status")) {
                                val customerObj = dataObj.getJSONObject("customer")
                                customerAddresses = customerObj.getJSONArray("locations").toString()
                                binding.edtCustomerName.setText(customerObj.getString("family"))

                                val orderStatus = dataObj.getJSONObject("orderStatus")

                                addressChangeCounter = 0
                                showLastAddress()
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
                                            " مشتری ${orderStatus.getInt("orderInterval")} دقیقه پیش سفارشی ثبت کرده است \n وضعیت سفارش : ${
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
                                                KeyBoardHelper.hideKeyboard()
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
                                binding.txtNewCustomer.visibility = View.VISIBLE
                            }
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

    private fun exitQueue(sipNumber: String) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.EXIT_QUEUE)
            .listener(exitTheQueueCallBack)
            .addParam("sipNumber", sipNumber)
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
        binding.edtMobile.setText("")
        binding.edtMobile.requestFocus()
        binding.edtCustomerName.setText("")
        binding.txtLockCustomer.visibility = View.GONE
        binding.txtNewCustomer.visibility = View.GONE
        binding.edtAddress.setText("")
        binding.edtStationCode.setText("")
        binding.edtDescription.setText("")
        pendingCartAdapter.notifyItemRangeRemoved(0, pendingCartModels.size)
        pendingCartModels.clear()
        addressModels.clear()
        customerAddresses = ""
        customerAddressId = "0"
        addressChangeCounter = 0
        addressLength = 0
        productId = ""
        isSame = false
        initProductTypeSpinner()
        initProductSpinner("")
        binding.txtSumPrice.text = "۰ تومان"
        sum = 0
        voipId = "0"
        disableViews()
    }

    private fun enableViews() {
        binding.txtSendMenu.isEnabled = true
        binding.vfSendMenu.isEnabled = true
        binding.edtCustomerName.isEnabled = true
        binding.llAddress.isEnabled = true
        binding.edtAddress.isEnabled = true
        binding.imgAddressList.isEnabled = true
        binding.edtStationCode.isEnabled = true
        binding.llCart.isEnabled = true
        binding.rlProductType.isEnabled = true
        binding.spProductType.isEnabled = true
        binding.llProduct.isEnabled = true
        binding.spProduct.isEnabled = true
        binding.imgAddOrder.isEnabled = true
        binding.edtDescription.isEnabled = true
    }

    private fun disableViews() {
        binding.txtSendMenu.isEnabled = false
        binding.vfSendMenu.isEnabled = false
        binding.edtCustomerName.isEnabled = false
        binding.llAddress.isEnabled = false
        binding.edtAddress.isEnabled = false
        binding.imgAddressList.isEnabled = false
        binding.edtStationCode.isEnabled = false
        binding.llCart.isEnabled = false
        binding.rlProductType.isEnabled = false
        binding.spProductType.isEnabled = false
        binding.llProduct.isEnabled = false
        binding.spProduct.isEnabled = false
        binding.imgAddOrder.isEnabled = false
        binding.edtDescription.isEnabled = false
    }

    private fun sendMenu() {
        binding.vfSendMenu.displayedChild = 1
        RequestHelper.builder(EndPoints.SEND_MENU)
            .addParam(
                "mobile",
                if (StringHelper.toEnglishDigits(binding.edtMobile.text.toString())
                        .startsWith("0")
                ) StringHelper.toEnglishDigits(binding.edtMobile.text.toString()) else "0${
                    StringHelper.toEnglishDigits(
                        binding.edtMobile.text.toString()
                    )
                }"
            )
            .listener(sendMenuCallBack)
            .post()
    }

    private val sendMenuCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
//            {"success":true,"message":"اس ام اس منو با موفقیت برای مشتری ارسال شد","data":{"status":true}}
            MyApplication.handler.post {
                try {
                    binding.vfSendMenu.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val data = jsonObject.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (status) {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") {}
                                .cancelable(true)
                                .show()
                        }
                    } else {
                        GeneralDialog()
                            .message(message)
                            .secondButton("باشه") {}
                            .cancelable(true)
                            .show()
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

    private fun submitOrder() {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.ADD_ORDER)
            .addParam(
                "mobile",
                if (binding.edtMobile.text.toString()
                        .startsWith("0")
                ) binding.edtMobile.text.toString() else "0${binding.edtMobile.text}"
            )
            .addParam("family", binding.edtCustomerName.text.trim().toString())
            .addParam("address", binding.edtAddress.text.trim().toString())
            .addParam("addressId", customerAddressId)
            .addParam("station", binding.edtStationCode.text.trim())
            .addParam("products", cartJArray)
            .addParam("description", binding.edtDescription.text.trim().toString())
            .listener(submitOrderCallBack)
            .post()
    }

    private val submitOrderCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    LoadingDialog.dismissCancelableDialog()
//                    {"success":true,"message":"ایستگاه موجود نمی باشد","data":{"status":false}}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val data = jsonObject.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (status) {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { clearData() }
                                .cancelable(true)
                                .show()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") {}
                                .cancelable(true)
                                .show()
                        }
                    } else {
                        GeneralDialog()
                            .message(message)
                            .secondButton("باشه") {}
                            .cancelable(true)
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LoadingDialog.dismissCancelableDialog()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            super.onFailure(reCall, e)
            LoadingDialog.dismissCancelableDialog()
        }
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

    private val mListener = object : CoreListenerStub() {
        override fun onRegistrationStateChanged(
            lc: Core,
            proxy: ProxyConfig,
            state: RegistrationState,
            message: String
        ) {
            if (core.defaultProxyConfig != null && core.defaultProxyConfig == proxy) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state))
            } else if (core.defaultProxyConfig == null) {
                binding.imgSipStatus.setImageResource(getStatusIconResource(state))
            }
            try {
                binding.imgSipStatus.setOnClickListener {
                    val core: Core = LinphoneService.getCore()
                    if (core != null) {
                        core.refreshRegisters()
                    }
                }
            } catch (ise: IllegalStateException) {
                ise.printStackTrace()
            }
        }
    }

    private fun getStatusIconResource(state: RegistrationState): Int {
        try {
            val core = LinphoneService.getCore()
            val defaultAccountConnected =
                core != null && core.defaultProxyConfig != null && core.defaultProxyConfig.state == RegistrationState.Ok
            if (state == RegistrationState.Ok && defaultAccountConnected) {
                return R.drawable.ic_led_connected
            } else if (state == RegistrationState.Progress) {
                return R.drawable.ic_led_inprogress
            } else if (state == RegistrationState.Failed) {
                return R.drawable.ic_led_error
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return R.drawable.ic_led_error
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

    private fun showLastAddress() {
        val addressArr = JSONArray(customerAddresses)
        val addressObj = addressArr.getJSONObject(addressArr.length() - 1)
        val address = addressObj.getString("address")
        originAddress = address
        binding.edtAddress.setText(address)
        binding.edtStationCode.setText(
            if (addressObj.has("station")) addressObj.getJSONObject("station").getInt("code")
                .toString() else "0"
        )
        addressLength = address.length
        customerAddressId = if (addressObj.has("_id")) addressObj.getString("_id") else "0"
        tempAddressId = if (addressObj.has("_id")) addressObj.getString("_id") else "0"
    }

    private fun customerAddressDialog() {
        addressModels.clear()
        if (customerAddresses == "") {
            MyApplication.Toast("ادرسی موجود نیست", Toast.LENGTH_SHORT)
            return
        }
        val addressArr = JSONArray(customerAddresses)
        for (i in 0 until addressArr.length()) {
            val addressObj = addressArr.getJSONObject(i)
            val addressModel = AddressModel(
                addressObj.getString("address"),
                if (addressObj.has("_id")) addressObj.getString("_id") else "0",
                if (addressObj.has("station")) addressObj.getJSONObject("station").getInt("code")
                    .toString() else "0"
            )
            addressModels.add(addressModel)
        }
        if (addressModels.size == 0) {
            MyApplication.Toast("ادرسی موجود نیست", Toast.LENGTH_SHORT)
        } else {
            AddressDialog().show(
                addressModels
            ) { addressModel ->
                addressChangeCounter = 0
                addressLength = addressModel.address.length
                customerAddressId = addressModel.addressId
                tempAddressId = addressModel.addressId
                binding.edtAddress.setText(addressModel.address)
                originAddress = addressModel.address
                binding.edtStationCode.setText(addressModel.stationId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        showTitleBar()
        MyApplication.prefManager.isAppRun = true
        core.addListener(mListener)
        val lpc = core.defaultProxyConfig
        if (lpc != null) {
            mListener.onRegistrationStateChanged(core, lpc, lpc.state, "")
        }
        if (MyApplication.prefManager.connectedCall) {
            startCallQuality()
            binding.imgCallOption.setImageResource(R.drawable.ic_call_dialog_enable)
            val calls = core.calls
            for (call in calls) {
                if (call != null && call.state == Call.State.StreamsRunning) {
                    if (voipId == "0") {
                        val address = call.remoteAddress
                        binding.edtMobile.setText(NumberValidation.removePrefix(address.username))
                        MyApplication.handler.postDelayed({ getCustomer(address.username) }, 600)
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
        MyApplication.prefManager.isAppRun = false
        KeyBoardHelper.hideKeyboard()
        if (core != null) {
            core.removeListener(mListener)
        }
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
                    startActivity(Intent(MyApplication.currentActivity, MainActivity::class.java))
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }
}
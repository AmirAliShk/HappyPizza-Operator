package ir.food.operatorAndroid.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.OrdersAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.CallDialog
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.OrderModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.sip.LinphoneService
import org.json.JSONObject
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub

class OrdersListFragment(mobile: String) : Fragment() {

    lateinit var binding: FragmentOrdersListBinding
    var value = "mobile"
    lateinit var core: Core
    var tellNumber: String = mobile

    var orderModels: ArrayList<OrderModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersListBinding.inflate(layoutInflater)

        TypefaceUtil.overrideFonts(binding.root)
        binding.edtSearchBar.requestFocus()
        if (tellNumber != "") {
            binding.edtSearchBar.setText(tellNumber)
            binding.imgSearchType.setImageResource(R.drawable.ic_phone)
            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_NUMBER
            value = "mobile"
            getOrders(binding.edtSearchBar.text.toString())
        }

        binding.imgRefresh.setOnClickListener {
            if (binding.edtSearchBar.text.toString() == "") {
                MyApplication.Toast("لطفا موردی برای جست و جو وارد کنید", Toast.LENGTH_LONG)
                binding.edtSearchBar.requestFocus()
                return@setOnClickListener
            }
            getOrders(binding.edtSearchBar.text.toString())
        }

        binding.imgSearch.setOnClickListener {
            if (binding.edtSearchBar.text.toString() == "") {
                MyApplication.Toast("لطفا موردی برای جست و جو وارد کنید", Toast.LENGTH_LONG)
                binding.edtSearchBar.requestFocus()
                return@setOnClickListener
            }
            KeyBoardHelper.hideKeyboard()
            getOrders(binding.edtSearchBar.text.toString())
        }

        binding.imgSearchType.setOnClickListener {
            SearchDialog().show(object : SearchDialog.SearchListener {
                override fun searchType(searchType: Int) {
                    binding.edtSearchBar.setText("")
                    when (searchType) {
                        1 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_phone)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_NUMBER
                            value = "mobile"
                        }
                        2 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_user)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                            value = "family"
                        }
                        3 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_gps)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                            value = "address"
                        }
                    }
                }
            })
        }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        if (MyApplication.prefManager.connectedCall) {
            binding.imgEndCall.setBackgroundResource(R.drawable.bg_pink_edge)
        } else {
            binding.imgEndCall.setBackgroundResource(0)
        }

        binding.imgEndCall.setOnClickListener {
            if (MyApplication.prefManager.connectedCall) {
                CallDialog().show(object : CallDialog.CallDialogInterface {
                    override fun onDismiss() {}

                    override fun onCallReceived() {}

                    override fun onCallTransferred() {}

                    override fun onCallEnded() {
                        binding.imgEndCall.setBackgroundResource(0)
                    }
                }, true)
            } else {
                MyApplication.Toast("در حال حاضر تماسی برقرار نیست", Toast.LENGTH_SHORT)
            }
        }

        binding.edtSearchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                if (binding.edtSearchBar.text.toString() == "") {
                    MyApplication.Toast("لطفا موردی برای جست و جو وارد کنید", Toast.LENGTH_LONG)
                    binding.edtSearchBar.requestFocus()
                    return@setOnEditorActionListener false
                }
                KeyBoardHelper.hideKeyboard()
                getOrders(binding.edtSearchBar.text.toString())

                return@setOnEditorActionListener true
            }
            false
        }

        return binding.root
    }

    private fun getOrders(searchText: String) {
        binding.vfOrders.displayedChild = 1
        var text = searchText
        if (value == "mobile") {
            if (!searchText.startsWith("0")) {
                text = "0$searchText"
            }
        }
        RequestHelper.builder(EndPoints.GET_ORDERS)
            .listener(callBack)
            .addPath(value)
            .addPath(text)
            .get()
    }

    private val callBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        orderModels.clear()
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataArr = jsonObject.getJSONArray("data")
                            for (i in 0 until dataArr.length()) {
                                val dataObj = dataArr.getJSONObject(i)
                                val model = OrderModel(
                                    dataObj.getString("_id"),
                                    dataObj.getJSONObject("status").getString("name"),
                                    dataObj.getJSONObject("status").getInt("status"),
                                    dataObj.getString("createdAt"),
                                    dataObj.getJSONObject("customer").getString("family"),
                                    dataObj.getJSONObject("customer").getString("mobile"),
                                    dataObj.getString("address"),
                                    dataObj.getBoolean("paid")
                                )
                                orderModels.add(model)
                            }
                            if (orderModels.size == 0) {
                                binding.vfOrders.displayedChild = 0
                            } else {
                                binding.vfOrders.displayedChild = 2
                                val adapter = OrdersAdapter(orderModels)
                                binding.searchList.adapter = adapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfOrders.displayedChild = 3
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfOrders.displayedChild = 3
                }
            }
        }

    var mCoreListener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            if (state == Call.State.End) {
                binding.imgEndCall.setBackgroundResource(0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        core = LinphoneService.getCore()
        core.addListener(mCoreListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        core.removeListener(mCoreListener)
    }
}
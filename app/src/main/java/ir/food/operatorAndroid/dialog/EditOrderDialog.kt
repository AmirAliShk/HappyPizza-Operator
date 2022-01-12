package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.downloader.PRDownloader
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.EditOrderCartAdapter
import ir.food.operatorAndroid.adapter.SpinnerAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogEditOrderBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.helper.VoiceDownloader
import ir.food.operatorAndroid.model.*
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONArray
import org.json.JSONObject

class EditOrderDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogEditOrderBinding

    var typesModels: ArrayList<ProductsTypeModel> = ArrayList()
    var tempProductsModels: ArrayList<PendingCartModel> = ArrayList()
    private var productsModels: ArrayList<ProductsModel> = ArrayList()
    lateinit var supportCartModels: ArrayList<EditOrderModel>
    lateinit var oldSupportCartModels: ArrayList<EditOrderModel>
    var productTypes: String = ""
    var productId: String = ""
    var sum = 0
    var isSame = false
    private lateinit var productObj: JSONObject
    private val cartAdapter =
        EditOrderCartAdapter(productsModels, object : EditOrderCartAdapter.TotalPrice {
            override fun collectTotalPrice(s: Int) {
                sum = 0
                if (s == 0) {
//                    binding.txtSumPrice.text = "۰ تومان"
                    return
                }
                for (i in 0 until s) {
//                    sum += Integer.valueOf(supportCartModels[i].price.toInt() * supportCartModels[i].quantity)
//                    binding.txtSumPrice.text =
//                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"
                }
            }
        })

    fun show(productArr: JSONArray, orderId: String) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogEditOrderBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        initProductTypeSpinner()
        initProductSpinner("")

        supportCartModels = ArrayList()
        oldSupportCartModels = ArrayList()

        for (i in 0 until productArr.length()) {
            productObj = productArr.getJSONObject(i)
            val cartModel = EditOrderModel(
                productObj.getString("id"),
                productObj.getString("name"),
                productObj.getInt("quantity"),
                productObj.getString("size")
            )
            oldSupportCartModels.add(cartModel)
            supportCartModels.add(cartModel)
        }

        binding.orderList.adapter = cartAdapter

        binding.imgAddOrder.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
            if (productId.isEmpty()) {
                return@setOnClickListener
            }
            for (i in 0 until JSONArray(MyApplication.prefManager.productsList).length()) {
                if (productsModels[i].id == productId) {
                    val pendingCart = EditOrderModel(
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getString("_id"),
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getString("name"),
                        1,
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("name")
                    )
                    if (supportCartModels.size == 0) {
                        supportCartModels.add(pendingCart)
                    } else {
                        for (j in 0 until supportCartModels.size) {
                            if (productsModels[i].supply == supportCartModels[j].quantity && supportCartModels[j].id == productsModels[i].id) {
                                MyApplication.Toast("تعداد از این بیشتر نمیشه", Toast.LENGTH_SHORT)
                                return@setOnClickListener
                            }
                            if (supportCartModels[j].id == productId) {
                                supportCartModels[j].quantity++
                                isSame = true
                                break
                            }
                        }
                        if (!isSame) {
                            supportCartModels.add(pendingCart)
                        } else {
                            isSame = false
                        }
                    }

                    sum += Integer.valueOf(
                        JSONArray(MyApplication.prefManager.productsList).getJSONObject(i)
                            .getJSONArray("size").getJSONObject(0)
                            .getString("price")
                    )
//                    binding.txtSumPrice.text =
//                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"
                    break
                }
            }

            cartAdapter.notifyDataSetChanged()
        }

        binding.btnSubmit.setOnClickListener {
            compare(oldSupportCartModels, supportCartModels, orderId)
        }

        binding.imgClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun compare(
        oldList: ArrayList<EditOrderModel>,
        currentOrders: ArrayList<EditOrderModel>,
        orderId: String
    ) {
        Log.i("TAG", "compare: oldList $oldList")
        Log.i("TAG", "compare: currentOrders $currentOrders")

        var orderArray = JSONArray()
        var orderObject = JSONObject()
        for (i in 0 until oldList.size) {
            for (j in 0 until currentOrders.size) {
                if (oldList[i].id == currentOrders[j].id) { // it means there is an order that the quantity was changed
                    if (oldList[i].quantity != currentOrders[j].quantity) {
                        orderObject = JSONObject()
                        orderObject.put("id", currentOrders[j].id)
                        orderObject.put("quantity", oldList[i].quantity - currentOrders[j].quantity)
                        orderObject.put("size", currentOrders[j].size)
                    }
                }
                if (!currentOrders[j].id.contains(oldList[i].id)) { // it means a new order was added
                    orderObject = JSONObject()
                    orderObject.put("id", currentOrders[j].id)
                    orderObject.put("quantity", currentOrders[j].quantity)
                    orderObject.put("size", currentOrders[j].size)
                }
                if (!oldList[i].id.contains(currentOrders[j].id)) { // it means an order was deleted
                    orderObject = JSONObject()
                    orderObject.put("id", currentOrders[j].id)
                    orderObject.put("quantity", currentOrders[j].quantity)
                    orderObject.put("size", currentOrders[j].size)
                }
                orderArray.put(orderObject)
            }
        }

        Log.i("TAG", "compare:finalList $orderArray")

//        editOrder(orderArray, orderId)

    }

    private fun dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "dismiss: " + e.message)
            AvaCrashReporter.send(e, "EditOrderDialog class, dismiss method")
        }
    }

    private fun initProductTypeSpinner() {
        val typesList = ArrayList<String>()
        try {
//            typesList.add(0, "نوع محصول")
            val typesArr = JSONArray(MyApplication.prefManager.productsTypeList)
            for (i in 0 until typesArr.length()) {
                val types = ProductsTypeModel(
                    typesArr.getJSONObject(i).getString("_id"),
                    typesArr.getJSONObject(i).getString("name")
                )
                typesModels.add(types)

                typesList.add(i, typesArr.getJSONObject(i).getString("name"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditOrderDialog class, initProductTypeSpinner method")
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
//                        if (position == 0) {
//                            productTypes = ""
//                            return
//                        }
                        tempProductsModels.clear()
                        productTypes = typesModels[position].id
                        initProductSpinner(productTypes)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditOrderDialog class, initProductTypeSpinner method2")
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
                    productsArr.getJSONObject(i).getJSONObject("type"),
                    productsArr.getJSONObject(i).getInt("supply")
                )
                productsModels.add(products)
                if (productsModels[i].type.getString("_id").equals(type)) {
                    val pendingCart = PendingCartModel(
                        productsArr.getJSONObject(i).getString("_id"),
                        productsArr.getJSONObject(i).getString("name"),
                        productsArr.getJSONObject(i).getString("nameWithSupply"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("price"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("name"), 1
                    )
                    tempProductsModels.add(pendingCart)
                    productsList.add(productsArr.getJSONObject(i).getString("nameWithSupply"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditOrderDialog class, initProductSpinner method")
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

    private fun editOrder(cartJArray: JSONArray, orderId: String) {
        LoadingDialog.makeCancelableLoader()
        RequestHelper.builder(EndPoints.EDIT_ORDER)
            .addParam("orderId", orderId)
            .addParam("products", cartJArray)
            .listener(editOrderCallBack)
            .put()
    }

    private val editOrderCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    LoadingDialog.dismissCancelableDialog()
//                    {"success":true,"message":"ایستگاه موجود نمی باشد","data":{"status":false}}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    var message = jsonObject.getString("message")
                    if (success) {
                        val data = jsonObject.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (status) {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { dialog.dismiss() }
                                .cancelable(false)
                                .show()
                        } else {
                            if (data.has("products") && data.getJSONArray("products").length() != 0
                            ) {
                                var productsName = ""
                                val productsArr = data.getJSONArray("products")
                                for (i in 0 until productsArr.length()) {
                                    productsName = if (i == 0) {
                                        "${productsArr[i]}"
                                    } else {
                                        "$productsName و ${productsArr[i]}"
                                    }
                                }
                                message = " موجودی محصول $productsName کافی نمیباشد "
                            }
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") {}
                                .cancelable(false)
                                .show()
                        }
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
                    AvaCrashReporter.send(e, "EditOrderDialog class, submitOrderCallBack method")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            super.onFailure(reCall, e)
            LoadingDialog.dismissCancelableDialog()
        }
    }
}
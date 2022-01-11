package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.PendingCartAdapter
import ir.food.operatorAndroid.adapter.SpinnerAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogEditOrderBinding
import ir.food.operatorAndroid.helper.KeyBoardHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.*
import ir.food.operatorAndroid.okHttp.RequestHelper
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONArray
import org.json.JSONObject

class EditOrderDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogEditOrderBinding

    var typesModels: ArrayList<ProductsTypeModel> = ArrayList()
    private var productsModels: ArrayList<PendingCartModel> = ArrayList()
    private val pendingSupportCartModels: ArrayList<PendingCartModel> = ArrayList()

    var productTypes: String = ""

    var productId: String = ""
    lateinit var product: PendingCartModel

    var sum = 0
    private lateinit var productObj: JSONObject
    private val cartAdapter =
        PendingCartAdapter(pendingSupportCartModels, object : PendingCartAdapter.TotalPrice {
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

        for (i in 0 until productArr.length()) {
            productObj = productArr.getJSONObject(i)
            val cartModel = PendingCartModel(
                productObj.getString("id"),
                productObj.getString("name"),
                "",
                productObj.getString("price"),
                productObj.getString("size"),
                productObj.getInt("quantity"),
                0,
                productObj.getString("discount")
            )
            pendingSupportCartModels.add(cartModel)
        }

        binding.orderList.adapter = cartAdapter

        binding.imgAddOrder.setOnClickListener {
            KeyBoardHelper.hideKeyboard()
            if (productId.isEmpty()) {
                return@setOnClickListener
            }

            if (pendingSupportCartModels.size == 0) {
                pendingSupportCartModels.add(product)
            } else {
                if (pendingSupportCartModels.contains(product)) {
                    for (j in 0 until pendingSupportCartModels.size) {
                        if (product.supply == pendingSupportCartModels[j].quantity) {
                            MyApplication.Toast("تعداد از این بیشتر نمیشه", Toast.LENGTH_SHORT)
                            return@setOnClickListener
                        }
                        if (pendingSupportCartModels[j].id == productId) {
                            pendingSupportCartModels[j].quantity++
                            break
                        }
                    }
                } else {
                    pendingSupportCartModels.add(product)
                }
            }

            sum += product.price.toInt()
//                    binding.txtSumPrice.text =
//                        StringHelper.toPersianDigits(StringHelper.setComma(sum.toString())) + " تومان"

            cartAdapter.notifyDataSetChanged()
        }

        binding.btnEditOrder.setOnClickListener {

            Log.e("TAG", "sending array object: ")
//            editOrder(cartJArray, orderId)
        }

        binding.imgClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
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
                        productsModels.clear()
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
                if (productsArr.getJSONObject(i).getJSONObject("type").getString("_id")
                        .equals(type)
                ) {
                    val pendingCart = PendingCartModel(
                        productsArr.getJSONObject(i).getString("_id"),
                        productsArr.getJSONObject(i).getString("name"),
                        productsArr.getJSONObject(i).getString("nameWithSupply"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("price"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0)
                            .getString("name"), 1,
                        productsArr.getJSONObject(i).getInt("supply")
                    )
                    productsModels.add(pendingCart)
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
                        productId = productsModels[position - 1].id
                        product = productsModels[position - 1]
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
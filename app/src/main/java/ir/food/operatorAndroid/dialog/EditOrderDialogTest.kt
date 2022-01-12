package ir.food.operatorAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.EditOrderCartAdapter
import ir.food.operatorAndroid.adapter.SpinnerAdapter
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.DialogEditOrderBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.EditOrderModel
import ir.food.operatorAndroid.model.ProductsModel
import ir.food.operatorAndroid.model.ProductsTypeModel
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONArray
import org.json.JSONObject

class EditOrderDialogTest {
    lateinit var dialog: Dialog
    lateinit var binding: DialogEditOrderBinding

    var isSame = false
    lateinit var productsModels: ArrayList<ProductsModel> // this list store all type of product, like : peperoni, french, chicken
    var typesModels: ArrayList<ProductsTypeModel> =
        ArrayList() // this list store all type of products type, like : pizza, sandwich, drink ect
    private var cartModels: ArrayList<ProductsModel> =
        ArrayList() // this is contain what you select for buy
    var productModel: ProductsModel? = null
    private val cartAdapter =
        EditOrderCartAdapter(cartModels, object : EditOrderCartAdapter.TotalPrice {
            override fun collectTotalPrice(s: Int) {
//                sum = 0
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
            val productObj = productArr.getJSONObject(i)
            val cartModel = ProductsModel(
                productObj.getString("id"),
                JSONArray(),
                productObj.getString("name"),
                "",
                JSONObject(),
                1,
                productObj.getInt("quantity")
            )
            cartModels.add(cartModel)
        }

        binding.orderList.adapter = cartAdapter

        binding.imgAddOrder.setOnClickListener {
            if (productModel == null) {
                binding.spProduct.performClick()
                return@setOnClickListener
            }

            if (cartModels.size == 0) {
                cartModels.add(productModel!!)
            } else {
                for (i in 0 until cartModels.size) {
                    if (productModel!!.id == cartModels[i].id) {
                        if (cartModels[i].supply == productModel!!.quantity) {
                            MyApplication.Toast("تعداد از این بیشتر نمیشه", Toast.LENGTH_SHORT)
                            return@setOnClickListener
                        } else {
                            cartModels[i].quantity++
                            isSame = true
                            break
                        }
                    }
                }
                if (!isSame) {
                    cartModels.add(productModel!!)
                } else {
                    isSame = false
                }
            }

            cartAdapter.notifyDataSetChanged()
        }

        binding.btnSubmit.setOnClickListener {

        }

        binding.imgClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun initProductTypeSpinner() {
        val typesList = ArrayList<String>()
        try {
            val typesArr = JSONArray(MyApplication.prefManager.productsTypeList)
            for (i in 0 until typesArr.length()) {
                val types = ProductsTypeModel(
                    typesArr.getJSONObject(i).getString("_id"),
                    typesArr.getJSONObject(i).getString("name")
                )
                typesModels.add(types)

                typesList.add(i, typesArr.getJSONObject(i).getString("name"))
            }

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
                        Log.i("TAG", "onItemSelected: $position")
                        Log.i("TAG", "onItemSelected: ${typesModels[position].id}")
                        initProductSpinner(typesModels[position].id)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditOrderDialog class, initProductTypeSpinner method")
        }
        if (binding.spProductType == null) return
    }

    private fun initProductSpinner(type: String) {
        val productsList = ArrayList<String>()
        productsModels = ArrayList()
        val productsArr = JSONArray(MyApplication.prefManager.productsList)
        try {
            productsList.add(0, "محصولات")
            for (i in 0 until productsArr.length()) {
                if (productsArr.getJSONObject(i).getJSONObject("type").getString("_id").equals(type)) {
                    val products = ProductsModel(
                        productsArr.getJSONObject(i).getString("_id"),
                        productsArr.getJSONObject(i).getJSONArray("size"),
                        productsArr.getJSONObject(i).getString("name"),
                        productsArr.getJSONObject(i).getString("description"),
                        productsArr.getJSONObject(i).getJSONObject("type"),
                        productsArr.getJSONObject(i).getInt("supply")
                    )
                    productsModels.add(products)
                    productsList.add(productsArr.getJSONObject(i).getString("nameWithSupply"))
                }
            }

            binding.spProduct.adapter =
                SpinnerAdapter(MyApplication.context, R.layout.item_spinner, productsList)

            binding.spProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        return
                    }
                    productModel = productsModels[position - 1]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "EditOrderDialog class, initProductSpinner method")
        }
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

}
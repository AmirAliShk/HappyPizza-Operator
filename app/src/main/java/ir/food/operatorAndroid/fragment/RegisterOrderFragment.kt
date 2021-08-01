package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.CartAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentRegisterOrderBinding
import ir.food.operatorAndroid.helper.DateHelper
import ir.food.operatorAndroid.helper.DateHelper.YearMonthDate
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.CartModel
import ir.food.operatorAndroid.model.ProductsModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class RegisterOrderFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    lateinit var binding: FragmentRegisterOrderBinding
    private var customer: JSONObject = JSONObject()
    private var products: JSONObject = JSONObject()
    lateinit var productsModel: ArrayList<ProductsModel>
    var cartModels: ArrayList<CartModel> = ArrayList()

    //    lateinit var productName: String
//    lateinit var productId: String
//    lateinit var productPrice: String
    lateinit var cartModel: CartModel
    private lateinit var datePickerDialog: DatePickerDialog
    var DATEPICKER = "DatePickerDialog";
    private lateinit var selectedDate: Date
    private lateinit var jalaliDate: YearMonthDate
    lateinit var to: String
    lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterOrderBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = this.resources.getColor(R.color.darkGray)
            window?.navigationBarColor = this.resources.getColor(R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

//        binding.btnSubmit.setOnClickListener {
//            registerOrder()
//        }


        binding.imgAddOrder.setOnClickListener {
//            if (cartModel.product?.id?.isEmpty() == true) {
//                return@setOnClickListener
//            }
//            var model = cartModel
//            cartModels.add(model)
//            adapter = CartAdapter(cartModels)
//            binding.listOrders.adapter = adapter;
//            adapter.notifyDataSetChanged()
//            addItem(cartModel)
            cartModel.product?.let { it1 -> isAlreadyInCart(it1.id) }
        }

        initProductSpinner()

        return binding.root
    }

    private fun isAlreadyInCart(targetItemId: String): Boolean {
        var isAlreadyInCart = false
        for (i in 0 until cartModels.size) {
            if (targetItemId.equals(cartModels[i])) {
                isAlreadyInCart = true
                break
            }
        }
        return isAlreadyInCart
    }

    private fun initProductSpinner() {
        productsModel = ArrayList<ProductsModel>()
        val productList = ArrayList<String>()
        try {
            val cityArr = JSONArray(MyApplication.prefManager.products)
            productList.add(0, "انتخاب نشده")
            for (i in 0 until cityArr.length()) {
                val productObj = cityArr.getJSONObject(i)
                val cityModel = ProductsModel(
                    productObj.getBoolean("active"),
                    productObj.getString("_id"),
                    productObj.getString("name"),
                    productObj.getString("sellingPrice"),
                    productObj.getString("updatedAt"),
                    productObj.getString("createdAt"),
                    productObj.getString("description"),
                )
//                                "active":true,
//                                "_id":"60d7291c519b311c905f9567",
//                                "name":"لاته",
//                                "sellingPrice":"17000",
//                                "user":"60d72865519b311c905f9566",
//                                "updatedAt":"2021-06-27T10:49:51.916Z",
//                                "createdAt":"2021-06-26T13:18:20.104Z",
//                                "v":0,
//                                "description":"شیر قهوه"

                productsModel.add(cityModel)
                productList.add(i + 1, productObj.getString("name"))
            }
//            if (binding.spProducts == null) return
//            binding.spProducts.adapter = ArrayAdapter(
//                MyApplication.currentActivity,
//                R.layout.item_spinner,
//                productList
//            )
//            binding.spProducts.onItemSelectedListener = object : OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View,
//                    position: Int,
//                    id: Long,
//                ) {
//                    if (position == 0) {
//                        CartModel(null, 1)
////                        productName = ""
////                        productId = ""
////                        productPrice = ""
//                        return
//                    }
//                    CartModel(productsModel[position - 1], 1)
////                    cartModel = productsModel[position - 1].name
////                    productId = productsModel[position - 1].id
////                    productPrice = productsModel[position - 1].sellingPrice
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {}
//            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun customerInfo() {
//        RequestHelper.builder(EndPoints.CUSTOMER_INFO + binding.edtMobile.text.toString())
//            .listener(infoCallBack)
//            .get()
    }

    private val infoCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    success: true,
//                    message: "اطلاعات مشتری با موفقیت ارسال شد",
//                    data: {
//                        family: "مصطفایی",
//                        mobile: "09625846122",
//                        birthday: "1990-12-18T23:59:00.798Z"
//                    }
                    val response = JSONObject(args[0].toString())
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONObject("data")
                    } else {
                        MyApplication.Toast("مشتری جدید", Toast.LENGTH_SHORT)//todo
                    }

                } catch (e: JSONException) {

                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            super.onFailure(reCall, e)
        }
    }

    private fun registerOrder() {
//        customer.put("family", binding.edtName.text.toString())
//        customer.put("mobile", binding.edtMobile.text.toString())
//        customer.put("birthday", to)//todo
//
//        products.put("_id", cartModel.product?.id)
//        products.put("quantity", 2)//todo
//        products.put("sellingPrice", cartModel.product?.sellingPrice)
//
//        RequestHelper.builder(EndPoints.ORDER)
//            .addParam("customer", customer)
//            .addParam("reminder", binding.edtReminder.text.toString())
//            .addParam("products", "")
//            .listener(registerOrderCallback)
//            .post()
//                        {
//                            products: [...{
//                            _id: "60b72a70e353f0385c2fe5af",
//                            quantity: 2,
//                            sellingPrice: "30000"
//                        }],
//                            customer: {
//                            family: "شکوهی",
//                            mobile: "09307580142",
//                            birthday: "2021-05-31T05:42:13.845Z"
//                        },
//                            reminder: 7,
//                            duration: 40,
//                            address: "معلم 24"
//                        }
    }

    private val registerOrderCallback: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                    } catch (e: JSONException) {

                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {

                }
                super.onFailure(reCall, e)
            }

        }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date =
            StringHelper.toPersianDigits(DateHelper.strPersianSeven(selectedDate)) + dayOfMonth.toString() + "-" + (monthOfYear + 1).toString() + "-" + year
        to = date
    }
}
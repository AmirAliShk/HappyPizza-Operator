package ir.team_x.crm.fragment

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
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentRegisterOrderBinding
import ir.team_x.crm.helper.DateHelper
import ir.team_x.crm.helper.StringHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.model.ProductsModel
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class RegisterOrderFragment : Fragment() {

    lateinit var binding: FragmentRegisterOrderBinding
    private var customer: JSONObject = JSONObject()
    private var products: JSONObject = JSONObject()
    lateinit var productsModel: ArrayList<ProductsModel>
    lateinit var productName: String
    lateinit var productId: String
    lateinit var productPrice: String
    private lateinit var datePickerDialog: DatePickerDialog
    var DATEPICKER = "DatePickerDialog";

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

        binding.btnSubmit.setOnClickListener {
            registerOrder()
        }

        binding.edtBirthday.setOnClickListener {
            val persianCalendar = PersianCalendar()
            datePickerDialog = DatePickerDialog.newInstance(
                { _: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val jalaliDate =
                        DateHelper.YearMonthDate(year, monthOfYear + 1, dayOfMonth, 23, 59, 0)
                    val selectedDate = DateHelper.jalaliToGregorian(jalaliDate)
                    val currentDate: Date = DateHelper.getCurrentGregorianDate()

                    if (selectedDate.time >= currentDate.time) {
                        MyApplication.Toast(
                            "نباید از تاریخ امروز بیشتر انتخاب کنی!",
                            Toast.LENGTH_SHORT
                        )

//                        binding.edtBirthday.setText(
//                            StringHelper.toPersianDigits(
//                                DateHelper.strPersianSeven(
//                                    currentDate
//                                )
//                            )
//                        )
                    } else {
                        binding.edtBirthday.setText(
                            StringHelper.toPersianDigits(
                                DateHelper.strPersianSeven(
                                    selectedDate
                                )
                            )
                        )
                    }
                },
                persianCalendar.persianYear,
                persianCalendar.persianMonth,
                persianCalendar.persianDay
            )
            datePickerDialog.maxDate = persianCalendar
            datePickerDialog.show(
                MyApplication.currentActivity.fragmentManager,
                DATEPICKER
            )
        }

        initProductSpinner()

        return binding.root
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
            if (binding.spProducts == null) return
            binding.spProducts.adapter = ArrayAdapter(
                MyApplication.currentActivity,
                R.layout.item_spinner,
                productList
            )
            binding.spProducts.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    if (position == 0) {
                        productName = ""
                        productId = ""
                        productPrice = ""
                        return
                    }
                    productName = productsModel[position - 1].name
                    productId = productsModel[position - 1].id
                    productPrice = productsModel[position - 1].sellingPrice
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun customerInfo() {
        RequestHelper.builder(EndPoints.CUSTOMER_INFO)
            .addParam("mobile", binding.edtMobile.text.toString())
            .listener(infoCallBack)
            .get()
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
                        binding.edtName.setText(dataObject.getString("family"))
                        binding.edtMobile.setText(dataObject.getString("mobile"))
                        binding.edtBirthday.setText(dataObject.getString("birthday"))
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
        customer.put("family", binding.edtName.text.toString())
        customer.put("mobile", binding.edtMobile.text.toString())
        customer.put("birthday", binding.edtBirthday.text.toString())

        products.put("_id", productId)
        products.put("quantity", 2)//todo
        products.put("sellingPrice", productPrice)

        RequestHelper.builder(EndPoints.REGISTER_ORDER)
            .addParam("customer", customer)
            .addParam("reminder", binding.edtReminder.text.toString())
            .addParam("products", "")
            .listener(registerOrderCallback)
            .post()
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
}
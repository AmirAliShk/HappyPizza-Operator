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
import android.widget.SpinnerAdapter
import androidx.fragment.app.Fragment
import ir.team_x.crm.R
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentRegisterOrderBinding
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.model.ProductsModel
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterOrderFragment : Fragment() {

    lateinit var binding: FragmentRegisterOrderBinding
    private var customer: JSONObject = JSONObject()
    lateinit var productsModel: ArrayList<ProductsModel>
    lateinit var productName: String
    lateinit var productId: String
    lateinit var productPrice: String


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
        initProductSpinner()
        return binding.root
    }

//    private fun initProductSpinner() {
//     var productsModel : ArrayList<>()
//
//    }

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
            binding.spProducts.adapter = ArrayAdapter(MyApplication.currentActivity,
                R.layout.item_spinner,
                productList)
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
                        productPrice = "0"
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

    private fun registerOrder() {
        customer.put("family", binding.edtName.text.toString())
        customer.put("mobile", binding.edtMobile.text.toString())
        customer.put("birthday", binding.edtBirthday.text.toString())

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
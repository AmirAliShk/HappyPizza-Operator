package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.CartAdapter
import ir.food.operatorAndroid.adapter.OrdersAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrderDetailBinding
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.EditAddressDialog
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.RegisterComplaintDialog
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.DateHelper
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.CartModel
import ir.food.operatorAndroid.model.OrderModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class OrderDetailsFragment(details: String) : Fragment() {

    lateinit var binding: FragmentOrderDetailBinding
    private val orderDetails = details
    var orderId = "0"
    lateinit var deliveryLocation: String
    lateinit var deliveryId: String
    var orderModels: ArrayList<OrderModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        parseDetails()

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.btnCancelOrder.setOnClickListener {
            GeneralDialog().message("ایا از کنسل کردن سرویس اطمینان دارید؟").firstButton("بله") {
                cancelService(orderId)
            }
                .secondButton("خیر") {}.show()
        }

        binding.btnSetComplaint.setOnClickListener {
            RegisterComplaintDialog().show(orderId)
        }

        binding.btnDeliverLocation.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("deliveryLocation", deliveryLocation)
            bundle.putString("deliveryId", deliveryId)
            FragmentHelper.toFragment(MyApplication.currentActivity, DeliverLocationFragment())
                .setArguments(bundle).add()
        }

        binding.btnChangeAddress.setOnClickListener {
            EditAddressDialog().show(orderId, binding.txtAddress.text.toString())
        }

        return binding.root
    }

    private fun parseDetails() {
        try {
            val dataObj = JSONObject(orderDetails)
            val orderObj = dataObj.getJSONObject("order")
            deliveryId = orderObj.getString("deliveryId")
            deliveryLocation = dataObj.getString("deliveryLocation")

            val tax = dataObj.getString("tax")

            val productsArr = orderObj.getJSONArray("products")
            val cartModels: ArrayList<CartModel> = ArrayList()
            for (i in 0 until productsArr.length()) {
                val productObj = productsArr.getJSONObject(i)
                val cartModel = CartModel(
                    productObj.getJSONObject("_id").getString("_id"),
                    productObj.getInt("quantity"),
                    productObj.getString("price"),
                    productObj.getJSONObject("_id").getString("name")
                )
                cartModels.add(cartModel)
            }

            orderId = orderObj.getString("_id")
            binding.txtStatus.text = orderObj.getJSONObject("status").getString("name")
            binding.txtTime.text = (StringHelper.toPersianDigits(DateHelper.strPersianEghit(DateHelper.parseFormat(orderObj.getString("createdAt") + "", null))))
            binding.txtName.text = orderObj.getJSONObject("customer").getString("family")
            binding.txtMobile.text = orderObj.getJSONObject("customer").getString("mobile")
            binding.txtAddress.text = orderObj.getString("address")
            if (orderObj.getString("description").isEmpty()) {
                binding.llDesc.visibility = View.GONE
            } else {
                binding.txtDescription.text = orderObj.getString("description")
            }

            val cartAdapter = CartAdapter(cartModels)
            binding.orderList.adapter = cartAdapter

            var icon = R.drawable.ic_payment
            var color = R.color.payment_color
            when (orderObj.getJSONObject("status").getInt("status")) {
                6 -> {
                    icon = R.drawable.ic_payment
                    color = R.color.payment_color
                }
                0 -> {
                    icon = R.drawable.ic_waiting
                    color = R.color.waiting
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.black
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.black
                        )
                    )
                }
                2 -> {
                    icon = R.drawable.ic_chef
                    color = R.color.preparing
                }
                5 -> {
                    icon = R.drawable.ic_coooking
                    color = R.color.cooking
                }
                3 -> {
                    icon = R.drawable.ic_delivery
                    color = R.color.delivery
                }
                1 -> {
                    icon = R.drawable.ic_close
                    color = R.color.canceled
                }
                4 -> {
                    icon = R.drawable.ic_round_done_24
                    color = R.color.finished
                }
            }
            binding.imgStatus.setImageResource(icon)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val header =
                    AppCompatResources.getDrawable(
                        MyApplication.context,
                        R.drawable.bg_orders_header
                    )
                binding.llHeaderStatus.background = header
                DrawableCompat.setTint(
                    header!!,
                    MyApplication.currentActivity.resources.getColor(color)
                )
            } else {
                binding.llHeaderStatus.setBackgroundColor(
                    MyApplication.currentActivity.resources.getColor(
                        color
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelService(id: String) {
        binding.vfCancel.displayedChild = 1
        RequestHelper.builder(EndPoints.CANCEL_ORDER)
            .listener(cancelServiceCallBack)
            .addParam("orderId", id)
            .delete()
    }

    private val cancelServiceCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfCancel.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            if (dataObj.getBoolean("status")) {
                                GeneralDialog().message(message).firstButton("باشه") {}.show()
                            }
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfCancel.displayedChild = 0
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfCancel.displayedChild = 0
                }
            }
        }

}
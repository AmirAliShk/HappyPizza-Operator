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
import ir.food.operatorAndroid.adapter.OrdersAdapter
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrderDetailBinding
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.GeneralDialog
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.OrderModel
import org.json.JSONObject

class OrderDetailsFragment(model: OrderModel) : Fragment() {

    lateinit var binding: FragmentOrderDetailBinding
    val orderModel = model

    var orderModels: ArrayList<OrderModel> = ArrayList()
    var adapter: OrdersAdapter = OrdersAdapter(orderModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.btnCancelOrder.setOnClickListener {
            GeneralDialog().message("ایا از کنسل کردن سرویس اطمینان دارید؟").firstButton("بله") {}
                .secondButton("خیر") {}.show()
        }

        binding.btnSetComplaint.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.btnDeliverLocation.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, DeliverLocationFragment())
                .add()
        }

        binding.btnChangeAddress.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.txtStatus.text = orderModel.statusName
        binding.txtTime.text = StringHelper.toPersianDigits(orderModel.date)
        binding.txtName.text = orderModel.name
        binding.txtMobile.text = StringHelper.toPersianDigits(orderModel.mobile)
        binding.txtAddress.text = StringHelper.toPersianDigits(orderModel.address)

        var icon = R.drawable.ic_payment
        var color = R.color.payment_color
        when (orderModel.statusCode) {
            0 -> {
                icon = R.drawable.ic_payment
                color = R.color.payment_color
            }
            1 -> {
                icon = R.drawable.ic_waiting
                color = R.color.waiting
                binding.txtStatus.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                binding.txtTime.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
            }
            2 -> {
                icon = R.drawable.ic_chef
                color = R.color.preparing
            }
            3 -> {
                icon = R.drawable.ic_coooking
                color = R.color.cooking
            }
            4 -> {
                icon = R.drawable.ic_delivery
                color = R.color.delivery
            }
            5 -> {
                icon = R.drawable.ic_close
                color = R.color.canceled
            }
            6 -> {
                icon = R.drawable.ic_round_done_24
                color = R.color.finished
            }
        }
        binding.imgStatus.setImageResource(icon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val header =
                AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
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

        return binding.root
    }
}
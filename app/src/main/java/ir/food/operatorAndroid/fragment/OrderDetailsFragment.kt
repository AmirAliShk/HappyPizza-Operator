package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.CartAdapter
import ir.food.operatorAndroid.app.DataHolder
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrderDetailBinding
import ir.food.operatorAndroid.dialog.*
import ir.food.operatorAndroid.helper.DateHelper
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.ProductsModel
import ir.food.operatorAndroid.push.AvaCrashReporter
import org.json.JSONObject
import java.util.HashMap

class OrderDetailsFragment(details: String) : Fragment() {

    lateinit var binding: FragmentOrderDetailBinding
    private val orderDetails = details
    var orderId = "0"
    lateinit var deliveryId: String
    private lateinit var deliveryLocation: LatLng
    private lateinit var deliveryLastLocation: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        parseDetails()

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        binding.btnCancelOrder.setOnClickListener {
            CancelDialogOrder().show(orderId, object : CancelDialogOrder.CancelOrderDialog {
                override fun onSuccess(b: Boolean) {
                    if (b) {
                        GeneralDialog()
                            .message("سفارش با موفقیت لغو شد")
                            .firstButton("باشه") {
                                binding.scrol.scrollTo(0, 0)
                                binding.txtStatus.text = "لغو شده"
                                binding.llEditOrder.visibility = GONE
                                binding.imgStatus.setImageResource(R.drawable.ic_close)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    val header =
                                        AppCompatResources.getDrawable(
                                            MyApplication.context,
                                            R.drawable.bg_orders_header
                                        )
                                    binding.llHeaderStatus.background = header
                                    DrawableCompat.setTint(
                                        header!!,
                                        MyApplication.currentActivity.resources.getColor(R.color.canceled)
                                    )
                                } else {
                                    binding.llHeaderStatus.setBackgroundColor(
                                        MyApplication.currentActivity.resources.getColor(
                                            R.color.canceled
                                        )
                                    )
                                }
                            }
                            .cancelable(false)
                            .show()
                    } else {
                        GeneralDialog()
                            .message("مشکلی پیش آمده، لطفا مجدد امتحان کنید")
                            .secondButton("بستن") { GeneralDialog().dismiss() }
                            .cancelable(false)
                            .show()
                    }
                }
            })
        }

        binding.btnSetComplaint.setOnClickListener {
            RegisterComplaintDialog().show(orderId)
        }

        binding.btnDeliverLocation.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                DeliverLocationFragment(orderId, deliveryLocation, deliveryLastLocation)
            )
                .add()
        }

        binding.btnChangeAddress.setOnClickListener {
            EditAddressDialog().show(
                orderId,
                binding.txtAddress.text.toString(),
                object : EditAddressDialog.Listener {
                    override fun address(address: String?) {
                        binding.txtAddress.text = address
                    }
                })
        }

        binding.btnEditOrder.setOnClickListener {
            EditOrderDialog().show(
                JSONObject(orderDetails).getJSONObject("order").getJSONArray("products"),
                orderId,
                StringHelper.extractTheNumber(binding.txtSendPrice.text.toString())
            )
        }

        return binding.root
    }

    private fun parseDetails() {
        try {
            val dataObj = JSONObject(orderDetails)
            val orderObj = dataObj.getJSONObject("order")
            deliveryId = orderObj.getString("deliveryId")
            deliveryLastLocation = dataObj.getJSONObject("deliveryLocation").getString("date")
            deliveryLocation = LatLng(
                dataObj.getJSONObject("deliveryLocation").getDouble("lat"),
                dataObj.getJSONObject("deliveryLocation").getDouble("lng")
            )

            val cartMap: HashMap<String, ProductsModel> = HashMap()
            val productsArr = orderObj.getJSONArray("products")
            val supportCartModels: ArrayList<ProductsModel> = ArrayList()
            for (i in 0 until productsArr.length()) {
                val productObj = productsArr.getJSONObject(i)
                val cartModel = ProductsModel(
                    productObj.getString("id"),
                    productObj.getString("size"),
                    productObj.getString("name"),
                    "",
                    JSONObject(),
                    0,
                    productObj.getInt("quantity"),
                    productObj.getString("price"),
                    productObj.getString("discount")
                )
                supportCartModels.add(cartModel)
                cartMap[productObj.getString("id")] = cartModel
            }
            DataHolder.getInstance().customerCart = cartMap

            orderId = orderObj.getString("id")
            binding.txtStatus.text = orderObj.getJSONObject("status").getString("name")
            binding.txtTaxPrice.text =
                StringHelper.toPersianDigits(StringHelper.setComma(dataObj.getString("tax")))
            binding.txtDiscount.text =
                StringHelper.toPersianDigits(StringHelper.setComma(orderObj.getString("discount")))
            binding.txtTotalPrice.text =
                StringHelper.toPersianDigits(StringHelper.setComma(dataObj.getString("total")))
            binding.txtSendPrice.text =
                StringHelper.toPersianDigits(
                    StringHelper.setComma(
                        orderObj.getInt("deliveryCost").toString()
                    )
                )
            binding.txtTime.text = (StringHelper.toPersianDigits(
                DateHelper.strPersianEghit(
                    DateHelper.parseFormat(
                        orderObj.getString("createdAt") + "",
                        null
                    )
                )
            ))
            binding.txtName.text = orderObj.getJSONObject("customer").getString("family")
            binding.txtMobile.text = orderObj.getJSONObject("customer").getString("mobile")
            binding.txtAddress.text = orderObj.getString("address")
            binding.txtStationAddress.text = orderObj.getJSONObject("station").getString("code")

            binding.txtFinishTime.text = if (orderObj.has("finishDate")) {
                (StringHelper.toPersianDigits(
                    DateHelper.strPersianFour1(
                        DateHelper.parseFormat(
                            orderObj.getString("finishDate") + "",
                            null
                        )
                    )
                ))
            } else {
                ""
            }

            binding.txtBakeTime.text = if (orderObj.has("bakingDate")) {
                (StringHelper.toPersianDigits(
                    DateHelper.strPersianFour1(
                        DateHelper.parseFormat(
                            orderObj.getString("bakingDate") + "",
                            null
                        )
                    )
                ))
            } else {
                ""
            }

            binding.txtSendTime.text = if (orderObj.has("deliveryDate")) {
                (StringHelper.toPersianDigits(
                    DateHelper.strPersianFour1(
                        DateHelper.parseFormat(
                            orderObj.getString("deliveryDate") + "",
                            null
                        )
                    )
                ))
            } else {
                ""
            }

            if (orderObj.getBoolean("paid")) {
                binding.txtIsPaid.text = " شده "
                binding.txtIsPaid.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.color_green))
            } else {
                binding.txtIsPaid.text = " نشده "
                binding.txtIsPaid.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.color_Red))
            }

            when (orderObj.getInt("paymentType")) {
                0 -> binding.txtPaymentType.text = "حضوری"
                1 -> binding.txtPaymentType.text = "آنلاین"
            }

            when (orderObj.getInt("orderType")) {
                0 -> binding.txtOrderType.text = "تلفنی"
                1 -> binding.txtOrderType.text = "آنلاین"
            }

            if (orderObj.getString("description")
                    .isNotEmpty() && orderObj.getString("systemDescription").isNotEmpty()
            )
                binding.txtDescription.text =
                    orderObj.getString("description") + "\n" + orderObj.getString("systemDescription")
            else if (orderObj.getString("description")
                    .isNotEmpty() && orderObj.getString("systemDescription").isEmpty()
            )
                binding.txtDescription.text = orderObj.getString("description")
            else if (orderObj.getString("description")
                    .isEmpty() && orderObj.getString("systemDescription").isNotEmpty()
            )
                binding.txtDescription.text = orderObj.getString("systemDescription")

            if (orderObj.has("operator")) {
                binding.txtOperator.text = orderObj.getString("operator")
            }
            val cartAdapter = CartAdapter(supportCartModels)
            binding.orderList.adapter = cartAdapter

            var icon = R.drawable.ic_led_inprogress
            var color = R.color.darkGray
            binding.txtStatus.setTextColor(
                MyApplication.currentActivity.resources.getColor(
                    R.color.white
                )
            )
            binding.txtTime.setTextColor(
                MyApplication.currentActivity.resources.getColor(
                    R.color.white
                )
            )
//            if (!orderObj.getBoolean("paid")) {
//                binding.btnDeliverLocation.isEnabled = false
//                binding.btnSetComplaint.isEnabled = false
//                icon = R.drawable.ic_payment
//                color = R.color.payment_color
//            } else {
            when (orderObj.getJSONObject("status").getInt("status")) {
                0 -> {
                    binding.btnDeliverLocation.visibility = GONE
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
                5 -> {
                    binding.llEditOrder.visibility = GONE
                    binding.btnDeliverLocation.visibility = GONE
                    icon = R.drawable.ic_chef
                    color = R.color.preparing
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                2 -> {
                    binding.llEditOrder.visibility = GONE
                    binding.btnDeliverLocation.visibility = GONE
                    icon = R.drawable.ic_coooking
                    color = R.color.cooking
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                3 -> {
                    binding.llEditOrder.visibility = GONE
                    icon = R.drawable.ic_delivery
                    color = R.color.delivery
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                1 -> {
                    binding.llEditOrder.visibility = GONE
                    binding.vfCancel.visibility = GONE
                    binding.btnDeliverLocation.visibility = GONE
                    binding.btnChangeAddress.visibility = GONE
                    icon = R.drawable.ic_close
                    color = R.color.canceled
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                4 -> {
                    binding.llEditOrder.visibility = GONE
                    binding.btnDeliverLocation.visibility = GONE
                    binding.btnChangeAddress.visibility = GONE
                    icon = R.drawable.ic_round_done_24
                    color = R.color.finished
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                7 -> {
                    binding.llEditOrder.visibility = GONE
                    binding.vfCancel.visibility = GONE
                    binding.btnDeliverLocation.visibility = GONE
                    binding.btnChangeAddress.visibility = GONE
                    icon = R.drawable.ic_payment
                    color = R.color.settlement
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
                6 -> {
                    binding.btnDeliverLocation.visibility = GONE
                    icon = R.drawable.ic_refresh_white
                    color = R.color.payment_color
                    binding.txtStatus.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                    binding.txtTime.setTextColor(
                        MyApplication.currentActivity.resources.getColor(
                            R.color.white
                        )
                    )
                }
            }
//            }
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
            AvaCrashReporter.send(e, "OrderDetailsFragment class, parseDetails method")
        }
    }

}
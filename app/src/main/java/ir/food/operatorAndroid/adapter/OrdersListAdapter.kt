package ir.food.operatorAndroid.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemSearchBinding
import ir.food.operatorAndroid.fragment.OrderDetailsFragment
import ir.food.operatorAndroid.helper.*
import ir.food.operatorAndroid.model.OrderModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class OrdersListAdapter(list: ArrayList<OrderModel>) :
    RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {

    private val models = list
    lateinit var vfDetails: ViewFlipper

    class ViewHolder(val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSLight)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtStatus.text = model.statusName
        holder.binding.txtTime.text = (StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(model.date + "", null)
            )
        ))
        holder.binding.txtName.text = model.name
        holder.binding.txtMobile.text = StringHelper.toPersianDigits(model.mobile)
        holder.binding.txtAddress.text = StringHelper.toPersianDigits(model.address)

        var icon = R.drawable.ic_payment
        var color = R.color.payment_color
//        if (!model.paid) {
//            icon = R.drawable.ic_payment
//            color = R.color.payment_color
//        } else {
        when (model.statusCode) {
            0 -> {
                icon = R.drawable.ic_waiting
                color = R.color.waiting
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.black
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.black
                    )
                )
                holder.binding.vfLoader.setIndicatorColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.black
                    )
                )
            }
            5 -> {
                icon = R.drawable.ic_chef
                color = R.color.preparing
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            2 -> {
                icon = R.drawable.ic_coooking
                color = R.color.cooking
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            3 -> {
                icon = R.drawable.ic_delivery
                color = R.color.delivery
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            1 -> {
                icon = R.drawable.ic_close
                color = R.color.canceled
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            4 -> {
                icon = R.drawable.ic_round_done_24
                color = R.color.finished
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
        }
//        }

        holder.binding.imgStatus.setImageResource(icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val header =
                AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
            holder.binding.llHeaderStatus.background = header
            DrawableCompat.setTint(
                header!!,
                MyApplication.currentActivity.resources.getColor(color)
            )
        } else {
            holder.binding.llHeaderStatus.setBackgroundColor(
                MyApplication.currentActivity.resources.getColor(
                    color
                )
            )
        }

        holder.itemView.setOnClickListener {
            this.vfDetails = holder.binding.vfDetails
            getOrderDetails(model.id)
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun getOrderDetails(orderId: String) {
        this.vfDetails.displayedChild = 1
        RequestHelper.builder(EndPoints.GET_ORDER_DETAILS)
            .addPath(orderId)
            .listener(callBack)
            .get()
    }

    private val callBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        vfDetails.displayedChild = 0
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataObj = jsonObject.getJSONObject("data")
                            KeyBoardHelper.hideKeyboard()
                            FragmentHelper
                                .toFragment(
                                    MyApplication.currentActivity,
                                    OrderDetailsFragment(dataObj.toString())
                                )
                                .add()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        vfDetails.displayedChild = 0
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    vfDetails.displayedChild = 0
                }
            }
        }
}
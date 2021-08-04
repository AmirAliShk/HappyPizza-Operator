package ir.food.operatorAndroid.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemSearchBinding
import ir.food.operatorAndroid.fragment.OrderDetailsFragment
import ir.food.operatorAndroid.fragment.OrdersListFragment
import ir.food.operatorAndroid.helper.FragmentHelper
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.OrderModel


class OrdersAdapter(list: ArrayList<OrderModel>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private val models = list

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
        holder.binding.txtTime.text = StringHelper.toPersianDigits(model.date)
        holder.binding.txtName.text = model.name
        holder.binding.txtMobile.text = StringHelper.toPersianDigits(model.mobile)
        holder.binding.txtAddress.text = StringHelper.toPersianDigits(model.address)

        var icon = R.drawable.ic_payment
        var color = R.color.payment_color
        when (model.statusCode) {
            0 -> {
                icon = R.drawable.ic_payment
                color = R.color.payment_color
            }
            1 -> {
                icon = R.drawable.ic_waiting
                color = R.color.waiting
                holder.binding.txtStatus.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
                holder.binding.txtTime.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
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
        holder.binding.imgStatus.setImageResource(icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val header = AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
            holder.binding.llHeaderStatus.background = header
            DrawableCompat.setTint(header!!, MyApplication.currentActivity.resources.getColor(color))
        } else {
            holder.binding.llHeaderStatus.setBackgroundColor(MyApplication.currentActivity.resources.getColor(color))
        }

        holder.itemView.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, OrderDetailsFragment(model))
                .add()
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }
}
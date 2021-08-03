package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemSearchBinding
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
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtStatus.text = model.statusName
        holder.binding.txtTime.text = model.date
        holder.binding.txtName.text = model.name
        holder.binding.txtMobile.text = model.mobile
        holder.binding.txtAddress.text = model.address

        when (model.statusCode) {
            0 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_payment)
            1 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_waiting)
            2 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_chef)
            3 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_coooking)
            4 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_delivery)
            5 -> holder.binding.imgStatus.setImageResource(R.drawable.ic_close)
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}
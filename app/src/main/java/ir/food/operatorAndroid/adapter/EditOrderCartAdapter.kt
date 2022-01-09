package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemOrderBinding
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.EditOrderModel

class EditOrderCartAdapter(list: ArrayList<EditOrderModel>, var totalPrice: TotalPrice) :
    RecyclerView.Adapter<EditOrderCartAdapter.ViewHolder>() {
    private val models = list

    interface TotalPrice {
        fun collectTotalPrice(s: Int)
    }

    class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

//        if (model.contains(model)) {
//            quantity++
//        }
        holder.binding.txtQuantity.text = StringHelper.toPersianDigits(model.quantity.toString())
        holder.binding.txtName.text = model.name

//        if (model.size[0].) {
//            holder.binding.txtDiscount.visibility = View.VISIBLE
//        } else {
//            holder.binding.txtDiscount.visibility = View.INVISIBLE
//        }

        holder.binding.imgReduce.setOnClickListener {
            if (model.quantity == 1) models.removeAt(position)
            else model.quantity--
            totalPrice.collectTotalPrice(models.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}
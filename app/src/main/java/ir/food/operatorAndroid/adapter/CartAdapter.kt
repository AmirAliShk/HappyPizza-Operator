package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemRegisterOrderBinding
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.CartModel

class CartAdapter(list: ArrayList<CartModel>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private val models = list
    var sum = 1
    var temp: Int = 0

    class ViewHolder(val binding: ItemRegisterOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRegisterOrderBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        if (models.contains(model)) {
            temp = sum++
        }
        holder.binding.txtName.text = model.product?.name
        holder.binding.txtPrice.text = (model.product?.sellingPrice)
        holder.binding.txtQuantity.text = StringHelper.toPersianDigits(temp.toString())//todo

        holder.binding.imgRemove.setOnClickListener {
            models.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}
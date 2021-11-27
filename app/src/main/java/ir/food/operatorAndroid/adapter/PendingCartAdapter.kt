package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemOrderBinding
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.PendingCartModel
import ir.food.operatorAndroid.model.ProductsModel

class PendingCartAdapter(list: ArrayList<PendingCartModel>, id: String) :
    RecyclerView.Adapter<PendingCartAdapter.ViewHolder>() {
    private val models = list

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

        holder.binding.txtQuantity.text = "1"
        holder.binding.txtName.text = model.name
        holder.binding.txtPrice.text =
            StringHelper.toPersianDigits(StringHelper.setComma(model.price))

//        if (model.size[0].) {
//            holder.binding.txtDiscount.visibility = View.VISIBLE
//        } else {
//            holder.binding.txtDiscount.visibility = View.INVISIBLE
//        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}
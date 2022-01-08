package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemMenuBinding
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.ProductsModel

class MenuListAdapter(list: ArrayList<ProductsModel>) :
    RecyclerView.Adapter<MenuListAdapter.ViewHolder>() {
    private val models = list

    class ViewHolder(val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)
        TypefaceUtil.overrideFonts(binding.txtPName, MyApplication.IraSanSMedume)
        TypefaceUtil.overrideFonts(binding.txtPrice, MyApplication.IraSanSMedume)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtPName.text = model.name
        holder.binding.txtIngredients.text = model.description
        holder.binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.price))

    }

    override fun getItemCount(): Int {
        return models.size
    }
}
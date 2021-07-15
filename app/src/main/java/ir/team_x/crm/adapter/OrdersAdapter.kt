package ir.team_x.crm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ItemOrdersBinding
import ir.team_x.crm.databinding.ItemRegisterOrderBinding
import ir.team_x.crm.helper.StringHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.model.OrdersModel

class OrdersAdapter(list: ArrayList<OrdersModel>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {
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
        holder.binding.txtName.text = model.name
        holder.binding.txtPrice.text = (model.sellingPrice)
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
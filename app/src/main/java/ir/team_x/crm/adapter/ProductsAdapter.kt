package ir.team_x.crm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ItemProductsBinding
import ir.team_x.crm.dialog.ProductDialog
import ir.team_x.crm.fragment.ProductsFragment
import ir.team_x.crm.helper.DateHelper
import ir.team_x.crm.helper.StringHelper
import ir.team_x.crm.helper.TypefaceUtil
import ir.team_x.crm.model.ProductsModel

class ProductsAdapter(list: ArrayList<ProductsModel>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemProductsBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtName.text = model.name
        holder.binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.sellingPrice)) + " تومان"
        holder.binding.txtEditTime.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToString(model.updatedAt))
        holder.binding.imgEdit.setOnClickListener {
            ProductDialog().show(model, "editProduct", object :
                ProductDialog.Refresh {
                override fun refresh(refresh: Boolean) {
                   notifyDataSetChanged()
                }

            })
        }
        if (model.description.isEmpty()) {
            holder.binding.llDescription.visibility = ViewGroup.INVISIBLE
        } else {
            holder.binding.txtDescription.text = model.description
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}
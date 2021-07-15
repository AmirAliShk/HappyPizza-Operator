package ir.team_x.crm.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.crm.databinding.ItemOrdersBinding
import ir.team_x.crm.model.OrdersModel

class OrdersAdapter(list: ArrayList<OrdersModel>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>(){

    private val models = list

     class ViewHolder(val binding: ItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: OrdersAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}
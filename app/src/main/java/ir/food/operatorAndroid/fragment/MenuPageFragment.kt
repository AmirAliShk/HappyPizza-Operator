package ir.food.operatorAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuAdapter
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.adapter.MenuListAdapter
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentMenuBinding
import ir.food.operatorAndroid.databinding.FragmentMenuPageBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.PendingCartModel
import ir.food.operatorAndroid.model.ProductsModel
import org.json.JSONArray
import java.text.FieldPosition

class MenuPageFragment(position: Int) : Fragment() {
    lateinit var binding: FragmentMenuPageBinding
    val pos = position
    lateinit var productsModels: ArrayList<ProductsModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuPageBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        return binding.root
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        if (menuVisible) {
            productsModels = ArrayList()
            val productsArr = JSONArray(MyApplication.prefManager.productsList)
            val typesArr = JSONArray(MyApplication.prefManager.productsTypeList)

            for (i in 0 until productsArr.length()) {
                if (typesArr.getJSONObject(pos).getString("_id") == productsArr.getJSONObject(i)
                        .getJSONObject("type").getString("_id")
                ) {
                    val products = ProductsModel(
                        productsArr.getJSONObject(i).getString("_id"),
                        productsArr.getJSONObject(i).getJSONArray("size"),
                        productsArr.getJSONObject(i).getString("name"),
                        productsArr.getJSONObject(i).getString("description"),
                        productsArr.getJSONObject(i).getJSONObject("type"),
                        productsArr.getJSONObject(i).getInt("supply"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0).getString("discount"),
                        productsArr.getJSONObject(i).getJSONArray("size").getJSONObject(0).getString("price")
                    )
                    productsModels.add(products)
                    val adapter = MenuListAdapter(productsModels)
                    binding.listMenu.adapter = adapter
                }
            }
        }
    }
}
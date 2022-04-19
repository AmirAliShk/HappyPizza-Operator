package ir.food.operatorAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.food.operatorAndroid.adapter.MenuViewPagerAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentMenuBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class MenuFragment(station: String) : Fragment() {
    lateinit var binding: FragmentMenuBinding
    lateinit var adapter: MenuViewPagerAdapter
    val station = station
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        adapter = MenuViewPagerAdapter(this)

        getProductsAndLists()

        binding.imgClose.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }

    private fun getProductsAndLists() {
        binding.vfMenu.displayedChild = 0
        RequestHelper.builder(EndPoints.GET_PRODUCTS)
            .addPath(station)
            .listener(productsCallBack)
            .get()
    }

    private val productsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfMenu.displayedChild = 1
//{"success":true,"message":"محصولات سفارش با موفقیت ارسال شد","data":{"products":[{"_id":"61091b0ca9335b389819e894","size":[{"name":"medium","price":"75000","discount":"15000"}],"name":"رست بیف","description":"گوشت گوساله . پنیر . قارچ . فلفل دلمه ای . پیازجه","type":{"_id":"610916826f9446153c5e268d","name":"پیتزا"}}],"types":[{"_id":"610916826f9446153c5e268d","name":"پیتزا"}],"status":true}}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val data = jsonObject.getJSONObject("data")
                        val status = data.getBoolean("status")
                        if (status) {
                            MyApplication.prefManager.kitchenList = data.toString()

                            binding.menuVPager.adapter = adapter

                            TabLayoutMediator(
                                binding.tabLayout,
                                binding.menuVPager
                            ) { tab: TabLayout.Tab, position: Int ->
                                tab.customView = adapter.getTabView(position)
                            }.attach()

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfMenu.displayedChild = 2
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            super.onFailure(reCall, e)
            MyApplication.handler.post {
                binding.vfMenu.displayedChild = 2
            }
        }
    }

}
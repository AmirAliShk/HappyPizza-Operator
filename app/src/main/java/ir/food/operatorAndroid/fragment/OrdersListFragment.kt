package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.OrdersAdapter
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.OrderModel
import org.json.JSONObject

class OrdersListFragment : Fragment() {

    lateinit var binding: FragmentOrdersListBinding

    var orderModels: ArrayList<OrderModel> = ArrayList()
    var adapter: OrdersAdapter = OrdersAdapter(orderModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersListBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        val data =
            "{\"orders\":[{\"status\":{\"name\":\"در حال پخت\",\"code\":2},\"date\":\"3/2\",\"name\":\"رضایی\",\"mobile\":\"093454400369\",\"address\":\"کوهسنگی\"},{\"status\":{\"name\":\"در صف پخت\",\"code\":1},\"date\":\"14/5\",\"name\":\"احمدی\",\"mobile\":\"093454400369\",\"address\":\"احمدآباد\"},{\"status\":{\"name\":\"در حال ارسال\",\"code\":3},\"date\":\"3/2\", \"name\":\"کریمی\" , \"mobile\" : \"093454400369\", \"address\" : \"تقی آباد\"},{\"status\":{\"name\" : \"در حال پخت\", \"code\" : 2},\"date\":\"3/2\", \"name\":\"رضایی\" , \"mobile\" : \"093454400369\", \"address\" : \"کوهسنگی\"}]}"
        val dataObject = JSONObject(data)
        val active = dataObject.getJSONArray("orders")
        for (i in 0 until active.length()) {
            val dataObj: JSONObject = active.getJSONObject(i)
            val status = dataObj.getJSONObject("status")

            var model = OrderModel(
                status.getString("name"),
                status.getInt("code"),
                dataObj.getString("date"),
                dataObj.getString("name"),
                dataObj.getString("mobile"),
                dataObj.getString("address")
            )

            orderModels.add(model)
        }
        binding.searchList.adapter = adapter

        binding.imgSearchType.setOnClickListener {
            SearchDialog().show(object : SearchDialog.SearchListener {
                override fun searchType(searchType: Int) {
                    when (searchType) {
                        1 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_user)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                        }
                        2 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_phone)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_NUMBER
                        }
                        3 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_gps)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                        }
                    }
                }
            })
        }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }
}
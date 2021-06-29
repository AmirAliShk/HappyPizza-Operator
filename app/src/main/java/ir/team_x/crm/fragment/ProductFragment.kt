package ir.team_x.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentProductBinding
import ir.team_x.crm.model.ProductsModel
import ir.team_x.crm.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class ProductFragment : Fragment() {

    lateinit var binding: FragmentProductBinding
    lateinit var productsModel: ArrayList<ProductsModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)

        getProducts()

        return binding.root
    }

    private fun getProducts() {
        if (binding.vfProduct != null) {
            binding.vfProduct.displayedChild = 0
        }

        RequestHelper.builder(EndPoints.GET_PRODUCT)
            .listener(productCallBack)
            .get()
    }

    private val productCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        productsModel = ArrayList()
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")

                        if (success) {
                            val dataObject = response.getJSONArray("data")
                            for (i in 0 until dataObject.length()) {
                                val dataObj: JSONObject = dataObject.getJSONObject(i);
                                var model= ProductsModel(
                                 dataObj.getString("name")
                                )
                                productsModel.add(model)
                            }

                            if (productsModel.size == 0) {
                                if (binding.vfProduct != null) {
                                    binding.vfProduct.displayedChild = 2
                                }
                            }
                        }
                    }catch (e: JSONException){
                         e.printStackTrace()
                    }
                }

            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                super.onFailure(reCall, e)
            }
        }

}
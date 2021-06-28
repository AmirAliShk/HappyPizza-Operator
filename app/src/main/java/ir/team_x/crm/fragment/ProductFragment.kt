package ir.team_x.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.app.EndPoints
import ir.team_x.crm.databinding.FragmentProductBinding
import ir.team_x.crm.okHttp.RequestHelper
import java.lang.Exception

class ProductFragment : Fragment() {

    lateinit var binding: FragmentProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                TODO("Not yet implemented")
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                super.onFailure(reCall, e)
            }
        }

}
package ir.food.operatorAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentDeliverLocationBinding
import ir.food.operatorAndroid.helper.TypefaceUtil

class DeliverLocationFragment : Fragment() {

    lateinit var binding: FragmentDeliverLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDeliverLocationBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }

}
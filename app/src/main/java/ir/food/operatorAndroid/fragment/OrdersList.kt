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
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.TypefaceUtil

class OrdersList : Fragment() {

    lateinit var binding: FragmentOrdersListBinding

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
package ir.food.operatorAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import ir.food.operatorAndroid.adapter.MenuViewPagerAdapter
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentMenuBinding
import ir.food.operatorAndroid.helper.TypefaceUtil
import org.json.JSONArray

class MenuFragment : Fragment() {
    lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        val adapter = MenuViewPagerAdapter(this)
        binding.menuVPager.adapter = adapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.menuVPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.customView = adapter.getTabView(position)
        }.attach()

        binding.imgClose.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }
}
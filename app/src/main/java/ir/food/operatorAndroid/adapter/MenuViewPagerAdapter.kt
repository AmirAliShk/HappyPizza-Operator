package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.fragment.MenuPageFragment
import ir.food.operatorAndroid.helper.TypefaceUtil
import org.json.JSONArray

class MenuViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return JSONArray(MyApplication.prefManager.productsTypeList).length()
    }

    override fun createFragment(position: Int): Fragment {
        return MenuPageFragment(position)
    }

    fun getTabView(position: Int): View {
        val v: View =
            LayoutInflater.from(MyApplication.currentActivity).inflate(R.layout.item_tab, null)
        val txtTabTitle = v.findViewById<TextView>(R.id.txtTabTitle)
        TypefaceUtil.overrideFonts(v)
        val typesArr = JSONArray(MyApplication.prefManager.productsTypeList)

        for (i in 0 until typesArr.length()) {
            if (position == i)
                txtTabTitle.text = typesArr.getJSONObject(i).getString("name")
        }

        return v
    }

}
package ir.team_x.crm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import ir.team_x.crm.R
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivityMainBinding
import ir.team_x.crm.fragment.RegisterOrderFragment
import ir.team_x.crm.fragment.SignInFragment
import ir.team_x.crm.helper.FragmentHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imgMenu.setOnClickListener { binding.draw.openDrawer(Gravity.RIGHT) }

        binding.registerOrder.setOnClickListener {
            FragmentHelper
                .toFragment(this, RegisterOrderFragment())
                .setAddToBackStack(false)
                .add()
        }
    }
    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }
}
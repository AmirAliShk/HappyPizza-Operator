package ir.team_x.crm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import ir.team_x.crm.R
import ir.team_x.crm.databinding.ActivityMainBinding
import ir.team_x.crm.fragment.SignInFragment
import ir.team_x.crm.helper.FragmentHelper

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imgMenu.setOnClickListener({ binding.draw.openDrawer(Gravity.RIGHT) })
    }
}
package ir.team_x.crm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivitySplashBinding
import ir.team_x.crm.fragment.SignInFragment
import ir.team_x.crm.helper.FragmentHelper

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        MyApplication.handler.postDelayed(
            {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, SignInFragment())
                    .setAddToBackStack(false)
                    .add()
            }, 1000
        )
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
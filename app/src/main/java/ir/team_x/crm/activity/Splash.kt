package ir.team_x.crm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.R
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivitySplashBinding
import ir.team_x.crm.fragment.SignInFragment

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        MyApplication.handler.postDelayed(
            {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragmentOne = SignInFragment()
                fragmentTransaction.add(
                    R.id.edtEmail,
                    fragmentOne,
                    "Fragment One"
                )
                fragmentTransaction.commit()
            }, 1000
        )
    }
}
package ir.team_x.crm.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        MyApplication.handler.postDelayed(
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("key", "start")
                startActivity(intent)
            }, 300
        )
    }
}
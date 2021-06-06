package ir.team_x.crm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.team_x.crm.R
import ir.team_x.crm.fragment.SignInFragment
import ir.team_x.crm.helper.FragmentHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mainFragment = SignInFragment()
        supportFragmentManager.beginTransaction().add(R.id.container, mainFragment)
            .commit()
//        FragmentHelper.toFragment(MainActivity,SignInFragment)
    }
}
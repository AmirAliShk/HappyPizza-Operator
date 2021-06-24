package ir.team_x.crm.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.activity.MainActivity
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentLogInBinding
import ir.team_x.crm.helper.FragmentHelper

class LogInFragment : Fragment() {

    lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)

        binding.btnLogIn.setOnClickListener {
            MyApplication.handler.postDelayed({
                MyApplication.currentActivity.startActivity(
                    Intent(
                        MyApplication.currentActivity,
                        MainActivity::class.java
                    )
                )
                MyApplication.currentActivity.finish()
            }, 200)
        }

        binding.txtSignUp.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, SignInFragment())
                .setAddToBackStack(false)
                .add()
        }

        return binding.root
    }


}
package ir.team_x.crm.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.activity.MainActivity
import ir.team_x.crm.app.MyApplication
import ir.team_x.crm.databinding.FragmentSignUpBinding
import ir.team_x.crm.helper.FragmentHelper

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        val name = binding.edtName.text.toString()
        val mobile = binding.edtMobile.text.toString()
        val email = binding.edtEmail.text.toString()
        val companyName = binding.edtCompanyName.text.toString()
        val password = binding.edtPassword.text.toString()

        binding.btnSignIn.setOnClickListener {
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
        binding.txtLogIn.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, LogInFragment())
                .setAddToBackStack(false)
                .add()
        }
        return binding.root
    }
}
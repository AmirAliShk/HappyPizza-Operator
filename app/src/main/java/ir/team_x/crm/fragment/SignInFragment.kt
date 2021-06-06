package ir.team_x.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.databinding.FragmentSignInBinding
import ir.team_x.crm.helper.ContinueProcessing

class SignInFragment : Fragment() {

    lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)

        val name = binding.edtName.text.toString()
        val mobile = binding.edtMobile.text.toString()
        val email = binding.edtEmail.text.toString()
        val companyName = binding.edtCompanyName.text.toString()
        val password = binding.edtPassword.text.toString()

        binding.btnSignIn.setOnClickListener { ContinueProcessing.runMainActivity() }

        return binding.root
    }
}
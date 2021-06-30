package ir.team_x.crm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.crm.databinding.FragmentRegisterOrderBinding
import ir.team_x.crm.helper.TypefaceUtil

class RegisterOrderFragment : Fragment() {

    lateinit var binding: FragmentRegisterOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        TypefaceUtil.overrideFonts(binding.root)

        binding = FragmentRegisterOrderBinding.inflate(layoutInflater)

        return binding.root
    }
}
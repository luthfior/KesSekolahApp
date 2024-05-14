package com.example.kessekolah.ui.onBoarding

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentOnBoardingBinding
import com.example.kessekolah.utils.LoginPreference
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref: LoginPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()

        setStatusBarTextColorGray()
        setStatusBarBackgroundColorWhite()

        pref = LoginPreference(requireContext())

        binding.viewPager.adapter = OnBoardingFragmentAdapter(requireActivity(), requireContext())
        TabLayoutMediator(binding.pageIndicator, binding.viewPager) { _, _ ->}.attach()
        binding.viewPager.offscreenPageLimit = 1
    }

    private fun buttonClick() {
        binding.btnMasuk.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }

        binding.btnDaftar.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_signUpFragment)
        }
//        binding.btnTamu.setOnClickListener {
////            val token = generateToken(15)
////            val data = LoginData(token, "Tamu", "Tamu", " ", " ")
////            pref = LoginPreference(requireContext())
////            pref.saveData(data)
//            findNavController().navigate(R.id.action_onBoardingFragment_to_pilihWilayahFragment)
//        }
    }

    private fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun setStatusBarTextColorGray() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = activity?.window?.decorView
            decor?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setStatusBarBackgroundColorWhite() {
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
    }
    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


}
package com.example.kessekolah.ui.core.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.MainActivity
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentProfileBinding
import com.example.kessekolah.utils.LoginPreference


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preference: LoginPreference
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = LoginPreference(requireContext())
        dataLogin = preference.getData()

        setupData()
        buttonClick()
    }

    private fun setupData() = with(binding) {
        tvName.text = dataLogin.name
        tvEmail.text = dataLogin.email
        tvProfesi.text = dataLogin.role
    }


    private fun buttonClick() = with(binding) {
        btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        btnTentangSekolah.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_tentangSekolahFragment)
        }


        btnKeluar.setOnClickListener {

            //add dialog to make sure action log out
            preference.removeData()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            requireActivity().finish()
        }
    }


}
package com.example.kessekolah.ui.onBoarding

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentOnBoardingBinding
import com.example.kessekolah.utils.LoginPreference

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var pref: LoginPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarTextColorGray()
        setStatusBarBackgroundColorWhite()

        pref = LoginPreference(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Internet Permission Required")
            .setMessage("This app requires permission to use the internet. Please allow internet access to continue.")
            .setPositiveButton("Allow") { _, _ ->
                checkInternetPermission()
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkInternetPermission() {
        if (!isInternetAvailable(requireContext())) {
            showInternetPermissionDialog()
        } else {
            buttonClick()
        }
    }

    private fun showInternetPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Internet Permission Required")
            .setMessage("This app requires an active internet connection. Please enable mobile data or Wi-Fi.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                buttonClick()
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun buttonClick() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }
    }


    private fun setStatusBarTextColorGray() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = activity?.window?.decorView
            @Suppress("DEPRECATION")
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
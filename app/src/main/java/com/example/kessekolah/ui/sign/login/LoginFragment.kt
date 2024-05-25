package com.example.kessekolah.ui.sign.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentLoginBinding
import com.example.kessekolah.model.LoginViewModel
import com.example.kessekolah.viewModel.ViewModelFactorySign
import com.example.kessekolah.utils.LoginPreference
import kotlin.properties.Delegates

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pref: LoginPreference

    private var usernameErrorData by Delegates.notNull<Boolean>()
    private var passwordErrorData by Delegates.notNull<Boolean>()

    init {
        usernameErrorData = true
        passwordErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = ViewModelFactorySign.getInstance(requireActivity().application)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[LoginViewModel::class.java]

        pref = LoginPreference(requireContext())

//        binding.btnTamu.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_loginFragment_to_pilihWilayahFragment
//            )
//        }
//
//        binding.btnLoginPhone.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_loginFragment_to_loginWithPhoneFragment
//            )
//        }

        binding.btnDaftar.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signUpFragment
            )
        }
        textListener()
        buttonCLick()
    }

    private fun textListener() {
        with(binding) {
            textUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    usernameErrorData = textUsername.text.toString().trim().isEmpty()
                    if (textUsername.toString().trim().isNullOrEmpty()) {
                        binding.usernameError.alpha = 1F
                    } else {
                        binding.usernameError.alpha = 0F
                    }
                    if (s?.contains(" ") == true) {
                        binding.usernameError.text = getString(R.string.username_space)
                        binding.usernameError.alpha = 1F
                    } else {
                        binding.usernameError.alpha = 0F
                    }
                    btnLogin.isEnabled = !(usernameErrorData || passwordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            textPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    passwordErrorData = s.toString().length < 6 || s.toString().trim().isEmpty()
                    if (passwordErrorData) {
                        binding.passwordError.alpha = 1F
                    } else {
                        binding.passwordError.alpha = 0F
                    }
                    btnLogin.isEnabled = !(usernameErrorData || passwordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }

    }

    private fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun buttonCLick() {
        binding.btnLogin.setOnClickListener {
            val username = binding.textUsername.text.toString().trim()
            val password = binding.textPassword.text.toString().trim()

            loginViewModel.loginUser(username, username, password) { isLoginSuccess ->
                if (isLoginSuccess) {
                    val token = generateToken(15)
                    val userLiveData = loginViewModel.getUserByUsernameAndEmail(username, username)
                    userLiveData.observe(viewLifecycleOwner) { user ->
                        val data = LoginData(token, user?.username, user?.name, user?.email, user?.phoneNumber)
                        if (user != null) {
                            val bundle = Bundle().apply {
                                putString("TOKEN", token)
                                putString("USERNAME", username)
                                putString("NAMA", user?.name)
                                putString("EMAIL", username)
                            }
                            pref.saveData(data)
                            findNavController().navigate(
                                R.id.action_loginFragment_to_homeActivity,
                                bundle
                            )
                            requireActivity().finish()
                        } else {
                            Log.d("LoginFragment", "Gadapet data dari database")
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Login gagal. Akun tidak ditemukan, silahkan Buat Akun terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = childFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout, fragment)
//        fragmentTransaction.commit()
//    }

}
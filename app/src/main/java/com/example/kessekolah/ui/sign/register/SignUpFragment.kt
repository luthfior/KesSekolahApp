package com.example.kessekolah.ui.sign.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.response.SignUpResponse
import com.example.kessekolah.databinding.FragmentSignUpBinding
import com.example.kessekolah.model.SignUpViewModel
import com.example.kessekolah.viewModel.ViewModelFactorySign
import kotlin.properties.Delegates


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var signUpViewModel: SignUpViewModel

    private var phoneNumberErrorData by Delegates.notNull<Boolean>()
    private var usernameErrorData by Delegates.notNull<Boolean>()
    private var passwordErrorData by Delegates.notNull<Boolean>()
    private var confirmPasswordErrorData by Delegates.notNull<Boolean>()

    init {
        phoneNumberErrorData = true
        usernameErrorData = true
        passwordErrorData = true
        confirmPasswordErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textCodePhone.isEnabled = false
        binding.layoutCodePhone.isEnabled = false

        val vmFactory = ViewModelFactorySign.getInstance(requireActivity().application)
        signUpViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[SignUpViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

//        observeData()
        textListener()
        buttonCLick()
//        user = arguments?.getParcelable(NEW_USER)

        binding.btnLoginPage.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_loginFragment
            )
        }

//        binding.btnTamu.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_signUpFragment_to_pilihWilayahActivity
//            )
//        }

    }

    private fun textListener() {
        with(binding) {
            textPhoneNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    phoneNumberErrorData = s.toString().trim().length >= 13  || textPhoneNumber.text.toString().trim().isEmpty() || !textPhoneNumber.text.toString().trim().startsWith("8")
                    if (textPhoneNumber.toString().trim().isNullOrEmpty()) {
                        binding.phoneNumberEmpty.alpha = 1F
                    } else {
                        binding.phoneNumberEmpty.alpha = 0F
                    }
                    if (textPhoneNumber.text.toString().trim().length >= 13 || !textPhoneNumber.text.toString().trim().startsWith("8")) {
                        binding.phoneNumberWrong.alpha = 1F
                    } else {
                        binding.phoneNumberWrong.alpha = 0F
                    }
                    btnSignUp.isEnabled = !(phoneNumberErrorData || usernameErrorData || passwordErrorData || confirmPasswordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

//            textPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
//                if (!hasFocus) {
//                    val phoneNumber = textPhoneNumber.text.toString().trim()
//                    if (!phoneNumber.startsWith("8")) {
//                        binding.phoneNumberWrong.alpha = 1F
//                    } else {
//                        binding.phoneNumberWrong.alpha = 0F
//                    }
//                }
//            }

            textUsername.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    usernameErrorData = s.toString().trim().isEmpty()
                    if (usernameErrorData) {
                        usernameEmpty.alpha = 1F
                    } else {
                        usernameEmpty.alpha = 0F
                    }
                    if (s?.contains(" ") == true) {
                        usernameWithSpace.alpha = 1F
                    } else {
                        usernameWithSpace.alpha = 0F
                    }
                    btnSignUp.isEnabled = !(phoneNumberErrorData || usernameErrorData || passwordErrorData || confirmPasswordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
//                    btnSignUp.isEnabled = false
                }

            })

            textPassword.addTextChangedListener(object: TextWatcher{
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
                    btnSignUp.isEnabled = !(phoneNumberErrorData || usernameErrorData || passwordErrorData || confirmPasswordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
//                    btnSignUp.isEnabled = false
                }

            })

            textConfirmPassword.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    confirmPasswordErrorData = s.toString().trim() != textPassword.text.toString().trim() || textConfirmPassword.text.toString().trim().isEmpty()
                    if (confirmPasswordErrorData) {
                        binding.confirmPasswordError.alpha = 1F
                    } else {
                        binding.confirmPasswordError.alpha = 0F
                    }
                    btnSignUp.isEnabled = !(phoneNumberErrorData || usernameErrorData || passwordErrorData || confirmPasswordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
                    // btnSignUp.isEnabled = !(passwordErrorData && confirmPasswordErrorData)
                }

            })
        }

    }

    private fun buttonCLick() {
        with(binding) {

            btnSignUp.setOnClickListener {
                var phoneNumber = "0" + textPhoneNumber.text.toString().trim()
                var username = textUsername.text.toString().trim()
                var password = textPassword.text.toString().trim()

                signUpViewModel.checkPhoneNumberExists(phoneNumber) { isPhoneNumberExists ->
                    if (isPhoneNumberExists) {
                        binding.phoneNumberError.alpha = 1F
                        btnSignUp.post {
                            btnSignUp.isEnabled = false
                        }
                    } else {
                        signUpViewModel.checkUsernameExists(username) { isUsernameExists ->
                            if (isUsernameExists) {
                                binding.usernameError.alpha = 1F
                                btnSignUp.post {
                                    btnSignUp.isEnabled = false
                                }
                            } else {
//                                user = User(0, phoneNumber, username, email, password)
//                                user?.let { user ->
//                                    signUpViewModel.insertProduk(user)
//                                }
                                val bundle = Bundle().apply {
                                    putString("PHONE_NUMBER", phoneNumber)
                                    putString("USERNAME", username)
                                    putString("PASSWORD", password)
                                }
//                                requireActivity().runOnUiThread {
//                                    findNavController().navigate(R.id.action_signUpFragment_to_otpActivity, bundle)
//                                    requireActivity().finish()
//                                }
                            }
                        }
                    }
                }
            }

            btnLoginPage.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

//            btnTamu.setOnClickListener {
//                findNavController().navigate(R.id.action_loginFragment_to_pilihWilayahFragment)
//            }
        }
    }

//    private fun succesHandler(data: SignUpResponse) {
//
//        Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
//        findNavController().navigate(R.id.action_signUpFragment_to_otpActivity)
//        requireActivity().finish()
//
//        binding.apply {
//            textPhoneNumber.text = null
//            textUsername.text = null
//            textPassword.text = null
//            textConfirmPassword.text = null
//        }
//    }

    companion object {
        const val NEW_USER = "new_user"
    }

}
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
import com.example.kessekolah.data.repo.AuthRepository
import com.example.kessekolah.data.response.ResponseMessage
import com.example.kessekolah.databinding.FragmentLoginBinding
import com.example.kessekolah.model.LoginViewModel
import com.example.kessekolah.viewModel.ViewModelFactorySign
import com.example.kessekolah.utils.LoginPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern
import kotlin.properties.Delegates


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null


    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pref: LoginPreference
    private lateinit var getUserEmail: String
    private lateinit var getUserName: String
    private lateinit var getUserRole: String
    private lateinit var getUserProfilePicture: String
    private var usernameErrorData by Delegates.notNull<Boolean>()
    private var passwordErrorData by Delegates.notNull<Boolean>()

    init {
        usernameErrorData = true
        passwordErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val firebaseAuth = FirebaseAuth.getInstance()
        val authPreference = LoginPreference(requireContext())
        val authRepository = AuthRepository(application, authPreference, firebaseAuth)
        val vmFactory = ViewModelFactorySign.getInstance(application, authRepository, firebaseAuth)

        loginViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[LoginViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        pref = LoginPreference(requireContext())

        binding.inForm.btnDaftar.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signUpFragment
            )
        }

        binding.inForm.btnLogin.isEnabled = false
        textListener()
        buttonCLick()
    }

    private fun textListener() {
        with(binding.inForm) {
            textUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val emailPattern = Pattern.compile(
                        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                    )
                    if (!emailPattern.matcher(s).matches()) {
                        usernameError.text = getString(R.string.email_format_error)
                        usernameError.alpha = 1F
                        usernameErrorData = true
                    } else {
                        usernameError.alpha = 0F
                        usernameErrorData = false
                    }
                    if (textUsername.toString().trim().isNullOrEmpty()) {
                        usernameError.alpha = 1F
                        usernameErrorData = true
                    } else {
                        usernameError.alpha = 0F
                        usernameErrorData = false
                    }
                    if (s?.contains(" ") == true) {
                        usernameError.text = getString(R.string.username_space)
                        usernameError.alpha = 1F
                        usernameErrorData = true
                    } else {
                        usernameError.alpha = 0F
                        usernameErrorData = false
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
                    passwordErrorData = s.toString().length < 8 || s.toString().trim().isEmpty()
                    if (passwordErrorData) {
                        binding.inForm.passwordError.alpha = 1F
                    } else {
                        binding.inForm.passwordError.alpha = 0F
                    }
                    btnLogin.isEnabled = !(usernameErrorData || passwordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }

    }

    private fun buttonCLick() {
        binding.inForm.btnLogin.setOnClickListener {
            val email = binding.inForm.textUsername.text.toString().trim()
            val password = binding.inForm.textPassword.text.toString().trim()

            loginViewModel.userLogin(email, password).observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseMessage.Loading -> {}
                    is ResponseMessage.Success -> {
                        if (response.data?.user != null) {
                            Toast.makeText(requireContext(), "User ditemukan", Toast.LENGTH_SHORT).show()

                            val currentUserUid = response.data?.user?.uid.toString()
                            database.child(currentUserUid).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val userData = snapshot.value as Map<String, Any>
                                        getUserEmail = userData["email"].toString()
                                        getUserName = userData["name"].toString()
                                        getUserRole = userData["role"].toString()
                                        getUserProfilePicture = userData["userProfilePicture"].toString()

                                        // Membuat objek LoginData
                                        val loginData = LoginData(
                                            response.data?.user?.uid.toString(),
                                            getUserName,
                                            getUserEmail,
                                            getUserRole,
                                            getUserProfilePicture,
                                            true
                                        )

                                        saveUserData(loginData)

                                        findNavController().navigate(
                                            R.id.action_loginFragment_to_homeActivity
                                        )

                                        requireActivity().finish()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("LoginFragment", "Gagal membaca data dari database")
                                }
                            })
                        } else {
                            Log.d("LoginFragment", "User tidak ditemukan")
                            Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResponseMessage.Error -> {
                        Log.d(
                            "OnErrorLogin: ",
                            "response: ${response.message.toString()}"
                        )
                        when (response.message) {
                            "There is no user record corresponding to this identifier. The user may have been deleted" ->
                                Toast.makeText(requireContext(), "Akun Tidak ditemukan", Toast.LENGTH_SHORT).show()
                            "The password is invalid or the user does not have a password" ->
                                Toast.makeText(requireContext(), "Email atau password salah", Toast.LENGTH_SHORT).show()
                            "The email address is badly formatted" ->
                                Toast.makeText(requireContext(), "Email atau password salah", Toast.LENGTH_SHORT).show()
                            else ->
                                Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun saveUserData(user: LoginData) {
        loginViewModel.saveUser(user)
    }

}
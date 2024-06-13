package com.example.kessekolah.ui.sign.login

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private lateinit var successDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var doneLogo: ImageView
    private lateinit var successTextView: TextView

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

            showLoadingDialog()
            loginViewModel.userLogin(email, password).observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseMessage.Loading -> {
                        showLoading()
                    }
                    is ResponseMessage.Success -> {
                        if (response.data?.user != null) {

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
                                    showErrorDialog(getString(R.string.error_login))
                                    Log.d("LoginFragment", "Gagal membaca data dari database")
                                }
                            })
                        } else {
                            showErrorDialog(getString(R.string.user_has_not_found))
                            Log.d("LoginFragment", "User tidak ditemukan")
//                            Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResponseMessage.Error -> {
                        Log.d(
                            "OnErrorLogin: ",
                            "response: ${response.message.toString()}"
                        )
                        when (response.message) {
                            "There is no user record corresponding to this identifier. The user may have been deleted" ->
//                                Toast.makeText(requireContext(), "Akun Tidak ditemukan", Toast.LENGTH_SHORT).show()
                                showErrorDialog(getString(R.string.user_has_not_found))
                            "The password is invalid or the user does not have a password" ->
//                                Toast.makeText(requireContext(), "Email atau password salah", Toast.LENGTH_SHORT).show()
                                showErrorDialog(getString(R.string.user_password_not_sync))
                            "The email address is badly formatted" ->
//                                Toast.makeText(requireContext(), "Email atau password salah", Toast.LENGTH_SHORT).show()
                                showErrorDialog(getString(R.string.email_format_error))
                            else ->
//                                Toast.makeText(requireContext(), "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()
                                showErrorDialog(getString(R.string.user_has_not_found))
                        }
                        showErrorDialog(response.message.toString())
                    }
                }
            }
        }
    }

    private fun showLoadingDialog() {
        successDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading_effect_layout)
            setCancelable(false)

            progressBar = findViewById(R.id.progressBar)
            doneLogo = findViewById(R.id.done_logo)
            successTextView = findViewById(R.id.successTextView)

            progressBar.visibility = View.VISIBLE
            doneLogo.visibility = View.GONE
            successTextView.visibility = View.GONE

            show()

            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message)
            )
        }
    }

    private fun showErrorDialog(message: String) {
        successDialog.dismiss()
        successDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loading_effect_layout)
            setCancelable(false)

            progressBar = findViewById(R.id.progressBar)
            doneLogo = findViewById(R.id.done_logo)
            successTextView = findViewById(R.id.successTextView)

            progressBar.visibility = View.GONE
            doneLogo.setImageResource(R.drawable.baseline_report_gmailerrorred_24)
            successTextView.text = message
            doneLogo.visibility = View.VISIBLE
            successTextView.visibility = View.VISIBLE

            show()

            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message)
            )
        }

        lifecycleScope.launch {
            delay(3000)
            successDialog.dismiss()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        doneLogo.visibility = View.GONE
        successTextView.visibility = View.GONE
        successDialog.show()
    }


    private fun saveUserData(user: LoginData) {
        loginViewModel.saveUser(user)
    }

}
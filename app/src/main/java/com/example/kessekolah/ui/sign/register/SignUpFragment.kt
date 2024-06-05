package com.example.kessekolah.ui.sign.register

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.repo.AuthRepository
import com.example.kessekolah.databinding.FragmentSignUpBinding
import com.example.kessekolah.model.SignUpViewModel
import com.example.kessekolah.viewModel.ViewModelFactorySign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates
import com.example.kessekolah.data.response.ResponseMessage
import com.example.kessekolah.utils.LoginPreference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var signUpViewModel: SignUpViewModel
    private val USERS_NODE = "users"
    private val USER_ID_COUNTER_NODE = "user_id_counter"

    private var emailErrorData by Delegates.notNull<Boolean>()
    private var passwordErrorData by Delegates.notNull<Boolean>()

    private lateinit var successDialog: Dialog
    private lateinit var progressBar: ProgressBar
    private lateinit var doneLogo: ImageView
    private lateinit var successTextView: TextView

    init {
        emailErrorData = true
        passwordErrorData = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val firebaseAuth = FirebaseAuth.getInstance()
        val authPreference = LoginPreference(requireContext())
        val authRepository = AuthRepository(application, authPreference, firebaseAuth)
        val vmFactory = ViewModelFactorySign.getInstance(application, authRepository, firebaseAuth)

        signUpViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[SignUpViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        textListener()
        buttonClick()

        binding.btnLoginPage.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_loginFragment
            )
        }
    }

    private fun textListener() {
        with(binding) {
            textUsername.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    emailErrorData = s.toString().trim().isEmpty()
                    binding.emailEmpty.alpha = if (emailErrorData) 1F else 0F
                    binding.emailHasSpace.alpha = if (s?.contains(" ") == true) 1F else 0F
                    if (!emailErrorData) {
                        binding.emailHasUsed.alpha = 0F
                        binding.emailFormatError.alpha = 0F
                    }
                    btnSignUp.isEnabled = !(emailErrorData || passwordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            textPassword.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    passwordErrorData = s.toString().length < 6 || s.toString().trim().isEmpty()
                    binding.passwordError.alpha = if (passwordErrorData) 1F else 0F
                    btnSignUp.isEnabled = !(emailErrorData || passwordErrorData)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun buttonClick() {
        with(binding) {
            btnSignUp.setOnClickListener {
                val email = textUsername.text.toString().trim()
                val password = textPassword.text.toString().trim()

                setupSuccessDialog()
                signUpViewModel.checkEmailExists(email) { isEmailExists ->
                    if (isEmailExists) {
                        binding.emailHasUsed.alpha = 1F
                        btnSignUp.post { btnSignUp.isEnabled = false }
                    } else {
                        signUpViewModel.insertUser(email, password).observe(viewLifecycleOwner) { response ->
                            when(response) {
                                is ResponseMessage.Loading -> {
                                    showLoading()
                                }
                                is ResponseMessage.Success -> {
                                    database.child(USER_ID_COUNTER_NODE).child("counter").addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val counter =
                                                dataSnapshot.getValue(Long::class.java) ?: 0
                                            val newUserId = counter + 1
                                            val currentUserUid = auth.currentUser?.uid
                                            if (currentUserUid != null) {
                                                val user = User(
                                                    id = newUserId.toInt(), // Assign the new ID to the user
                                                    name = email,
                                                    email = email,
                                                    userProfilePicture = "",
                                                    role = "",
                                                    uid = currentUserUid,
                                                    createdAt = getCurrentDateTime()
                                                )
                                                database.child(USERS_NODE).child(currentUserUid)
                                                    .setValue(user).addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            database.child(USER_ID_COUNTER_NODE).child("counter")
                                                                .setValue(newUserId)
                                                            showSuccess()
                                                        } else {
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Failed to create account",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Handle error
                                        }
                                    })
                                }
                                is ResponseMessage.Error -> {
                                    Log.d("OnErrorRegister: ", "response: ${response.message}")
                                    when (response.message) {
                                        "The email address is badly formatted" -> binding.emailFormatError.alpha = 1F
                                        "The email address is already in use by another account." -> binding.emailHasUsed.alpha = 1F
                                        else -> Toast.makeText(requireContext(), R.string.error_register, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            btnLoginPage.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
    }

    private fun setupSuccessDialog() {
        successDialog = Dialog(requireContext())
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        successDialog.setContentView(R.layout.loading_effect_layout)

        progressBar = successDialog.findViewById(R.id.progressBar)
        doneLogo = successDialog.findViewById(R.id.done_logo)
        successTextView = successDialog.findViewById(R.id.successTextView)

        successDialog.setCancelable(false)
        successDialog.show()

        // Atur dimensi dan posisi dialog
        val window = successDialog.window
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.pop_out_message))
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        doneLogo.visibility = View.GONE
        successTextView.visibility = View.GONE
        successDialog.show()
    }

    private fun showSuccess() {
        progressBar.visibility = View.GONE
        doneLogo.visibility = View.VISIBLE
        successTextView.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(3000)
            successDialog.dismiss()
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }


    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}

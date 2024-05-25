package com.example.kessekolah.ui.sign.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.lifecycle.ViewModelProvider
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.ActivityCompleteProfileBinding
import com.example.kessekolah.model.CompleteProfileViewModel
import com.example.kessekolah.viewModel.ViewModelFactorySign
import com.example.kessekolah.utils.LoginPreference
import kotlin.properties.Delegates

class CompleteProfileActivity : AppCompatActivity() {

    private var _binding: ActivityCompleteProfileBinding? = null
    private val binding get() = _binding!!

    private var nameErrorData by Delegates.notNull<Boolean>()
    private var emailErrorData by Delegates.notNull<Boolean>()

    private lateinit var completeProfileViewModel: CompleteProfileViewModel
    private lateinit var phoneNumber: String
    private lateinit var username: String
    private lateinit var password: String
    private var user: User? = null

    private lateinit var pref: LoginPreference

    init {
        nameErrorData = true
        emailErrorData = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val vmFactory = ViewModelFactorySign.getInstance(application)
        completeProfileViewModel = ViewModelProvider(this, vmFactory)[CompleteProfileViewModel::class.java]

        phoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()
        username = intent.getStringExtra("USERNAME").toString()
        password = intent.getStringExtra("PASSWORD").toString()

        pref = LoginPreference(this)

        textListener()
        buttonCLick()
    }

    private fun textListener() {
        with(binding) {
            textName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    nameErrorData = s.toString().trim().isNullOrEmpty()
                    if (nameErrorData) {
                        binding.nameError.alpha = 1F
                    } else {
                        binding.nameError.alpha = 0F
                    }
                    btnNext.isEnabled = !(nameErrorData || emailErrorData)
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            textEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    emailErrorData = !Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() || s.toString().trim().isNullOrEmpty()
//                    if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
//                        binding.emailError.alpha = 1F
//                    } else {
//                        binding.emailError.alpha = 0F
//                    }
                    if (s.toString().trim().isNullOrEmpty()) {
                        binding.emailEmpty.alpha = 1F
                    } else {
                        binding.emailEmpty.alpha = 0F
                    }
                    btnNext.isEnabled = !(nameErrorData || emailErrorData)
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })

            textEmail.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    val email = textEmail.text.toString().trim()
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
                        binding.emailError.alpha = 1F
                    } else {
                        binding.emailError.alpha = 0F
                    }
                }
            }
        }
    }

    private fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun buttonCLick() {
        with(binding) {

            btnNext.setOnClickListener {
                var name = textName.text.toString().trim()
                var email = textEmail.text.toString().trim()

                completeProfileViewModel.checkEmailExists(email) { isEmailExists ->
                    if (isEmailExists) {
                        binding.emailUsed.alpha = 1F
                        btnNext.post {
                            btnNext.isEnabled = false
                        }
                    } else {
                        binding.emailUsed.alpha = 0F
                        user = User(0, phoneNumber, username, name, email, password)
                        user?.let { user ->
                            completeProfileViewModel.insertUser(user)
                        }
                        val token = generateToken(15)
                        val data = LoginData(token, username, name, email, phoneNumber)
                        pref = LoginPreference(this@CompleteProfileActivity)
                        pref.saveData(data)
//                        val intent = Intent(this@CompleteProfileActivity, PilihWilayahActivity::class.java)
//                        startActivity(intent)
                    }
                }
            }
        }
    }

}
package com.fiqri.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.data.remote.response.RegisterResponse
import com.fiqri.mystoryapp.data.remote.retrofit.ApiConfig
import com.fiqri.mystoryapp.databinding.ActivityMainBinding
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var customButton: CustomButton
    private lateinit var customButton2: CustomButton
    private lateinit var customInputPassword: CustomPasswordInput
    private lateinit var customInputName: CustomInput
    private lateinit var customInputEmail: CustomInput

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        supportActionBar?.hide()
        loginViewModel.getLoginInfo("password").observe(this) { password: String? ->
            if(password?.length!! >= 8) {
                finish()
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customButton = findViewById(R.id.my_button)
        customInputName = findViewById(R.id.ed_register_name)
        customInputEmail = findViewById(R.id.ed_register_email)
        customInputPassword = findViewById(R.id.ed_register_password)
        customButton2 = findViewById(R.id.my_button2)
        customButton2.text = "Sudah punya akun? login disini"

        customInputName.hint = "Masukkan nama anda"
        customInputEmail.hint = "Masukkan email anda"
        customInputPassword.hint = "Masukkan password anda"
        setMyButtonEnable()

        customInputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        customInputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        customInputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        customButton.setOnClickListener {
            register(customInputName.text.toString(), customInputEmail.text.toString(), customInputPassword.text.toString())
        }

        customButton2.setOnClickListener {
            val login = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(login)
            finish()
        }
        playAnimation()
    }

    private fun setMyButtonEnable() {
        val result = customInputPassword.text
        val email = customInputEmail.text
        val name = customInputName.text
        var enableEmail = false
        var enableName = false
        var enablePass = false

        if (name.toString().isEmpty()) {
            customButton.isEnabled = false
            customButton.text = "Bagian nama harus diisi terlebih dahulu"
        } else {
            enableName = true
        }

        if (email.toString().isEmpty()) {
            customButton.isEnabled = false
            customButton.text = "Bagian email harus diisi terlebih dahulu"
        } else {
            enableEmail = true
        }

        if (result.toString().length < 8) {
            customButton.isEnabled = false
            customButton.text = "Password harus 8 karakter atau lebih"
        } else {
            enablePass = true
        }

        if (enableName && enableEmail && enablePass) {
            customButton.isEnabled = true
            customButton.text = "Sign Up"
        }

    }

    fun register(name: String, email: String, password: String) {
        val client = ApiConfig.getApiService("aaa").register(name, email, password)
        customButton.isEnabled = false
        customButton.text = "Loading"
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    customButton.isEnabled = true
                    customButton.text = "Sign Up"
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Akun terdaftar")
                        setMessage("Akun anda berhasil terdaftar!")
                        setPositiveButton("Kembali") { _, _ ->

                        }
                        create()
                        show()
                    }
                } else {
                    customButton.isEnabled = true
                    customButton.text = "Sign Up"
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Gagal Sign Up!")
                        setMessage("Email yang anda ketik sudah terdaftar")
                        setPositiveButton("Coba lagi") { _, _ ->

                        }
                        create()
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                customButton.isEnabled = true
                customButton.text = "Sign Up"
                AlertDialog.Builder(this@RegisterActivity).apply {
                    setTitle("Gagal Sign Up!")
                    setMessage("Gagal terkoneksi ke server, periksa koneksi internet anda dan coba lagi")
                    setPositiveButton("Coba lagi") { _, _ ->

                    }
                    create()
                    show()
                }
            }
        })

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.appImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.registerInstruction, View.ALPHA, 1f).setDuration(300)
        val name =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(300)
        val email =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.myButton2, View.ALPHA, 1f).setDuration(300)

        val together1 = AnimatorSet().apply {
            playTogether(title, name)
        }

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(
                together1,
                email,
                password,
                together
            )
            startDelay = 100
        }.start()
    }
}
package com.fiqri.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.data.remote.response.LoginResponse
import com.fiqri.mystoryapp.data.remote.response.LoginResult
import com.fiqri.mystoryapp.data.remote.retrofit.ApiConfig
import com.fiqri.mystoryapp.databinding.ActivityLoginBinding
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var customButton: CustomButton
    private lateinit var toRegisterButton: CustomButton
    private lateinit var customInputPassword: CustomPasswordInput
    private lateinit var customInputEmail: CustomInput

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        customButton = findViewById(R.id.my_button)
        toRegisterButton = findViewById(R.id.toRegister)
        customInputEmail = findViewById(R.id.login_email)
        customInputPassword = findViewById(R.id.login_password)

        toRegisterButton.text = "Belum punya akun? Sign up disini!"

        customInputEmail.hint = "Masukkan email anda"
        customInputPassword.hint = "Masukkan password anda"

        setMyButtonEnable()

        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.getLoginInfo("password").observe(this) { password: String? ->
            if(password?.length!! >= 8) {
                val toStory = Intent(this@LoginActivity, StoryListActivity::class.java)
                toStory.putExtra("reload", "no")
                startActivity(toStory)
                finish()
            }
        }

        customInputPassword.addTextChangedListener(object : TextWatcher {
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
            login(customInputEmail.text.toString(), customInputPassword.text.toString())
        }

        toRegisterButton.setOnClickListener{
            val toRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(toRegister)
            finish()
        }
        playAnimation()
    }

    fun login(email: String, password: String) {
        val client = ApiConfig.getApiService("aaa").login(email, password)
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        customButton.text = "Loading"
        customButton.isEnabled = false
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                var userInfo: LoginResult?
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        userInfo = response.body()?.loginResult
                        loginViewModel.saveLoginInfo("username", userInfo?.name!!)
                        loginViewModel.saveLoginInfo("password", password)
                        loginViewModel.saveLoginInfo("token", userInfo?.token!!)
                    }
                    val toStory = Intent(this@LoginActivity, StoryListActivity::class.java)
                    toStory.putExtra("reload", "no")
                    startActivity(toStory)
                    finish()
                } else {
                    customButton.text = "Login"
                    customButton.isEnabled = true
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Gagal login!")
                        setMessage("Email/password anda salah")
                        setPositiveButton("Coba lagi") { _, _ ->

                        }
                        create()
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                customButton.text = "Login"
                customButton.isEnabled = true
                AlertDialog.Builder(this@LoginActivity).apply {
                    setTitle("Gagal login!")
                    setMessage("Gagal terkoneksi ke server, periksa koneksi internet anda dan coba lagi")
                    setPositiveButton("Coba lagi") { _, _ ->

                    }
                    create()
                    show()
                }
            }
        })

    }

    private fun setMyButtonEnable() {
        val result = customInputPassword.text
        val email = customInputEmail.text
        var enableEmail = false
        var enablePass = false

        if (email.toString().isEmpty()) {
            customButton.isEnabled = false
            customButton.text = "Bagian email harus diisi terlebih dahulu"
        } else {
            enableEmail = true
        }

        if (result.toString().isEmpty()) {
            customButton.isEnabled = false
            customButton.text = "Password harus diisi terlebih dahulu"
        } else {
            enablePass = true
        }


        if (enableEmail && enablePass) {
            customButton.isEnabled = true
            customButton.text = "Login"
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.appLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.loginInstruction, View.ALPHA, 1f).setDuration(300)
        val email =
            ObjectAnimator.ofFloat(binding.loginEmail, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.toRegister, View.ALPHA, 1f).setDuration(300)

        val together1 = AnimatorSet().apply {
            playTogether(title, email)
        }

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(
                together1,
                password,
                together
            )
            startDelay = 100
        }.start()
    }
}
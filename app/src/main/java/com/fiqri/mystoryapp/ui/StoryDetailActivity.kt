package com.fiqri.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.databinding.ActivityStoryDetailBinding
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore


class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        supportActionBar?.setTitle(intent.getStringExtra("author")!! + "'s post")
        Glide.with(binding.storyImg.context)
            .load(intent.getStringExtra("imgUrl")!!)
            .into(binding.storyImg)

        binding.uName.text = intent.getStringExtra("author")!!
        binding.descText.text = intent.getStringExtra("desc")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        when(item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                val toLogin = Intent(this@StoryDetailActivity, LoginActivity::class.java)
                startActivity(toLogin)
                finish()
            }

            R.id.map -> {
                val toMap = Intent(this@StoryDetailActivity, StoryMapsLocationActivity::class.java)
                startActivity(toMap)
            }
        }
        return true
    }
}
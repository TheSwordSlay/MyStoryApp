package com.fiqri.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.databinding.ActivityStoryListBinding
import com.fiqri.mystoryapp.helper.login.LoginPreferences
import com.fiqri.mystoryapp.helper.login.LoginViewModel
import com.fiqri.mystoryapp.helper.login.LoginViewModelFactory
import com.fiqri.mystoryapp.helper.login.dataStore
import com.fiqri.mystoryapp.helper.stories.LoadingStateAdapter
import com.fiqri.mystoryapp.helper.stories.StoryAdapter
import com.fiqri.mystoryapp.helper.stories.StoryViewModel
import com.fiqri.mystoryapp.helper.stories.ViewModelFactory

class StoryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryListBinding
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )

        loginViewModel.getLoginInfo("password").observe(this) { password: String? ->
            if(password?.length!! < 8) {
                finish()
            }
        }

        loginViewModel.getLoginInfo("username").observe(this) { name: String? ->
            supportActionBar?.setTitle("Welcome " + name)
        }


        loginViewModel.getLoginInfo("token").observe(this) { utoken: String? ->
            binding.fabAddStory.setOnClickListener {
                val toPost = Intent(this@StoryListActivity, PostStoryActivity::class.java)
                toPost.putExtra("token", utoken!!)
                startActivity(toPost)
            }
        }

        if(intent.getStringExtra("reload")!! == "yes"){
            val here = Intent(this@StoryListActivity, StoryListActivity::class.java)
            here.putExtra("reload", "no")
            startActivity(here)
            finish()
        }

        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getData() {
        val adapter = StoryAdapter()
        binding.listStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.storyList.observe(this, {
            adapter.submitData(lifecycle, it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val pref = LoginPreferences.getInstance(application.dataStore)
        val loginViewModel = ViewModelProvider(this, LoginViewModelFactory(pref)).get(
            LoginViewModel::class.java
        )
        when(item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                val toLogin = Intent(this@StoryListActivity, LoginActivity::class.java)
                startActivity(toLogin)
                finish()
            }

            R.id.map -> {
                val toMap = Intent(this@StoryListActivity, StoryMapsLocationActivity::class.java)
                startActivity(toMap)
            }
        }
        return true
    }
}
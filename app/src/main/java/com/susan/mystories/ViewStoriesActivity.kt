package com.susan.mystories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.susan.mystories.databinding.ActivityViewStoriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewStoriesActivity : AppCompatActivity() {

    private val userViewModel by viewModels<StoriesViewModel>()
    private val storyViewModel by viewModels<ViewStoriesViewModel>()
    private lateinit var viewStoryBinding: ActivityViewStoriesBinding
    private lateinit var storyAdapter: ViewStoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewStoryBinding = ActivityViewStoriesBinding.inflate(layoutInflater)
        setContentView(viewStoryBinding.root)
        supportActionBar?.title = getString(R.string.your_page)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        storyViewModel.loadAllData.observe(this) {
            showLoading(it)
        }
        aturTampilan()
        accountToken()
        validateButton()
    }

    private fun aturTampilan() {
        storyAdapter = ViewStoriesAdapter()
        viewStoryBinding.rvSnapstory.layoutManager = GridLayoutManager(this@ViewStoriesActivity, 2)
        viewStoryBinding.rvSnapstory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadStoriesAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun getStoriesFromAccount() {
        storyViewModel.getMoreStories.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun accountToken() {
        userViewModel.getAccountStories().observe(this) {
            if (it.token.trim() == "") {
                val intent = Intent(this@ViewStoriesActivity, SignInStoriesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                getStoriesFromAccount()
            }
        }
    }

    private fun validateButton() {
        viewStoryBinding.btnCreate.setOnClickListener {
            val intent = Intent(this@ViewStoriesActivity, PostStoriesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun keluarApp() {
        userViewModel.signOutStories()
        accountToken()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                val mapIntent = Intent(this, MapsStoriesActivity::class.java)
                startActivity(mapIntent)
            }
            R.id.bahasa -> {
                val bahasaIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(bahasaIntent)
            }
            R.id.sign_out -> {
                keluarApp()
            }
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            viewStoryBinding.progressBar.visibility = View.VISIBLE
        } else {
            viewStoryBinding.progressBar.visibility = View.GONE
        }
    }
}
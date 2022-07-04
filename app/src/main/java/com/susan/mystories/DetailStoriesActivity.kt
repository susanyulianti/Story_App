package com.susan.mystories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.susan.mystories.databinding.ActivityDetailStoriesBinding


class DetailStoriesActivity : AppCompatActivity() {
    private lateinit var activityDetailStoriesBinding: ActivityDetailStoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailStoriesBinding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(activityDetailStoriesBinding.root)
        supportActionBar?.title = "Detail"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        storiesDetail()
    }

    private fun storiesDetail(){
        val detailStories = intent.getParcelableExtra<ListStoriesPost>("ListStoryItem") as ListStoriesPost
        activityDetailStoriesBinding.apply {
            activityDetailStoriesBinding.nameAccount.text = detailStories.name
            activityDetailStoriesBinding.created.text = detailStories.createdAt
            activityDetailStoriesBinding.tvDescription.text = detailStories.description
            activityDetailStoriesBinding.tvLat.text =
                "Latitude       : " + detailStories.lat.toString()
            activityDetailStoriesBinding.tvLon.text =
                "Longitude    : " + detailStories.lon.toString()

            Glide.with(applicationContext)
                .load(detailStories.photoUrl)
                .centerCrop()
                .into(activityDetailStoriesBinding.storiesImage)
        }
    }
}
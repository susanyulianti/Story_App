package com.susan.mystories

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.susan.mystories.databinding.StoriesRowItemBinding

class ViewStoriesAdapter : PagingDataAdapter<ListStoriesPost, ViewStoriesAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    class StoryViewHolder(private val binding: StoriesRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: ListStoriesPost) {
            binding.apply {
                Glide.with(itemView)
                    .load(listStoryItem.photoUrl)
                    .into(binding.storiesPhoto)
                binding.nameAccount.text = listStoryItem.name
                binding.tvDescription.text = listStoryItem.description
            }

            itemView.setOnClickListener {
                val detailIntent = Intent(itemView.context, DetailStoriesActivity::class.java)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storiesPhoto, "photo"),
                        Pair(binding.nameAccount, "name"),
                        Pair(binding.tvDescription, "description"),
                    )
                detailIntent.putExtra("ListStoryItem", listStoryItem)
                itemView.context.startActivity(detailIntent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoriesRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val dataItem = getItem(position)
        if (dataItem != null) {
            holder.bind(dataItem)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoriesPost>() {
            override fun areItemsTheSame(lastStories: ListStoriesPost, startStories: ListStoriesPost): Boolean {
                return lastStories == startStories
            }
            override fun areContentsTheSame(lastStories: ListStoriesPost, startStories: ListStoriesPost): Boolean {
                return  lastStories.photoUrl == startStories.photoUrl &&
                        lastStories.name == startStories.name &&
                        lastStories.id == startStories.id &&
                        lastStories.createdAt == startStories.createdAt &&
                        lastStories.description == startStories.description
            }
        }
    }
}
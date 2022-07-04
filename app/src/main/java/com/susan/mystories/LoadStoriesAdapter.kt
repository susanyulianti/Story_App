package com.susan.mystories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.susan.mystories.databinding.StoriesRowLoadingBinding

class LoadStoriesAdapter (private val restart: () -> Unit) : LoadStateAdapter<LoadStoriesAdapter.ViewHolder>() {
    inner class ViewHolder(private val storiesRowLoadingBinding: StoriesRowLoadingBinding, restart: () -> Unit) : RecyclerView.ViewHolder(storiesRowLoadingBinding.root) {
        init {
            storiesRowLoadingBinding.restart.setOnClickListener { restart.invoke() }
        }

        fun bind(loadState: LoadState) {
            if(loadState is LoadState.Error) {
                storiesRowLoadingBinding.loadFailed.text = loadState.error.localizedMessage
            }
            storiesRowLoadingBinding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                loadFailed.isVisible = loadState is LoadState.Loading
                restart.isVisible = loadState is LoadState.Loading
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = StoriesRowLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, restart)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
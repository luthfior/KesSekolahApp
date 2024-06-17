package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.databinding.ItemBannerVideoBinding

class VideoListAdapterCore :
    ListAdapter<Int, VideoListAdapterCore.ListViewHolder>(VideoListDiffCallback()) {

    class ListViewHolder(var binding: ItemBannerVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemBannerVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val drawableRes = getItem(position)

        with(holder.binding) {
            backColorBanner.setBackgroundResource(drawableRes)
             textNews.text = ""
             ilusBanner.setImageDrawable(null)
        }
    }
}

class VideoListDiffCallback : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}

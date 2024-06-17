package com.example.kessekolah.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.ItemBannerVideoBinding

class EbookListAdapterCore(private val fragment: Fragment) :
    ListAdapter<Int, EbookListAdapterCore.ListViewHolder>(EbookListDiffCallback()) {

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

            holder.itemView.setOnClickListener {
                fragment.findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
            }
        }
    }
}

class EbookListDiffCallback : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}
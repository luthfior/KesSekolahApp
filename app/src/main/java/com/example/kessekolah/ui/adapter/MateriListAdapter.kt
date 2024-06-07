package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.data.database.MateriList
import com.example.kessekolah.databinding.MateriItemListBinding

class MateriListAdapter : ListAdapter<MateriList, MateriListAdapter.ListViewHolder>(MateriListDiffCallback()) {
    class ListViewHolder(var binding: MateriItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = MateriItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, category, timeStamp, icon) = getItem(position)
        holder.binding.ivItemIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, icon))
        holder.binding.tvItemTitle.text = title
        holder.binding.tvItemCategory.text = category
        holder.binding.tvItemTime.text = timeStamp
    }
}

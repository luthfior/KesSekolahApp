package com.example.kessekolah.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.kessekolah.data.database.MateriList

class MateriListDiffCallback : DiffUtil.ItemCallback<MateriList>() {
    override fun areItemsTheSame(oldItem: MateriList, newItem: MateriList): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: MateriList, newItem: MateriList): Boolean {
        return oldItem == newItem
    }
}
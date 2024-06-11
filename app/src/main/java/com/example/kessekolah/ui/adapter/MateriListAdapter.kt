package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriList
import com.example.kessekolah.databinding.MateriItemListBinding

class MateriListAdapter :
    ListAdapter<MateriList, MateriListAdapter.ListViewHolder>(MateriListDiffCallback()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: MateriItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            MateriItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (fileName, title, fileUrl, tahun, category, timeStamp, icon) = getItem(position)
        val data = getItem(position)

        with(holder.binding) {
            ivItemIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_book))
            tvItemTitle.text = data.title
            tvItemCategory.text = data.category
            tvItemTime.text = data.timeStamp
            btnDelete.setOnClickListener {
                onItemClickCallback.onDeleteClicked(data)
            }
            btnEdit.setOnClickListener {
                onItemClickCallback.onEditClicked(data)
            }

        }
    }

    interface OnItemClickCallback {
        fun onDeleteClicked(data: MateriList)
        fun onEditClicked(data: MateriList)
    }
}

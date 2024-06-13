package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.BookmarkItemListBinding

class BookMarkAdapter : RecyclerView.Adapter<BookMarkAdapter.ListViewHolder>() {

    private val listUsersFavorite = ArrayList<MateriData>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setListUsers(favoriteUser: List<MateriData>) {
        listUsersFavorite.addAll(favoriteUser)
        notifyDataSetChanged()
    }

    inner class ListViewHolder(private val binding: BookmarkItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(materiBookMark: MateriData) {
            with(binding) {
                tvItemTitle.text = materiBookMark.judul
                tvItemCategory.text = materiBookMark.category
                tvItemTime.text = materiBookMark.timestamp
                ivItemIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_book))

                itemView.setOnClickListener { onItemClickCallback.onItemClicked(materiBookMark) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = BookmarkItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUsersFavorite[position])
    }

    override fun getItemCount(): Int = listUsersFavorite.size

    interface OnItemClickCallback {
        fun onItemClicked(data: MateriData)
    }
}




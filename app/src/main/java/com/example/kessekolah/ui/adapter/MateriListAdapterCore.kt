package com.example.kessekolah.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriList
import com.example.kessekolah.databinding.ItemBannerMateriBinding
import com.example.kessekolah.databinding.MateriItemListBinding

class MateriListAdapterCore:
    ListAdapter<MateriList, MateriListAdapterCore.ListViewHolder>(MateriListDiffCallback()) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemBannerMateriBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemBannerMateriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (title, fileUrl, category, timeStamp, icon) = getItem(position)

        with(holder.binding) {
            ilusBanner.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    holder.itemView.context.getResources()
                        .getIdentifier("ilus_banner_$icon", "drawable", holder.itemView.context.getPackageName())
                )
            )
            textNews.text = title

            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getItem(position))}

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MateriList)
    }
}

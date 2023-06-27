package com.example.chernykhhomework.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.chernykhhomework.databinding.HelpItemBinding
import com.example.chernykhhomework.presentation.entity.HelpPage
import com.example.chernykhhomework.presentation.viewholders.HelpViewHolder

class HelpAdapter : ListAdapter<HelpPage, HelpViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder =
        HelpViewHolder(
            HelpItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, position, currentList.size)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<HelpPage>() {

            override fun areItemsTheSame(oldItem: HelpPage, newItem: HelpPage): Boolean =
                oldItem.textId == newItem.textId


            override fun areContentsTheSame(oldItem: HelpPage, newItem: HelpPage): Boolean =
                oldItem == newItem


        }
    }
}
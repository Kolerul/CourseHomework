package com.example.chernykhhomework.presentation.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.HelpItemBinding
import com.example.chernykhhomework.presentation.entity.HelpPage

class HelpViewHolder(private val binding: HelpItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(helpPage: HelpPage, position: Int, total: Int) {
        binding.apply {
            helpImage.setImageResource(helpPage.imageId)
            helpText.text = binding.root.context.getString(helpPage.textId)
            helpPageCounter.text =
                binding.root.context.getString(R.string.page_counter, (position + 1), total)
        }
    }
}
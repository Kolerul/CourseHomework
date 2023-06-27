package com.example.chernykhhomework.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.databinding.LoanItemBinding
import com.example.chernykhhomework.presentation.viewholders.LoanViewHolder

class LoansAdapter(private val onItemClick: (Loan) -> Unit) :
    ListAdapter<Loan, LoanViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder =
        LoanViewHolder(
            LoanItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClick(current)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Loan>() {
            override fun areItemsTheSame(oldItem: Loan, newItem: Loan): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: Loan, newItem: Loan): Boolean =
                oldItem == newItem

        }
    }
}
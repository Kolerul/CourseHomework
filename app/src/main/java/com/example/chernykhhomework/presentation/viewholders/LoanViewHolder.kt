package com.example.chernykhhomework.presentation.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.chernykhhomework.R
import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.databinding.LoanItemBinding

class LoanViewHolder(private val binding: LoanItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loan: Loan) {
        binding.apply {
            amount.text = binding.root.context.getString(R.string.amount, loan.amount)
            date.text = binding.root.context.getString(R.string.date, loan.date)
            state.text = binding.root.context.getString(R.string.state, loan.state)
        }
    }
}
package com.example.chernykhhomework.presentation.viewholders

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import com.example.chernykhhomework.R
import com.example.chernykhhomework.data.network.entity.Loan
import com.example.chernykhhomework.databinding.LoanItemBinding


class LoanViewHolder(private val binding: LoanItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loan: Loan) {
        binding.apply {
            amount.text = binding.root.context.getString(R.string.amount, loan.amount)

            val parts = loan.date.split(".")
            val dateAndTime = parts[0].split("T")
            date.text =
                binding.root.context.getString(R.string.date, "${dateAndTime[0]} ${dateAndTime[1]}")


            state.text = binding.root.context.getString(R.string.state, loan.state)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (loan.state) {
                    "APPROVED" -> state.setTextColor(binding.root.context.getColor(R.color.green))
                    "REGISTERED" -> state.setTextColor(binding.root.context.getColor(R.color.light_orange))
                    "REJECTED" -> state.setTextColor(binding.root.context.getColor(R.color.red))
                }
            }

        }
    }
}
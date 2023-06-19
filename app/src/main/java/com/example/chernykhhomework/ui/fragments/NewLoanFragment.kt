package com.example.chernykhhomework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.App
import com.example.chernykhhomework.R
import com.example.chernykhhomework.data.network.entity.LoanConditions
import com.example.chernykhhomework.data.network.entity.LoanRequest
import com.example.chernykhhomework.databinding.FragmentNewLoanBinding
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import com.example.chernykhhomework.presentation.viewmodel.NewLoanFragmentViewModel

class NewLoanFragment : Fragment() {

    private var binding: FragmentNewLoanBinding? = null
    private val notNullBinding: FragmentNewLoanBinding
        get() = binding!!

    private val viewModel: NewLoanFragmentViewModel by viewModels {
        (activity?.application as App).appComponent.viewModelsFactory()
    }

    private var loanConditions: LoanConditions? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewLoanBinding.inflate(inflater, container, false)
        return notNullBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIStateObserver()
        setOnClickListeners()
        setOnItemMenuClickListeners()
    }

    private fun setOnItemMenuClickListeners() {
        notNullBinding.newLoanFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.update_button -> {
                    viewModel.conditionsRequest()
                    true
                }

                R.id.help_button -> {
                    //Какая то помощь
                    true
                }

                else -> {
                    false
                }
            }
        }

        notNullBinding.newLoanFragmentToolbar.setNavigationOnClickListener {
            val bundle = bundleOf(LoansListFragment.APP_IS_RUNNING_ARGUMENT_KEY to true)
            findNavController().navigate(R.id.action_global_registrationFragment, bundle)
        }
    }

    private fun setUIStateObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewLoanUIState.Initializing -> {
                    viewModel.conditionsRequest()
                    notNullBinding.arrangeButton.isEnabled = false
                    notNullBinding.emptyFieldsWarningTextView.isVisible = false
                    showContentLayout()
                }

                is NewLoanUIState.Loading -> {
                    showLoadingProgressBar()
                }

                is NewLoanUIState.Success -> {
                    notNullBinding.arrangeButton.isEnabled = true
                    showContentLayout()
                    if (state.conditions != null) {
                        notNullBinding.apply {
                            loanConditions = state.conditions
                            amountTitleTextView.text =
                                requireContext().getString(
                                    R.string.amount_title,
                                    state.conditions.maxAmount
                                )
                            periodTextView.text = state.conditions.period.toString()
                            percentTextView.text = "${state.conditions.percent}%"
                        }
                    } else {
                        showRequestResultLayout(true)
                        findNavController().navigate(R.id.action_newLoanFragment_to_loansListFragment)
                    }
                }

                is NewLoanUIState.Error -> {
                    notNullBinding.emptyFieldsWarningTextView.text = state.message
                    notNullBinding.emptyFieldsWarningTextView.isVisible = true
                }
            }
        }
    }

    private fun showContentLayout() {
        notNullBinding.apply {
            contentLayout.visibility = View.VISIBLE
            requestResultLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
        }
    }

    private fun showLoadingProgressBar() {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            requestResultLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun showRequestResultLayout(isSuccessful: Boolean) {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            requestResultLayout.visibility = View.VISIBLE
            if (isSuccessful) {
                requestResultImage.setImageResource(R.drawable.ic_success)
                requestResultText.text =
                    requireContext().getString(R.string.successful_new_loan_request)
            } else {
                requestResultImage.setImageResource(R.drawable.ic_error)
                requestResultText.text = requireContext().getString(R.string.error_new_loan_request)
            }
            loadingProgressBar.visibility = View.GONE
        }
    }


    private fun setOnClickListeners() {
        notNullBinding.arrangeButton.setOnClickListener {
            if (fieldsNotBlank()) {
                if (viewModel.uiState.value is NewLoanUIState.Success) {

                }
                notNullBinding.apply {
                    val loan = LoanRequest(
                        amountTextView.text.toString().toLong(),
                        firstNameTextView.text.toString(),
                        lastNameTextView.text.toString(),
                        loanConditions?.percent ?: 0.0,
                        loanConditions?.period ?: 0,
                        phoneNumberTextView.text.toString()
                    )
                    viewModel.newLoanRequest(loan)
                }

            } else {
                notNullBinding.emptyFieldsWarningTextView.isVisible = true
            }
        }
    }

    private fun fieldsNotBlank(): Boolean {
        notNullBinding.apply {
            return (amountTextView.text.isNotBlank() && firstNameTextView.text.isNotBlank()
                    && lastNameTextView.text.isNotBlank() && phoneNumberTextView.text.isNotBlank())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
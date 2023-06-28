package com.example.chernykhhomework.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.LoanApplication
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.LoanConditions
import com.example.chernykhhomework.domain.entity.LoanRequest
import com.example.chernykhhomework.databinding.FragmentNewLoanBinding
import com.example.chernykhhomework.presentation.uistate.NewLoanUIState
import com.example.chernykhhomework.presentation.viewmodel.NewLoanFragmentViewModel
import com.example.chernykhhomework.ui.fragments.dialogfragment.HelpDialogFragment
import com.example.chernykhhomework.ui.util.animationBlinking
import javax.inject.Inject

class NewLoanFragment : Fragment() {

    private var binding: FragmentNewLoanBinding? = null
    private val notNullBinding: FragmentNewLoanBinding
        get() = binding!!

    private val viewModel: NewLoanFragmentViewModel by viewModels {
        (activity?.application as LoanApplication).appComponent.viewModelsFactory()
    }

    private var loanConditions: LoanConditions? = null

    @Inject
    lateinit var helpDialog: HelpDialogFragment

    override fun onAttach(context: Context) {
        (requireActivity().application as LoanApplication).appComponent.inject(this)
        super.onAttach(context)
    }

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

        if (savedInstanceState != null) {
            loanConditions = LoanConditions(
                maxAmount = savedInstanceState.getLong(MAX_AMOUNT_CONDITION),
                percent = savedInstanceState.getDouble(PERCENT_CONDITION),
                period = savedInstanceState.getInt(PERIOD_CONDITION)
            )

            showConditions(loanConditions!!)
        }

        setUIStateObserver()
        setOnClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (loanConditions != null) {
            outState.putLong(MAX_AMOUNT_CONDITION, loanConditions!!.maxAmount)
            outState.putDouble(PERCENT_CONDITION, loanConditions!!.percent)
            outState.putInt(PERIOD_CONDITION, loanConditions!!.period)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setOnClickListeners() {
        notNullBinding.submitButton.setOnClickListener {
            if (fieldsNotBlank()) {
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
                showErrorText(getString(R.string.empty_fields_warning))
            }
            hideSoftKeyboard()
        }

        notNullBinding.successfulResultLayout.setOnClickListener {
            findNavController().navigate(R.id.action_newLoanFragment_to_loansListFragment)
        }

        notNullBinding.toLoanListButton.setOnClickListener {
            hideSoftKeyboard()
            findNavController().navigate(R.id.action_newLoanFragment_to_loansListFragment)
        }

        setOnItemMenuClickListeners()
    }

    private fun setOnItemMenuClickListeners() {
        notNullBinding.newLoanFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.update_button -> {
                    viewModel.conditionsRequest()
                    hideSoftKeyboard()
                    true
                }

                R.id.help_button -> {
                    hideSoftKeyboard()
                    showHelpDialog()
                    true
                }

                else -> false
            }
        }

        notNullBinding.newLoanFragmentToolbar.setNavigationOnClickListener {
            hideSoftKeyboard()
            val bundle = bundleOf(LoansListFragment.APP_IS_RUNNING_ARGUMENT_KEY to true)
            findNavController().navigate(R.id.action_global_registrationFragment, bundle)
        }
    }

    private fun setUIStateObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewLoanUIState.Initializing -> {
                    setNormalScreenState()
                    viewModel.conditionsRequest()
                    val firstEntry = arguments?.getBoolean(FIRST_ENTRY_KEY) ?: false
                    if (firstEntry) {
                        showHelpDialog()
                    }
                }

                is NewLoanUIState.Loading -> {
                    setLoadingScreenState()
                }

                is NewLoanUIState.Success -> {
                    setNormalScreenState()
                    if (state.conditions != null) {
                        showConditions(state.conditions)
                    } else {
                        showSuccessfulResultLayout()
                    }
                }

                is NewLoanUIState.Error -> {
                    setNormalScreenState()
                    showErrorText(state.message)
                }
            }
        }
    }

    private fun showHelpDialog() {
        val imageArray = IntArray(4)
        imageArray[0] = R.drawable.ic_loan
        imageArray[1] = R.drawable.ic_update
        imageArray[2] = R.drawable.ic_list
        imageArray[3] = R.drawable.ic_account_circle

        val descriptionArray = IntArray(4)
        descriptionArray[0] = R.string.new_loan_help
        descriptionArray[1] = R.string.new_loan_help_update
        descriptionArray[2] = R.string.new_loan_help_to_list
        descriptionArray[3] = R.string.account_icon_help

        helpDialog.arguments = bundleOf(
            HelpDialogFragment.IMAGE_ID to imageArray,
            HelpDialogFragment.DESCRIPTION_ID to descriptionArray,
        )
        helpDialog.show(parentFragmentManager, HelpDialogFragment.TAG)
    }


    private fun showConditions(conditions: LoanConditions) {
        notNullBinding.apply {
            loanConditions = conditions
            amountTitleTextView.text = requireContext().getString(
                R.string.amount_title, conditions.maxAmount
            )
            amountCondition.text = requireContext().getString(
                R.string.max_amount, conditions.maxAmount
            )
            percentCondition.text = requireContext().getString(
                R.string.percent2, conditions.percent.toString()
            )
            periodCondition.text = requireContext().getString(
                R.string.period, conditions.period.toString()
            )
        }
    }

    private fun hideSoftKeyboard() {
        val imm =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setNormalScreenState() {
        notNullBinding.apply {
            contentLayout.visibility = View.VISIBLE
            successfulResultLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            newLoanFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = true
            errorTextView.isVisible = false
        }
    }

    private fun setLoadingScreenState() {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            successfulResultLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            newLoanFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = false
        }
    }

    private fun showSuccessfulResultLayout() {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            successfulResultLayout.visibility = View.VISIBLE
            successfulResultImage.setImageResource(R.drawable.ic_success)
            successfulResultText.text =
                requireContext().getString(R.string.successful_new_loan_request)
            hintTextView.animationBlinking()
            loadingProgressBar.visibility = View.GONE
            newLoanFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = false
        }
    }

    private fun showErrorText(message: String) {
        notNullBinding.errorTextView.text = message
        notNullBinding.errorTextView.isVisible = true
    }

    private fun fieldsNotBlank(): Boolean {
        notNullBinding.apply {
            return (amountTextView.text.isNotBlank() && firstNameTextView.text.isNotBlank()
                    && lastNameTextView.text.isNotBlank() && phoneNumberTextView.text.isNotBlank())
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val FIRST_ENTRY_KEY = "first entry"
        const val MAX_AMOUNT_CONDITION = "max amount"
        const val PERCENT_CONDITION = "percent"
        const val PERIOD_CONDITION = "period"
    }
}
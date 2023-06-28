package com.example.chernykhhomework.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.LoanApplication
import com.example.chernykhhomework.R
import com.example.chernykhhomework.domain.entity.Loan
import com.example.chernykhhomework.domain.entity.LoanState
import com.example.chernykhhomework.databinding.FragmentLoanBinding
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.viewmodel.LoanFragmentViewModel
import com.example.chernykhhomework.ui.fragments.dialogfragment.HelpDialogFragment
import javax.inject.Inject

class LoanFragment : Fragment() {

    private var binding: FragmentLoanBinding? = null
    private val notNullBinding: FragmentLoanBinding
        get() = binding!!

    private val viewModel: LoanFragmentViewModel by viewModels {
        (activity?.application as LoanApplication).appComponent.viewModelsFactory()
    }

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
        binding = FragmentLoanBinding.inflate(inflater, container, false)
        return notNullBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIStateObserver()
        setOnItemMenuClickListeners()
    }

    private fun setOnItemMenuClickListeners() {
        notNullBinding.loanFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.help_button -> {
                    showHelpDialog()
                    true
                }

                else -> false
            }
        }

        notNullBinding.loanFragmentToolbar.setNavigationOnClickListener {
            val bundle = bundleOf(LoansListFragment.APP_IS_RUNNING_ARGUMENT_KEY to true)
            findNavController().navigate(R.id.action_global_registrationFragment, bundle)
        }
    }

    private fun setUIStateObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoanUIState.Initializing -> {
                    showContentLayout()
                    val id = arguments?.getInt(LoansListFragment.ID_ARGUMENT_KEY) ?: -1
                    viewModel.getLoanById(id)
                }

                is LoanUIState.Loading -> {
                    showProgressBar()
                }

                is LoanUIState.Success -> {
                    showContentLayout()
                    setLoanData(state.loan)
                }

                is LoanUIState.Error -> {
                    showErrorLayout(state.message)
                }
            }
        }
    }


    private fun setLoanData(loan: Loan) {
        notNullBinding.apply {
            val title = requireContext().getString(R.string.loan_number, loan.id)
            loanFragmentToolbar.title = title
            loanAmount.text =
                requireContext().getString(R.string.amount, loan.amount)
            val parts = loan.date.split(".")
            val dateAndTime = parts[0].split("T")

            loanDate.text =
                requireContext().getString(R.string.date, "${dateAndTime[0]} ${dateAndTime[1]}")
            loanFirstname.text =
                requireContext().getString(R.string.firstname, loan.firstName)
            loanLastname.text =
                requireContext().getString(R.string.lastname, loan.lastName)
            loanPercent.text =
                requireContext().getString(
                    R.string.percent2,
                    loan.percent.toString()
                )
            loanPeriod.text =
                requireContext().getString(
                    R.string.period,
                    loan.period.toString()
                )
            loanPhoneNumber.text =
                requireContext().getString(
                    R.string.phone_number,
                    loan.phoneNumber
                )
            loanState.text =
                requireContext().getString(R.string.state, loan.state)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (loan.state) {
                    LoanState.APPROVED -> {
                        loanState.setTextColor(notNullBinding.root.context.getColor(R.color.green))
                        loanInfoTextView.text = getString(R.string.approved_loan_info)
                    }

                    LoanState.REGISTERED -> {
                        loanState.setTextColor(notNullBinding.root.context.getColor(R.color.light_orange))
                        loanInfoTextView.text = getString(R.string.registered_loan_info)
                    }

                    LoanState.REJECTED -> {
                        loanState.setTextColor(notNullBinding.root.context.getColor(R.color.red))
                        loanInfoTextView.text = getString(R.string.rejected_loan_info)
                    }
                }
            }
        }
    }

    private fun showContentLayout() {
        notNullBinding.apply {
            contentLayout.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.GONE
        }
    }

    private fun showProgressBar() {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
        }
    }

    private fun showErrorLayout(errorMessage: String) {
        notNullBinding.apply {
            contentLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            errorText.text = errorMessage
        }
    }

    private fun showHelpDialog() {

        val imageArray = IntArray(2)
        imageArray[0] = R.drawable.ic_loan
        imageArray[1] = R.drawable.ic_account_circle

        val descriptionArray = IntArray(2)
        descriptionArray[0] = R.string.loan_help
        descriptionArray[1] = R.string.account_icon_help

        helpDialog.arguments = bundleOf(
            HelpDialogFragment.IMAGE_ID to imageArray,
            HelpDialogFragment.DESCRIPTION_ID to descriptionArray,
        )
        helpDialog.show(parentFragmentManager, HelpDialogFragment.TAG)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
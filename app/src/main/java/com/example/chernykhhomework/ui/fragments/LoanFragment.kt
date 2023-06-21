package com.example.chernykhhomework.ui.fragments

import android.content.DialogInterface
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
import com.example.chernykhhomework.databinding.FragmentLoanBinding
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.viewmodel.LoanFragmentViewModel
import com.example.chernykhhomework.ui.fragments.dialogfragment.NewLoanHelpDialogFragment

class LoanFragment : Fragment() {

    private var binding: FragmentLoanBinding? = null
    private val notNullBinding: FragmentLoanBinding
        get() = binding!!

    private val viewModel: LoanFragmentViewModel by viewModels {
        (activity?.application as LoanApplication).appComponent.viewModelsFactory()
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
        setHelpDialogListener()
    }

    private fun setOnItemMenuClickListeners() {
        notNullBinding.loanFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.help_button -> {
                    showHelpDialog(0)
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
                    notNullBinding.apply {
                        val title = requireContext().getString(R.string.loan_number, state.loan.id)
                        loanFragmentToolbar.title = title
                        loanAmount.text =
                            requireContext().getString(R.string.amount, state.loan.amount)
                        loanDate.text = requireContext().getString(R.string.date, state.loan.date)
                        loanFirstname.text =
                            requireContext().getString(R.string.firstname, state.loan.firstName)
                        loanLastname.text =
                            requireContext().getString(R.string.lastname, state.loan.lastName)
                        loanPercent.text =
                            requireContext().getString(
                                R.string.percent2,
                                state.loan.percent.toString()
                            )
                        loanPeriod.text =
                            requireContext().getString(
                                R.string.period,
                                state.loan.period.toString()
                            )
                        loanPhoneNumber.text =
                            requireContext().getString(
                                R.string.phone_number,
                                state.loan.phoneNumber
                            )
                        loanState.text =
                            requireContext().getString(R.string.state, state.loan.state)
                    }
                }

                is LoanUIState.Error -> {
                    showErrorLayout(state.message)
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

    private fun showHelpDialog(page: Int) {
        val imageArray = arrayOf(
            R.drawable.ic_loan,
            R.drawable.ic_account_circle
        )

        val descriptionArray = arrayOf(
            R.string.loan_help,
            R.string.account_icon_help
        )

        val helpDialog = NewLoanHelpDialogFragment()
        helpDialog.arguments = bundleOf(
            NewLoanHelpDialogFragment.PAGE_INDEX to page,
            NewLoanHelpDialogFragment.IMAGE_ID to imageArray[page],
            NewLoanHelpDialogFragment.DESCRIPTION_ID to descriptionArray[page],
            NewLoanHelpDialogFragment.MAX_PAGES to imageArray.size
        )
        helpDialog.show(requireActivity().supportFragmentManager, NewLoanHelpDialogFragment.TAG)
    }

    private fun setHelpDialogListener() {
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(
                NewLoanHelpDialogFragment.REQUEST_KEY,
                viewLifecycleOwner
            ) { _, result ->
                val which = result.getInt(NewLoanHelpDialogFragment.RESPONSE_KEY)
                val page = result.getInt(NewLoanHelpDialogFragment.PAGE_INDEX)
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> showHelpDialog(page + 1)
                    DialogInterface.BUTTON_NEGATIVE -> showHelpDialog(page - 1)
                }
            }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
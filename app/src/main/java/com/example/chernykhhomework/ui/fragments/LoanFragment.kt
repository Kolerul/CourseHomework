package com.example.chernykhhomework.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.App
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentLoanBinding
import com.example.chernykhhomework.presentation.uistate.LoanUIState
import com.example.chernykhhomework.presentation.viewmodel.LoanFragmentViewModel

class LoanFragment : Fragment() {

    private var binding: FragmentLoanBinding? = null
    private val notNullBinding: FragmentLoanBinding
        get() = binding!!

    private val viewModel: LoanFragmentViewModel by viewModels {
        (activity?.application as App).appComponent.viewModelsFactory()
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
                    //Какая то помощь
                    true
                }

                else -> {
                    false
                }
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
                    val id = arguments?.getInt(LoansListFragment.ID_ARGUMENT_KEY) ?: -1
                    viewModel.getLoanById(id)
                }

                is LoanUIState.Loading -> {
                    //Анимация
                }

                is LoanUIState.Success -> {
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
                    Log.d("LoanFragment", state.message)
                    //Ошибки
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
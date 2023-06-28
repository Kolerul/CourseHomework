package com.example.chernykhhomework.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.LoanApplication
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentLoansListBinding
import com.example.chernykhhomework.presentation.adapter.LoansAdapter
import com.example.chernykhhomework.presentation.entity.ErrorWrapper
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.viewmodel.LoansListFragmentViewModel
import com.example.chernykhhomework.ui.fragments.dialogfragment.HelpDialogFragment
import com.example.chernykhhomework.ui.fragments.dialogfragment.QuitDialogFragment
import java.lang.Error
import javax.inject.Inject

class LoansListFragment : Fragment() {

    private var binding: FragmentLoansListBinding? = null
    private val notNullBinding: FragmentLoansListBinding
        get() = binding!!

    private val viewModel: LoansListFragmentViewModel by viewModels {
        (activity?.application as LoanApplication).appComponent.viewModelsFactory()
    }

    @Inject
    lateinit var quitDialog: QuitDialogFragment

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
        binding = FragmentLoansListBinding.inflate(inflater, container, false)
        return notNullBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LoansAdapter() { loan ->
            val bundle = bundleOf(ID_ARGUMENT_KEY to loan.id)
            findNavController().navigate(R.id.action_loansListFragment_to_loanFragment, bundle)
        }
        notNullBinding.loanRecyclerView.adapter = adapter

        setUIStateObserver(adapter)
        setOnMenuItemListener()
        setQuitDialogListener()
        setOnBackPressedListener()
    }

    private fun setOnMenuItemListener() {
        notNullBinding.loanListFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.update_button -> {
                    viewModel.getLoansList()
                    true
                }

                R.id.add_button -> {
                    findNavController().navigate(R.id.action_loansListFragment_to_newLoanFragment)
                    true
                }

                R.id.help_button -> {
                    showHelpDialog()
                    true
                }

                else -> false
            }

        }

        notNullBinding.loanListFragmentToolbar.setNavigationOnClickListener {
            val bundle = bundleOf(APP_IS_RUNNING_ARGUMENT_KEY to true)
            findNavController().navigate(R.id.action_global_registrationFragment, bundle)
        }
    }

    private fun setUIStateObserver(adapter: LoansAdapter) {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoansListUIState.Initializing -> {
                    viewModel.getLoansList()
                    setNormalScreenState()
                }

                is LoansListUIState.Loading -> {
                    setLoadingScreenState()
                }

                is LoansListUIState.Success -> {
                    adapter.submitList(state.loansList)
                    setNormalScreenState()
                }

                is LoansListUIState.Error ->
                    setErrorScreenState(state.error)
            }
        }
    }

    private fun setOnBackPressedListener() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            showQuitDialog()
        }
    }

    private fun setNormalScreenState() {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.GONE
            loanListFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = true
        }
    }

    private fun setLoadingScreenState() {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
            loanListFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = false
        }
    }

    private fun setErrorScreenState(error: ErrorWrapper) {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            loanListFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = true
            errorText.text = requireContext().getString(
                error.errorCode,
                error.errorClass,
                error.errorMessage
            )
        }
    }

    private fun showQuitDialog() {
        quitDialog.show(parentFragmentManager, QuitDialogFragment.TAG)
    }

    private fun setQuitDialogListener() {
        requireActivity()
            .supportFragmentManager.setFragmentResultListener(
                QuitDialogFragment.REQUEST_KEY,
                viewLifecycleOwner
            ) { _, result ->
                val which = result.getInt(QuitDialogFragment.RESPONSE_KEY)
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    requireActivity().finish()
                }
            }
    }

    private fun showHelpDialog() {
        val imageArray = IntArray(4)
        imageArray[0] = R.drawable.ic_list
        imageArray[1] = R.drawable.ic_update
        imageArray[2] = R.drawable.ic_add
        imageArray[3] = R.drawable.ic_account_circle

        val descriptionArray = IntArray(4)
        descriptionArray[0] = R.string.loans_list_help
        descriptionArray[1] = R.string.loans_list_help_update
        descriptionArray[2] = R.string.add_new_loan_help
        descriptionArray[3] = R.string.account_icon_help

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

    companion object {
        const val ID_ARGUMENT_KEY = "id"
        const val APP_IS_RUNNING_ARGUMENT_KEY = "app is running"
    }
}
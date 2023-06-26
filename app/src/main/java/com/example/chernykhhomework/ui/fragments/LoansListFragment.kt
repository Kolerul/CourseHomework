package com.example.chernykhhomework.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.LoanApplication
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentLoansListBinding
import com.example.chernykhhomework.presentation.LoansAdapter
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.viewmodel.LoansListFragmentViewModel
import com.example.chernykhhomework.ui.fragments.dialogfragment.HelpDialogFragment
import com.example.chernykhhomework.ui.fragments.dialogfragment.QuitDialogFragment
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
        setHelpDialogListener()
        setQuitDialogListener()
        setOnBackPressedListener()
        setOnButtonClickListener()
    }

    private fun setOnButtonClickListener() {
        notNullBinding.useCacheButton.setOnClickListener {
            viewModel.getLoansList(true)
        }
    }

    private fun setOnMenuItemListener() {
        notNullBinding.loanListFragmentToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.update_button -> {
                    viewModel.getLoansList(false)
                    true
                }

                R.id.add_button -> {
                    findNavController().navigate(R.id.action_loansListFragment_to_newLoanFragment)
                    true
                }

                R.id.help_button -> {
                    showHelpDialog(0)
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
                    viewModel.getLoansList(false)
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
                    setErrorScreenState(state.message)
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

    private fun setErrorScreenState(message: String) {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            loanListFragmentToolbar.menu.findItem(R.id.update_button).isEnabled = true
            errorText.text = message
        }
    }

    private fun showQuitDialog() {
        //val quitDialog = QuitDialogFragment()
        quitDialog.show(requireActivity().supportFragmentManager, QuitDialogFragment.TAG)
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

    private fun showHelpDialog(page: Int) {
        val imageArray = arrayOf(
            R.drawable.ic_list,
            R.drawable.ic_update,
            R.drawable.ic_add,
            R.drawable.ic_account_circle
        )

        val descriptionArray = arrayOf(
            R.string.loans_list_help,
            R.string.loans_list_help_update,
            R.string.add_new_loan_help,
            R.string.account_icon_help
        )

        val helpDialog = HelpDialogFragment()

        helpDialog.arguments = bundleOf(
            HelpDialogFragment.PAGE_INDEX to page,
            HelpDialogFragment.IMAGE_ID to imageArray[page],
            HelpDialogFragment.DESCRIPTION_ID to descriptionArray[page],
            HelpDialogFragment.MAX_PAGES to imageArray.size
        )
        helpDialog.show(requireActivity().supportFragmentManager, HelpDialogFragment.TAG)

    }

    private fun setHelpDialogListener() {
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(
                HelpDialogFragment.REQUEST_KEY,
                viewLifecycleOwner
            ) { _, result ->
                val which = result.getInt(HelpDialogFragment.RESPONSE_KEY)
                val page = result.getInt(HelpDialogFragment.PAGE_INDEX)
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

    companion object {
        const val ID_ARGUMENT_KEY = "id"
        const val APP_IS_RUNNING_ARGUMENT_KEY = "app is running"
    }
}
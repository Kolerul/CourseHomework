package com.example.chernykhhomework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.App
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentLoansListBinding
import com.example.chernykhhomework.presentation.LoansAdapter
import com.example.chernykhhomework.presentation.uistate.LoansListUIState
import com.example.chernykhhomework.presentation.viewmodel.LoansListFragmentViewModel

class LoansListFragment : Fragment() {

    private var binding: FragmentLoansListBinding? = null
    private val notNullBinding: FragmentLoansListBinding
        get() = binding!!

    private val viewModel: LoansListFragmentViewModel by viewModels {
        (activity?.application as App).appComponent.viewModelsFactory()
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
                    showRecyclerView()
                }

                is LoansListUIState.Loading -> {
                    showProgressBar()
                }

                is LoansListUIState.Success -> {
                    adapter.submitList(state.loansList)
                    showRecyclerView()
                }

                is LoansListUIState.Error ->
                    showErrorLayout(state.message)
            }
        }
    }

    private fun showRecyclerView() {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.GONE
        }
    }

    private fun showProgressBar() {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
        }
    }

    private fun showErrorLayout(message: String) {
        notNullBinding.apply {
            loanRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            errorText.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val ID_ARGUMENT_KEY = "id"
        const val APP_IS_RUNNING_ARGUMENT_KEY = "app is running"
    }
}
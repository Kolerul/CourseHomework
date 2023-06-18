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
            val bundle = bundleOf(ID_KEY to loan.id)
            findNavController().navigate(R.id.action_loansListFragment_to_loanFragment, bundle)
        }
        notNullBinding.loanRecyclerView.adapter = adapter
        setUIStateObserver(adapter)

    }

    private fun setUIStateObserver(adapter: LoansAdapter) {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoansListUIState.Initializing -> {
                    viewModel.getLoansList()
                }

                is LoansListUIState.Loading -> {
                    // Анимация загрузки
                }

                is LoansListUIState.Success -> {
                    adapter.submitList(state.loansList)
                }

                is LoansListUIState.Error ->
                    Log.d("LoansListFragment", state.message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val ID_KEY = "id"
    }
}
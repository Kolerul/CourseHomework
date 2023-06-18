package com.example.chernykhhomework.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.App
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentRegistrationBinding
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import com.example.chernykhhomework.presentation.viewmodel.RegistrationFragmentViewModel

class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null
    private val notNullBinding: FragmentRegistrationBinding
        get() = binding!!

    private val viewModel: RegistrationFragmentViewModel by viewModels {
        (activity?.application as App).appComponent.viewModelsFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return notNullBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUIStateObserver()
        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        notNullBinding.apply {
            registrationButton.setOnClickListener {
                if (nameEditText.text.isNotBlank() && passwordEditText.text.isNotBlank()) {
                    viewModel.register(
                        nameEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                    warningTextView.visibility = View.INVISIBLE
                } else {
                    warningTextView.visibility = View.VISIBLE
                }
            }

            logInButton.setOnClickListener {
                if (nameEditText.text.isNotBlank() && passwordEditText.text.isNotBlank()) {
                    viewModel.logIn(nameEditText.text.toString(), passwordEditText.text.toString())
                    warningTextView.visibility = View.INVISIBLE
                } else {
                    warningTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUIStateObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterUIState.Initializing -> {
                    //Шото надо придумать
                }

                is RegisterUIState.Loading -> {
                    //Шото надо реализовать
                }

                is RegisterUIState.Success -> {
                    findNavController().navigate(R.id.action_registrationFragment_to_newLoanFragment)
                }

                is RegisterUIState.Error -> {
                    notNullBinding.warningTextView.isVisible = true
                    notNullBinding.warningTextView.text = state.message
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
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
                    showContentLinearLayout()
                    notNullBinding.warningTextView.isVisible = false
                    val appIsRunning =
                        arguments?.getBoolean(LoansListFragment.APP_IS_RUNNING_ARGUMENT_KEY)
                            ?: false
                    if (!appIsRunning) {
                        viewModel.autoLogIn()
                    }
                }

                is RegisterUIState.Loading -> {
                    notNullBinding.warningTextView.isVisible = false
                    showLoadingProgressBar()
                }

                is RegisterUIState.Success -> {
                    showContentLinearLayout()
                    if (state.user != null) {
                        showWelcomeTextView(state.user.name)
                        if (state.firstEntry) {
                            findNavController().navigate(R.id.action_registrationFragment_to_newLoanFragment)
                        } else {
                            findNavController().navigate(R.id.action_registrationFragment_to_loansListFragment)
                        }
                    }
                }

                is RegisterUIState.Error -> {
                    showContentLinearLayout()
                    notNullBinding.apply {
                        warningTextView.isVisible = true
                        warningTextView.text = state.message
                    }
                }

            }
        }
    }

    private fun showContentLinearLayout() {
        notNullBinding.apply {
            contentLinearLayout.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.GONE
            welcomeTextView.visibility = View.GONE
        }
    }

    private fun showLoadingProgressBar() {
        notNullBinding.apply {
            contentLinearLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            welcomeTextView.visibility = View.GONE
        }
    }

    private fun showWelcomeTextView(name: String) {
        notNullBinding.apply {
            contentLinearLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            welcomeTextView.visibility = View.VISIBLE
            notNullBinding.welcomeTextView.text = requireContext().getString(R.string.welcome, name)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
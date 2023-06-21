package com.example.chernykhhomework.ui.fragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chernykhhomework.LoanApplication
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentRegistrationBinding
import com.example.chernykhhomework.presentation.uistate.RegisterUIState
import com.example.chernykhhomework.presentation.viewmodel.RegistrationFragmentViewModel


class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null
    private val notNullBinding: FragmentRegistrationBinding
        get() = binding!!

    private val viewModel: RegistrationFragmentViewModel by viewModels {
        (activity?.application as LoanApplication).appComponent.viewModelsFactory()
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
                checkFieldsNotBlankAndPerformLogInAction {
                    viewModel.register(
                        name = nameEditText.text.toString(),
                        password = passwordEditText.text.toString()
                    )
                }
            }

            logInButton.setOnClickListener {
                checkFieldsNotBlankAndPerformLogInAction {
                    viewModel.logIn(
                        name = nameEditText.text.toString(),
                        password = passwordEditText.text.toString()
                    )
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
                        if (state.firstEntry) {
                            val bundle = bundleOf(NewLoanFragment.FIRST_ENTRY_KEY to true)
                            showWelcomeAnimationAndPerformAction(state.user.name) {
                                findNavController()
                                    .navigate(
                                        R.id.action_registrationFragment_to_newLoanFragment,
                                        bundle
                                    )
                            }
                        } else {
                            showWelcomeAnimationAndPerformAction(state.user.name) {
                                findNavController()
                                    .navigate(R.id.action_registrationFragment_to_loansListFragment)
                            }
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

    private fun checkFieldsNotBlankAndPerformLogInAction(action: () -> Unit) {
        notNullBinding.apply {
            if (nameEditText.text.isNotBlank() && passwordEditText.text.isNotBlank()) {
                action.invoke()
                warningTextView.visibility = View.INVISIBLE
            } else {
                warningTextView.visibility = View.VISIBLE
                warningTextView.text = requireContext().getString(R.string.empty_fields_warning)
            }
            hideSoftKeyboard()
        }
    }

    private fun hideSoftKeyboard() {
        val imm =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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

    private fun showWelcomeAnimationAndPerformAction(name: String, endAction: () -> Unit) {
        notNullBinding.apply {
            contentLinearLayout.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            welcomeTextView.visibility = View.VISIBLE
            notNullBinding.welcomeTextView.text = requireContext().getString(R.string.welcome, name)
            notNullBinding.welcomeTextView.alpha = 0f
        }

        val welcomeAnimation = ObjectAnimator.ofFloat(
            notNullBinding.welcomeTextView, View.ALPHA, 1f
        ).apply {
            duration = 2000L
            doOnEnd {
                endAction.invoke()
            }
        }
        welcomeAnimation.start()
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
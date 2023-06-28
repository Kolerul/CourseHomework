package com.example.chernykhhomework.ui.fragments.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.chernykhhomework.R
import javax.inject.Inject

class QuitDialogFragment @Inject constructor() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    RESPONSE_KEY to which
                )
            )
            dismiss()
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.DialogFragmentStyle)
            .setPositiveButton(getString(R.string.exit), listener)
            .setNegativeButton(getString(R.string.cancel), listener)
            .setTitle(getString(R.string.quit_question))

        return dialog.create()
    }

    companion object {
        val TAG = QuitDialogFragment::class.java.simpleName
        const val REQUEST_KEY = "quit request key"
        const val RESPONSE_KEY = "quit response key"
    }
}
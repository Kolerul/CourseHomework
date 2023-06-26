package com.example.chernykhhomework.ui.fragments.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentHelpDialogBinding
import javax.inject.Inject

class HelpDialogFragment @Inject constructor() : DialogFragment() {

    private var binding: FragmentHelpDialogBinding? = null
    private val notNullBinding: FragmentHelpDialogBinding
        get() = binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentHelpDialogBinding.inflate(layoutInflater)

        val pageIndex = arguments?.getInt(PAGE_INDEX) ?: 0
        val maxPages = arguments?.getInt(MAX_PAGES) ?: 0
        val imageId = arguments?.getInt(IMAGE_ID) ?: 0
        val descriptionId = arguments?.getInt(DESCRIPTION_ID) ?: 0

        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    RESPONSE_KEY to which,
                    PAGE_INDEX to pageIndex
                )
            )
            dismiss()
        }

        notNullBinding.apply {
            helpImage.setImageResource(imageId)
            helpText.text = getString(descriptionId)
            helpPageCounter.text = getString(R.string.page_counter, (pageIndex + 1), maxPages)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.help))
            .setView(notNullBinding.root)
            .setNeutralButton(getString(R.string.close), listener)



        if (pageIndex > 0)
            dialog.setNegativeButton(getString(R.string.previous), listener)

        if (pageIndex < maxPages - 1)
            dialog.setPositiveButton(getString(R.string.next), listener)

        return dialog.create()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        //val TAG = HelpDialogFragment::class.java.simpleName
        val TAG = this.toString()
        const val PAGE_INDEX = "page index"
        const val MAX_PAGES = "max pages"
        const val IMAGE_ID = "image id"
        const val DESCRIPTION_ID = "description id"
        const val REQUEST_KEY = "request key"
        const val RESPONSE_KEY = "response key"
    }
}
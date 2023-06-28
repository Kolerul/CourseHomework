package com.example.chernykhhomework.ui.fragments.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.chernykhhomework.R
import com.example.chernykhhomework.databinding.FragmentHelpDialogBinding
import com.example.chernykhhomework.presentation.adapter.HelpAdapter
import com.example.chernykhhomework.presentation.entity.HelpPage
import javax.inject.Inject

class HelpDialogFragment @Inject constructor() : DialogFragment() {

    private var binding: FragmentHelpDialogBinding? = null
    private val notNullBinding: FragmentHelpDialogBinding
        get() = binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentHelpDialogBinding.inflate(layoutInflater)


        val imageIdArray = arguments?.getIntArray(IMAGE_ID) ?: IntArray(0)
        val descriptionIdArray = arguments?.getIntArray(DESCRIPTION_ID) ?: IntArray(0)

        val helpPageList = buildList {
            for (index in imageIdArray.indices) {
                add(
                    HelpPage(imageIdArray[index], descriptionIdArray[index])
                )
            }
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(notNullBinding.helpRecyclerView)
        val adapter = HelpAdapter()
        adapter.submitList(helpPageList)
        notNullBinding.helpRecyclerView.adapter = adapter

        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    RESPONSE_KEY to which
                )
            )
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.DialogFragmentStyle)
            .setTitle(getString(R.string.help))
            .setView(notNullBinding.root)
            .setPositiveButton(getString(R.string.close), listener)

        return dialog.create()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG = HelpDialogFragment::class.java.simpleName
        const val IMAGE_ID = "image id"
        const val DESCRIPTION_ID = "description id"
        const val REQUEST_KEY = "request key"
        const val RESPONSE_KEY = "response key"
    }
}
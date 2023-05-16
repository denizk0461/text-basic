package com.denizd.textbasic.sheet

import android.os.Bundle
import android.view.View
import com.denizd.textbasic.R
import com.denizd.textbasic.databinding.SheetTextBinding
import com.denizd.textbasic.util.viewBinding

/**
 * Bottom sheet used for displaying any sort of text to the user.
 */
class TextSheet : AppSheet(R.layout.sheet_text) {

    // View binding
    private val binding: SheetTextBinding by viewBinding(SheetTextBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve headline for the window
        val header = arguments?.getString("header") ?: ""

        // Check whether an ID has passed to use LiveData
        val content = arguments?.getString("content") ?: ""

        binding.textHeader.text = header
        binding.textContent.text = content

        // Set up a simple X button to be able to close the sheet
        binding.buttonCancel.setOnClickListener {
            // Do nothing and dismiss the sheet
            dismiss()
        }
    }
}
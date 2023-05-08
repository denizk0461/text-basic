package com.denizd.textbasic.fragment

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.textbasic.QuoteAdapter
import com.denizd.textbasic.QuoteStorage
import com.denizd.textbasic.util.viewBinding
import com.denizd.textbasic.R
import com.denizd.textbasic.databinding.FragmentQuoteBinding
import com.google.android.material.transition.MaterialSharedAxis

class QuoteFragment : BaseFragment(R.layout.fragment_quote) {

    private lateinit var storage: QuoteStorage
    private lateinit var quoteAdapter: QuoteAdapter

    private val binding: FragmentQuoteBinding by viewBinding(FragmentQuoteBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quoteConstraintLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        storage = QuoteStorage(context)
        quoteAdapter = QuoteAdapter(storage.getAllQuotes())

        binding.quoteScroller.let { s ->
            s.layoutManager = LinearLayoutManager(context)
            s.adapter = quoteAdapter
        }

        binding.addFab.setOnClickListener {
            quoteAdapter.addNewQuote()
        }
    }

    private fun save() {
        storage.saveQuotes(quoteAdapter.getAllQuotes())
    }

    override fun onPause() {
        save()
        super.onPause()
    }
}
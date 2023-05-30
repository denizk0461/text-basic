package com.denizd.textbasic.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.denizd.textbasic.R
import com.denizd.textbasic.adapter.QuoteAdapter
import com.denizd.textbasic.adapter.RecyclerRowMoveCallback
import com.denizd.textbasic.databinding.FragmentQuoteBinding
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.util.showSnackBar
import com.denizd.textbasic.util.viewBinding

class QuoteFragment : BaseFragment(R.layout.fragment_quote), QuoteAdapter.OnDeleteListener {

    private lateinit var storage: QuoteStorage
    private lateinit var quoteAdapter: QuoteAdapter

    private val binding: FragmentQuoteBinding by viewBinding(FragmentQuoteBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = QuoteStorage.getInstance(context)

        if (storage.getAllQuotes().isEmpty()) {
            storage.noMigrationNecessary()
        }

        if (storage.needsMigration()) {
            storage.migrateEntries()
        }

        quoteAdapter = QuoteAdapter(storage.getAllQuotes(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        ItemTouchHelper(RecyclerRowMoveCallback(quoteAdapter))
            .attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = quoteAdapter

        // Set up scroll change listener to shrink and extend FAB accordingly
        binding.recyclerView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // Calculate the vertical scroll difference
            val dy = scrollY - oldScrollY
            if (dy > 0) {
                // If scrolling down, shrink the FAB
                binding.fabAddQuote.shrink()
            } else if (dy < 0) {
                // If scrolling up, extend the FAB
                binding.fabAddQuote.extend()
            }
        }

        binding.buttonSave.setOnClickListener {
            save()
            context.theme.showSnackBar(
                binding.coordinatorLayout,
                getString(R.string.quote_fragment_force_save),
            )
        }

        binding.fabAddQuote.setOnClickListener {
            quoteAdapter.addNewQuote()
            binding.recyclerView.smoothScrollToPosition(quoteAdapter.itemCount - 1)
        }
    }

    private fun save() {
        storage.saveQuotes(quoteAdapter.getAllQuotes())
    }

    override fun onPause() {
        save()
        super.onPause()
    }

    override fun onDelete(entry: String, index: Int) {
        context.theme.showSnackBar(
            view = binding.coordinatorLayout,
            text = getString(R.string.quote_fragment_snack_text),
            actionText = getString(R.string.quote_fragment_snack_undo),
        ) {
            quoteAdapter.addEntryAt(index, entry)
        }
    }
}
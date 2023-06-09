package com.denizd.textbasic.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denizd.textbasic.R
import com.google.android.material.button.MaterialButton
import java.util.Collections


class QuoteAdapter(
    quotes: List<String>,
    private val onDeleteListener: OnDeleteListener,
) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>(),
    RecyclerRowMoveCallback.RecyclerViewTouchHelperContract {

    private var mutableQuotes: MutableList<String> = quotes.toMutableList()

    class QuoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val quote: TextView = view.findViewById(R.id.edit_text_quote)
        val delete: MaterialButton = view.findViewById(R.id.delete_button)
    }

//    override fun getItemViewType(position: Int): Int {
//        return /*if (position == itemCount - 1) 1 else*/ 0
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.quote_entry, parent, false)
            return QuoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val currentItem = mutableQuotes[position]

        holder.quote.text = currentItem

        holder.quote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mutableQuotes[holder.layoutPosition] = p0.toString()
            }
        })

        holder.delete.setOnClickListener {
            onDeleteListener.onDelete(mutableQuotes[holder.layoutPosition], holder.layoutPosition)
            mutableQuotes.removeAt(holder.layoutPosition)
            notifyItemRemoved(holder.layoutPosition)
        }
    }

    override fun getItemCount(): Int = mutableQuotes.size

    fun addNewQuote() {
        mutableQuotes.add("")
        notifyItemInserted(mutableQuotes.size + 1)
    }

    fun addEntryAt(index: Int, entry: String) {
        mutableQuotes.add(index, entry)
        notifyItemInserted(index)
    }

    fun getAllQuotes(): Array<String> {
        return mutableQuotes.filter { s -> s.isNotBlank() }.toTypedArray()
    }

    override fun onRowMoved(from: Int, to: Int) {
        if (from < to) for (i in from until to) {
            Collections.swap(mutableQuotes, i, i + 1)
        } else for (i in from downTo to + 1) {
            Collections.swap(mutableQuotes, i, i - 1)
        }

        notifyItemMoved(from, to)
    }

    override fun onRowSelected(viewHolder: QuoteViewHolder) {
        // TODO
    }

    override fun onRowClear(viewHolder: QuoteViewHolder) {
        // TODO
    }

    interface OnDeleteListener {
        fun onDelete(entry: String, index: Int)
    }
}
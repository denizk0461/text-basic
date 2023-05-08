package com.denizd.textbasic

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuoteAdapter(quotes: List<String>) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    private var mutableQuotes: MutableList<String> = quotes.toMutableList()

    class QuoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val quote: TextView = view.findViewById(R.id.quote)
        val delete: ImageButton = view.findViewById(R.id.delete_button)
    }

    override fun getItemViewType(position: Int): Int {
        return /*if (position == itemCount - 1) 1 else*/ 0
    }

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
            mutableQuotes.removeAt(holder.layoutPosition)
            notifyItemRemoved(holder.layoutPosition)
        }
    }

    override fun getItemCount(): Int = mutableQuotes.size

    fun setQuotes(quotes: List<String>) {
        this.mutableQuotes = quotes.toMutableList()
        notifyDataSetChanged()
    }

    fun addNewQuote() {
        mutableQuotes.add("")
        notifyItemInserted(mutableQuotes.size + 1)
    }

    fun getAllQuotes(): Array<String> {
        return mutableQuotes.filter { s -> s.isNotBlank() }.toTypedArray()
    }
}
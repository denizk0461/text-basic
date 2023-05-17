package com.denizd.textbasic.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.denizd.textbasic.R

class DropdownAdapter(context: Context, items: List<String>)
    : ArrayAdapter<String>(context, R.layout.item_dropdown, items) {

    private val noOpFilter = object : Filter() {
        private val noOpResult = FilterResults()
        override fun performFiltering(constraint: CharSequence?) = noOpResult
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }

    override fun getFilter() = noOpFilter
}
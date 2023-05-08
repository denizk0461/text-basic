package com.denizd.textbasic.fragment

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var _context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun getContext(): Context = _context
}
package com.denizd.textbasic.fragment

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.denizd.textbasic.sheet.AppSheet

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var _context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun getContext(): Context = _context

    /**
     * Opens a bottom sheet. Sheet must be a subclass of [com.denizd.textbasic.sheet.AppSheet].
     *
     * @param sheet element that will be displayed
     */
    protected fun openBottomSheet(sheet: AppSheet) {
        sheet.show(childFragmentManager, sheet.javaClass.simpleName)
    }
}
package com.denizd.textbasic.fragment

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private lateinit var _context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun getContext(): Context = _context

//    protected fun RecyclerView.addFabScrollListener() {
//        addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                when {
//                    dy > 0 -> fab.hide()
//                    dy < 0 -> fab.show()
//                }
//            }
//        })
//    }
//
//    protected fun NestedScrollView.addFabScrollListener() {
//        setOnScrollChangeListener { _, _, dy, _, doy ->
//            when {
//                dy > doy -> fab.hide()
//                dy < doy -> fab.show()
//            }
//        }
//    }
}
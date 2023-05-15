package com.denizd.textbasic.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class RecyclerRowMoveCallback(
    private val touchHelperContract: RecyclerViewTouchHelperContract,
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        touchHelperContract.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return false
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int,
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            touchHelperContract.onRowSelected(viewHolder as QuoteAdapter.QuoteViewHolder)
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        touchHelperContract.onRowClear(viewHolder as QuoteAdapter.QuoteViewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    interface RecyclerViewTouchHelperContract {
        fun onRowMoved(from: Int, to: Int)
        fun onRowSelected(viewHolder: QuoteAdapter.QuoteViewHolder)
        fun onRowClear(viewHolder: QuoteAdapter.QuoteViewHolder)
    }
}
package com.denizd.textbasic.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialAutoCompleteTextView(context, attrs, defStyleAttr) {

    override fun getFreezesText(): Boolean {
        return false
    }
}
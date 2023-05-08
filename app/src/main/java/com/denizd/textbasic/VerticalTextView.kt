package com.denizd.textbasic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet

class VerticalTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyle) {

    private var _width = 0
    private var _height = 0
    private val _bounds: Rect = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // vise versa
        _height = measuredWidth
        _width = measuredHeight
        setMeasuredDimension(_width, _height)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(_width.toFloat(), _height.toFloat())
        canvas.rotate(-90f)
        val paint = paint
        paint.color = textColors.defaultColor
        val text = text()
        paint.getTextBounds(text, 0, text.length, _bounds)
        canvas.drawText(text, compoundPaddingLeft.toFloat(), (_bounds.height() - _width) / 2f, paint)
        canvas.restore()
    }

    private fun text(): String {
        return super.getText().toString()
    }
}
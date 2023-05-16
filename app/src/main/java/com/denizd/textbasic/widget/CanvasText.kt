package com.denizd.textbasic.widget

import android.content.Context
import android.graphics.*
import android.text.StaticLayout
import android.text.TextPaint
import com.denizd.textbasic.db.QuoteStorage
import com.denizd.textbasic.R

class CanvasText {

    companion object {
        fun drawText(context: Context, isPreview: Boolean = false, size: Pair<Int, Int>? = null): Bitmap {


            val previewSize = 20f

            val storage = QuoteStorage.getInstance(context)

            size?.let {
                storage.setSize(size)
            }

            val widgetSize = storage.getSize()
            val outlineSize = storage.getOutlineSizeNew().toFloat()

            val bmp = if (isPreview) {
                Bitmap.createBitmap(1024, 256, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(widgetSize.first * 3, widgetSize.second * 3, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(bmp)

            val quote = if (isPreview) {
                context.getString(R.string.example_text)
            } else if (storage.isOrderRandom()) {
                storage.getRandomQuote()
            } else {
                storage.getNextQuote()
            }.ifBlank { context.getString(R.string.info_add_text) }

            val colours = storage.getColours(context = context)

            val scaledTextSize =
                context.resources.displayMetrics.scaledDensity * if (/*isPreview*/false) previewSize else {
                    storage.getTextSize().toFloat()
                }
            val savedTypeface = storage.getTypeface()

            val widgetGravity = storage.getWidgetGravity()

            val paint = TextPaint().apply {
                style = Paint.Style.FILL
                color = colours.first
                textSize = scaledTextSize
                textAlign = when (widgetGravity) {
                    0, 3, 6 -> Paint.Align.LEFT
                    2, 5, 8 -> Paint.Align.RIGHT
                    else -> Paint.Align.CENTER
                }

                isAntiAlias = true
                isSubpixelText = false
                typeface = savedTypeface
            }

            val staticLayout = StaticLayout.Builder
                .obtain(quote, 0, quote.length, paint, if (isPreview) 1024 else widgetSize.first * 3).build()

            val boundsText = Rect()
            paint.getTextBounds(quote, 0, quote.length, boundsText)

            val padding = 16f
            val alignment = when (widgetGravity) {
                0 -> { // Align TOP LEFT
                    canvas.translate(boundsText.left.toFloat() + padding, 0f)
                    Paint.Align.LEFT
                }
                1 -> { // Align TOP CENTER
                    canvas.translate(canvas.width / 2f, 0f)
                    Paint.Align.CENTER
                }
                2 -> { // Align TOP RIGHT
                    canvas.translate(canvas.width.toFloat() - padding, 0f)
                    Paint.Align.RIGHT
                }
                3 -> { // Align CENTER LEFT
                    canvas.translate(boundsText.left.toFloat() + padding, (bmp.height.toFloat() / 2) - (staticLayout.height / 2))
                    Paint.Align.LEFT
                }
                // 4 is defined further below
                5 -> { // Align CENTER RIGHT
                    canvas.translate(canvas.width.toFloat() - padding, (bmp.height.toFloat() / 2) - (staticLayout.height / 2))
                    Paint.Align.RIGHT
                }
                6 -> { // Align BOTTOM LEFT
                    canvas.translate(boundsText.left.toFloat() + padding, bmp.height.toFloat() - staticLayout.height - padding)
                    Paint.Align.LEFT
                }
                7 -> { // Align BOTTOM CENTER
                    canvas.translate(canvas.width / 2f, bmp.height.toFloat() - staticLayout.height - padding)
                    Paint.Align.CENTER
                }
                8 -> { // Align BOTTOM RIGHT
                    canvas.translate(canvas.width.toFloat() - padding, bmp.height.toFloat() - staticLayout.height - padding)
                    Paint.Align.RIGHT
                }
                else -> { // 4; Align CENTER
                    canvas.translate(canvas.width / 2f, (bmp.height.toFloat() / 2) - (staticLayout.height / 2))
                    Paint.Align.CENTER
                }
            }

            when (val bgType = storage.getBackgroundType()) {
                0 -> { // background
                    val bgPaint = Paint().apply {
                        style = Paint.Style.FILL
                        color = colours.second
                    }
                    val rounding = 24f

                    val rect = when (widgetGravity) { // right
                        2, 5, 8 -> roundedRect(
                            -boundsText.right.toFloat() - 4f, 0f, 0f, staticLayout.height.toFloat(),
                            rounding, rounding
                        )
                        1, 4, 7 -> roundedRect( // middle
                            -(boundsText.width() / 2f), 0f,
                            boundsText.right.toFloat() / 2f, staticLayout.height.toFloat(),
                            rounding, rounding
                        )
                        else -> roundedRect( // left
                            0f, 0f, boundsText.right.toFloat() + 4f, staticLayout.height.toFloat(),
                            rounding, rounding
                        )
                    }

                    canvas.drawPath(rect, bgPaint)
                }
                1, 2 -> { // outline or shadow
                    val strokePaint = TextPaint().apply {
                        style = Paint.Style.STROKE
                        strokeWidth = outlineSize //if (isPreview) previewSize / 1.5f else
                        color = colours.second
                        textSize = scaledTextSize
                        textAlign = alignment

                        isAntiAlias = true
                        isSubpixelText = false
                        typeface = savedTypeface

                        if (bgType == 2) { // blur outline for shadow
                            maskFilter = BlurMaskFilter(if (outlineSize == 0f) 1f else outlineSize, BlurMaskFilter.Blur.NORMAL)
                        }
                    }
                    val strokeStaticLayout = StaticLayout.Builder
                        .obtain(quote, 0, quote.length, strokePaint, if (isPreview) 1024 else widgetSize.first * 3).build()
                    strokeStaticLayout.draw(canvas)
                }
            }

            staticLayout.draw(canvas)
            return bmp
        }

//        private fun calculateWidgetGravity(widgetGravity: Int, bitmap: Bitmap, boundsText: Rect, isPreview: Boolean) : Pair<Float, Float> {
//            return if (isPreview) {
//                Pair((bitmap.width - boundsText.width()) / 2f, (bitmap.height + boundsText.height()) / 2f)
//            } else when (widgetGravity) {
//                0 -> { // top left
//                    Pair(0f + boundsText.left, (boundsText.height() - boundsText.top).toFloat())
//                }
//                1 -> { // top center
//                    Pair(
//                        (bitmap.width - boundsText.width()) / 2f,
//                        (boundsText.height() - boundsText.top).toFloat()
//                    )
//                }
//                2 -> { // top right
//                    Pair(
//                        (bitmap.width - (boundsText.right)).toFloat(),
//                        (boundsText.height() - boundsText.top).toFloat()
//                    )
//                }
//                3 -> { // center left
//                    Pair(0f + boundsText.left, (bitmap.height + boundsText.height()) / 2f)
//                }
//                // 4 == else branch
//                5 -> { // center right
//                    Pair(
//                        (bitmap.width - (boundsText.right)).toFloat(),
//                        (bitmap.height + boundsText.height()) / 2f
//                    )
//                }
//                6 -> { // bottom left
//                    Pair(0f + boundsText.left, (bitmap.height - boundsText.bottom).toFloat())
//                }
//                7 -> { // bottom center
//                    Pair(
//                        (bitmap.width - boundsText.width()) / 2f,
//                        (bitmap.height - boundsText.bottom).toFloat()
//                    )
//                }
//                8 -> { // bottom right
//                    Pair(
//                        (bitmap.width - (boundsText.right)).toFloat(),
//                        (bitmap.height - boundsText.bottom).toFloat()
//                    )
//                }
//                else -> { // center center LIKE 4
//                    Pair(
//                        (bitmap.width - boundsText.width()) / 2f,
//                        (bitmap.height + boundsText.height()) / 2f
//                    )
//                }
//            }
//        }

        private fun roundedRect(
            left: Float, top: Float, right: Float, bottom: Float,
            roundingX: Float, roundingY: Float
        ): Path {
            var rx = roundingX
            var ry = roundingY
            val path = Path()
            if (rx < 0) rx = 0f
            if (ry < 0) ry = 0f
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) rx = width / 2
            if (ry > height / 2) ry = height / 2
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry
            path.moveTo(right, top + ry)
            path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
            path.rLineTo(-widthMinusCorners, 0f)
            path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
            path.rLineTo(0f, heightMinusCorners)

            path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
            path.rLineTo(widthMinusCorners, 0f)
            path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner

            path.rLineTo(0f, -heightMinusCorners)
            path.close() //Given close, last lineto can be removed.
            return path
        }
    }
}
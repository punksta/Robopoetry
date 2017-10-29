package com.punksta.apps.robopoetry

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.punksta.apps.robopoetry.ext.pxFromDp


/**
 * Created by stanislav on 10/28/17.
 */

/**
 *    Draws corners like these
 *
 *     |
 *     | |
 *     | | |_
 *     |  _ _ _
 *      _ _ _ _ _ _
 */
class BottomLeftCornerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     *  [(x, y), (x1, y1)]
     *
     *  x - x offset from left where corner begins
     *  y - y offset from bottom where corners ends
     */
    private var corners : List<Pair<Float, Float>> = emptyList()


    private val paint = Paint()

    init {


        when (attrs) {
            null -> {
                paint.color = Color.WHITE
                paint.strokeWidth = 20f
            }
            else -> {
                val a = context.theme.obtainStyledAttributes(
                        attrs,
                        R.styleable.BottomLeftCornerView,
                        0, 0
                )

                paint.color = a.getColor(R.styleable.BottomLeftCornerView_color, Color.WHITE);
                paint.strokeWidth = a.getDimension(R.styleable.BottomLeftCornerView_strokeWidth, 1f);

                corners = (a.getString(R.styleable.BottomLeftCornerView_data) ?: "")
                        .filter { it != ' ' }
                        .split(";")
                        .map { it.split(",") }
                        .map { pxFromDp(it[0].toFloat()) to pxFromDp(it[1].toFloat()) }

                a.recycle()
            }
        }
    }

    fun setCorners(corners: List<Pair<Float, Float>>) {
        this.corners = corners
        invalidate()
    }

    fun setCornderProps(color: Int = paint.color, width: Float = paint.strokeWidth) {
        paint.color = color
        paint.strokeWidth = width
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val circleRadius = paint.strokeWidth / 2

        corners.forEach { (startX, bottomMargin) ->
            val endY = this.height - bottomMargin
            val endX = this.width.toFloat()
            canvas.drawLine(startX, 0f, startX, endY, paint)
            canvas.drawLine(startX, endY, endX, endY, paint)
            canvas.drawCircle(startX, endY, circleRadius, paint)
        }
    }
}
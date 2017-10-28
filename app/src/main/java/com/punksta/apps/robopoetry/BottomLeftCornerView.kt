package com.punksta.apps.robopoetry

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

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
     *  x - percent of view width where corner begins
     *  y - percent of view height where corners ends
     */
    private var corners : List<Pair<Float, Float>> = listOf(0.5f to 0.5f)


    private val paint = Paint()

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = 20f
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

        corners.forEach { (startXPercent, endYPercent) ->
            val startX = this.width * startXPercent
            val endY = this.height * endYPercent
            val endX = this.width
            canvas.drawLine(startX, 0f, startX, endY, paint)
            canvas.drawLine(startX, endY, endX.toFloat(), endY, paint)
            canvas.drawCircle(startX, endY, paint.strokeWidth / 2, paint)
        }
    }
}
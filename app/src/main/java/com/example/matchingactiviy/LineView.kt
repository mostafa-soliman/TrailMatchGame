package com.example.matchingactiviy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()

    var startCoordinates: Pair<Float, Float>? = null
    var endCoordinates: Pair<Float, Float>? = null
    var lineList = mutableListOf<LineList>()
    init {
        paint.apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        startCoordinates?.let { start ->
            endCoordinates?.let { end ->
                canvas.drawLine(start.first, start.second, end.first, end.second, paint)
            }
        }
        for ( line in lineList){
            line.startCoordinates?.let { start ->
                line.endCoordinates?.let { end ->
                    canvas.drawLine(start.first, start.second, end.first, end.second, paint)
                }
            }
        }

    }

    data class LineList(  var startCoordinates: Pair<Float, Float>?, var endCoordinates: Pair<Float, Float>? )
}
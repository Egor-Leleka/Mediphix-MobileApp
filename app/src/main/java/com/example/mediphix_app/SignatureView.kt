package com.example.mediphix_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class SignatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var path = Path()
    private var paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.mediphix_darkblue)
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5f
    }
    private var bitmap: Bitmap? = null
    private lateinit var canvas: Canvas

    override fun onDraw(canvas: Canvas) {
        // Fill the canvas with a white color
        canvas.drawColor(android.graphics.Color.WHITE)

        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
            MotionEvent.ACTION_UP -> {}
        }
        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        invalidate()
    }

    fun getSignatureBitmap(): Bitmap {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        canvas = Canvas(bitmap!!)
        draw(canvas)
        return bitmap!!
    }
}
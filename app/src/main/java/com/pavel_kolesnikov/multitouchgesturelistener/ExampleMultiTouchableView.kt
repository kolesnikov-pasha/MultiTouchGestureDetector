package com.pavel_kolesnikov.multitouchgesturelistener

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.pavel_kolesnikov.mutlitouch_gesture_detector.MultiTouchGestureDetector
import com.pavel_kolesnikov.mutlitouch_gesture_detector.MultiTouchGestureListener

class ExampleMultiTouchableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), MultiTouchGestureListener {
    private val multiTouchGestureDetector = MultiTouchGestureDetector(this)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        multiTouchGestureDetector.onTouch(this, event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.setMatrix(multiTouchGestureDetector.allGesturesMatrix)
        super.onDraw(canvas)
    }

    override fun onGestureStart(detector: MultiTouchGestureDetector) {}

    override fun onGesture(detector: MultiTouchGestureDetector) {
        invalidate()
    }

    override fun onGestureEnd(detector: MultiTouchGestureDetector) {}

}
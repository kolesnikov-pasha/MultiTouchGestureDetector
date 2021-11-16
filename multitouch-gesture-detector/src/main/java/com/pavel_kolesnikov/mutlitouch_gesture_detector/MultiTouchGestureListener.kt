package com.pavel_kolesnikov.mutlitouch_gesture_detector

interface MultiTouchGestureListener {
    fun onGestureStart(detector: MultiTouchGestureDetector)
    fun onGesture(detector: MultiTouchGestureDetector)
    fun onGestureEnd(detector: MultiTouchGestureDetector)
}
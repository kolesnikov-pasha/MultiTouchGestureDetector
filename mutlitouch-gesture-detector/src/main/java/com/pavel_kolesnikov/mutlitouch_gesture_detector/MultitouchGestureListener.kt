package com.pavel_kolesnikov.mutlitouch_gesture_detector

interface MultitouchGestureListener {
    fun onGestureStart(detector: MultitouchGestureDetector)
    fun onGesture(detector: MultitouchGestureDetector)
    fun onGestureEnd(detector: MultitouchGestureDetector)
}
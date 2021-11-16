package com.pavel_kolesnikov.mutlitouch_gesture_detector

import android.graphics.Matrix
import android.view.MotionEvent
import java.lang.IllegalStateException

internal class GestureSession {
    var isActive = false
    val gestureMatrix = Matrix()
    val activePointersCount
        get() = pointerIds.size
    val pointerIds: List<Int>
        get() = mutablePointerIds
    private val mutablePointerIds = mutableListOf<Int>()
    val startCoordinates = mutableMapOf<Int, Coordinates>()
    private val currentCoordinates = mutableMapOf<Int, Coordinates>()

    fun start(startCoordinates: Map<Int, Coordinates>) {
        reset()
        isActive = true
        for ((pointerId, coordinates) in startCoordinates.entries) {
            this.startCoordinates[pointerId] = coordinates
            currentCoordinates[pointerId] = coordinates
            mutablePointerIds.add(pointerId)
        }
    }

    fun update(event: MotionEvent) {
        if (!isActive) {
            throw IllegalStateException("Cannot update not active gesture!")
        }
        for (pointerId in pointerIds) {
            if (event.findPointerIndex(pointerId) == MotionEvent.INVALID_POINTER_ID) {
                throw IllegalStateException("Event doesn't contain pointer $pointerId!")
            }
            currentCoordinates[pointerId] = GestureUtils.getXY(event, pointerId)
        }
        if (pointerIds.size > 1) {
            updateMultiTouchMatrix()
        } else {
            updateSingleTouchMatrix()
        }
    }

    private fun updateMultiTouchMatrix() {
        val firstPointerId = pointerIds[0]
        val secondPointerId = pointerIds[1]

        val old1 = startCoordinates[firstPointerId] ?: return
        val old2 = startCoordinates[secondPointerId] ?: return
        val new1 = currentCoordinates[firstPointerId] ?: return
        val new2 = currentCoordinates[secondPointerId] ?: return

        val scale = GestureUtils.getScale(old1, old2, new1, new2)
        val degrees =
            GestureUtils.getAngleDegrees(old1, old2, new1, new2)

        gestureMatrix.reset()
        gestureMatrix.postTranslate(new1.x - old1.x, new1.y - old1.y)
        gestureMatrix.postRotate(degrees, new1.x, new1.y)
        gestureMatrix.postScale(scale, scale, new1.x, new1.y)
    }

    private fun updateSingleTouchMatrix() {
        val pointerIds = startCoordinates.keys.toList()
        val firstPointerId = pointerIds[0]

        val old1 = startCoordinates[firstPointerId] ?: return
        val new1 = currentCoordinates[firstPointerId] ?: return

        gestureMatrix.reset()
        gestureMatrix.postTranslate(new1.x - old1.x, new1.y - old1.y)
    }

    fun stop() {
        isActive = false
    }

    private fun reset() {
        isActive = false
        gestureMatrix.reset()
        startCoordinates.clear()
        currentCoordinates.clear()
        mutablePointerIds.clear()
    }
}
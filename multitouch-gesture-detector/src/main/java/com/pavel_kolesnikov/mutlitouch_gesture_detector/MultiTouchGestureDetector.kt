package com.pavel_kolesnikov.mutlitouch_gesture_detector

import android.graphics.Matrix
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class MultiTouchGestureDetector(
    private val listener: MultiTouchGestureListener
) : View.OnTouchListener {

    private val session = GestureSession()
    private val gestureCurrentCoords = mutableMapOf<Int, Coordinates>()
    val gestureMatrix = Matrix()
    get() {
        field.reset()
        field.postConcat(session.gestureMatrix)
        return field
    }
    val allGesturesMatrix = Matrix()
    get() {
        field.reset()
        field.postConcat(fullTransformMatrix)
        field.postConcat(gestureMatrix)
        return field
    }
    private val fullTransformMatrix = Matrix()

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        val actionIndex = event.actionIndex
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = event.getPointerId(actionIndex)
                gestureCurrentCoords[pointerId] = GestureUtils.getXY(event, pointerId)
                updateCurrentCoordinates(event)
                onActionDown()
            }
            MotionEvent.ACTION_MOVE -> {
                updateCurrentCoordinates(event)
                if (session.isActive) {
                    session.update(event)
                    listener.onGesture(this)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = event.getPointerId(actionIndex)
                gestureCurrentCoords.remove(pointerId)
                updateCurrentCoordinates(event)
                onActionUp()
            }
        }
        return true
    }

    private fun updateCurrentCoordinates(event: MotionEvent) {
        for (pointerId in gestureCurrentCoords.keys) {
            gestureCurrentCoords[pointerId] = GestureUtils.getXY(event, pointerId)
        }
    }

    private fun onActionDown() {
        if (session.isActive && session.activePointersCount >= 2) {
            return
        }
        if (session.isActive) {
            listener.onGestureEnd(this)
            fullTransformMatrix.postConcat(gestureMatrix)
            session.stop()
        }
        session.start(gestureCurrentCoords)
        listener.onGestureStart(this)
    }

    private fun onActionUp() {
        if (!session.isActive) {
            return
        }
        val currentPointerIds = gestureCurrentCoords.keys.toList()
        for (pointerId in session.pointerIds) {
            if (!currentPointerIds.contains(pointerId)) {
                listener.onGestureEnd(this)
                fullTransformMatrix.postConcat(gestureMatrix)
                session.stop()
                break
            }
        }
        if (!session.isActive) {
            val newPointerIds = currentPointerIds.subList(0, min(2, currentPointerIds.size))
            session.start(
                mapOf(
                    *newPointerIds.map { pointerId ->
                        pointerId to gestureCurrentCoords[pointerId]!!
                    }.toTypedArray()
                )
            )
            listener.onGestureStart(this)
        }
    }
}
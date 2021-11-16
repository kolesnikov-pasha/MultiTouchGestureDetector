package com.pavel_kolesnikov.mutlitouch_gesture_detector

import android.view.MotionEvent
import java.lang.IllegalArgumentException
import kotlin.math.*

object GestureUtils {
    private fun getVectorLength(from: Coordinates, to: Coordinates): Float {
        return sqrt((from.x - to.x).pow(2) + (from.y - to.y).pow(2))
    }

    private fun getVectorProduct(
        from1: Coordinates,
        to1: Coordinates,
        from2: Coordinates,
        to2: Coordinates
    ): Float {
        return (to1.x - from1.x) * (to2.y - from2.y) - (to2.x - from2.x) * (to1.y - from1.y)
    }

    private fun getScalarProduct(
        from1: Coordinates,
        to1: Coordinates,
        from2: Coordinates,
        to2: Coordinates
    ): Float {
        return (to1.x - from1.x) * (to2.x - from2.x) + (to2.y - from2.y) * (to1.y - from1.y)
    }

    fun getAngleDegrees(
        from1: Coordinates,
        to1: Coordinates,
        from2: Coordinates,
        to2: Coordinates
    ): Float {
        val len1 = getVectorLength(from1, to1)
        val len2 = getVectorLength(from2, to2)
        val vectorProduct = getVectorProduct(from1, to1, from2, to2)
        val scalarProduct = getScalarProduct(from1, to1, from2, to2)
        return (sign(vectorProduct) * 180 * acos((scalarProduct / (len1 * len2)).toDouble()) / PI).toFloat()
    }

    fun getScale(
        from1: Coordinates,
        to1: Coordinates,
        from2: Coordinates,
        to2: Coordinates
    ): Float {
        val len1 = getVectorLength(from1, to1)
        val len2 = getVectorLength(from2, to2)
        return len2 / len1
    }

    fun getXY(event: MotionEvent, pointerId: Int): Coordinates {
        val pointerIndex = event.findPointerIndex(pointerId)
        if (pointerIndex == MotionEvent.INVALID_POINTER_ID) {
            throw IllegalArgumentException("This pointer doesn't active")
        }
        return Coordinates(event.getX(pointerIndex), event.getY(pointerIndex))
    }
}
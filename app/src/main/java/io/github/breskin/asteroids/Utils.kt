package io.github.breskin.asteroids

import android.graphics.Paint
import android.graphics.PointF
import io.github.breskin.asteroids.controls.Vector
import kotlin.math.cos
import kotlin.math.sin

object Utils {
    fun rotatePoint(cx: Float, cy: Float, angle: Float, p: PointF) {
        val s = sin(angle)
        val c = cos(angle)

        p.x -= cx
        p.y -= cy

        val xnew = p.x * c - p.y * s
        val ynew = p.x * s + p.y * c

        p.x = xnew + cx
        p.y = ynew + cy
    }

    fun rotateVector(v: Vector, angle: Float): Vector {
        val s = sin(angle)
        val c = cos(angle)

        val x = v.x * c - v.y * s
        val y = v.x * s + v.y * c

        return Vector(x, y)
    }

    fun timeToString(time: Int): String {
        val builder = StringBuilder()

        val minutes = time / 60
        val seconds = time % 60

        if (minutes > 0) {
            if (minutes < 10)
                builder.append('0')

            builder.append(minutes)
            builder.append(':')
        } else {
            builder.append("00:")
        }

        if (seconds < 10)
            builder.append('0')
        builder.append(seconds)

        return builder.toString()
    }

    fun fitFontSize(paint: Paint, text: String, textSize: Float, maxWidth: Float) {
        paint.textSize = textSize

        while (paint.measureText(text) > maxWidth)
            paint.textSize--
    }
}
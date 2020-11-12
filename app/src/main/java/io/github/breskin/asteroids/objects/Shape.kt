package io.github.breskin.asteroids.objects

import android.graphics.PointF
import io.github.breskin.asteroids.Utils.rotatePoint
import io.github.breskin.asteroids.controls.Vector
import kotlin.math.*

interface Shape {
    val points: MutableList<PointF>

    fun getPoint(index: Int, rotation: Float, position: PointF, scale: Float = 1f): PointF {
        val point = PointF(points[index].x * scale + position.x, points[index].y * scale + position.y)
        rotatePoint(position.x, position.y, rotation, point)
        return point
    }

    fun containsPoint(point: PointF, rotation: Float, position: PointF): Boolean {
        var i = 0
        var j = points.size - 1
        var c = false

        while (i < points.size) {
            val pointI = getPoint(i, rotation, position)
            val pointJ = getPoint(j, rotation, position)

            if (((pointI.y > point.y) != (pointJ.y > point.y)) && (point.x < (pointJ.x - pointI.x) * (point.y - pointI.y) / (pointJ.y - pointI.y) + pointI.x))
                c = !c

            j = i++
        }

        return c
    }
}
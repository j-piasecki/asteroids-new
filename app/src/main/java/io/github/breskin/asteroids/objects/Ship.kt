package io.github.breskin.asteroids.objects

import android.graphics.*
import android.util.Log
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic


class Ship : Shape {
    private val paint = Paint()
    override val points: MutableList<PointF> = mutableListOf()

    private var shipLeft: Path = Path()
    private var shipRight: Path = Path()
    
    var rotation = 0f
    var scale = 1f
    var alpha = 255
    var position = PointF(Float.NaN, Float.NaN)

    private var _size = Float.NaN
    var size
        get() = _size
        set(value) {
            _size = value

            points.clear()
            points.add(PointF(0f, -size / 2))
            points.add(PointF(size / 2, size / 2))
            points.add(PointF(0f, size / 4))
            points.add(PointF(-size / 2, size / 2))

            shipLeft = Path()
            shipLeft.moveTo(1f, -size / 2)
            shipLeft.lineTo(1f, size / 4)
            shipLeft.lineTo(-size / 2 + 1, size / 2)
            shipLeft.close()

            shipRight = Path()
            shipRight.moveTo(0f, -size / 2)
            shipRight.lineTo(0f, size / 4)
            shipRight.lineTo(size / 2, size / 2)
            shipRight.close()
        }

    fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(position.x, position.y)
        canvas.rotate(rotation * 180f / Math.PI.toFloat())
        canvas.scale(scale, scale)

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(alpha, 255, 0, 0)
        canvas.drawPath(shipRight, paint)
        paint.color = Color.argb(alpha, 220, 0, 0)
        canvas.drawPath(shipLeft, paint)

        canvas.restore()
    }
}
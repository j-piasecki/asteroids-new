package io.github.breskin.asteroids.controls

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class Joystick {
    private var radius = 0f
    private var touchDown = false
    private var pointerId = -1

    var flat = false
    var origin: PointF = PointF(0f, 0f)

    private var centerPosition: PointF = PointF(0f, 0f)
    private var joystickPosition: PointF = PointF(0f, 0f)

    private val paint: Paint = Paint()

    val active: Boolean
        get() = touchDown

    init {
        paint.isAntiAlias = true
    }

    val vector: Vector
        get() {
            val v = Vector(joystickPosition.x - centerPosition.x, joystickPosition.y - centerPosition.y)
            val range: Float = if (flat) 1f else v.length / radius

            v.normalize()
            v.x = v.x * range
            v.y = v.y * range

            if (java.lang.Float.isNaN(v.x) || java.lang.Float.isNaN(v.y)) {
                v.x = 0f
                v.y = 0f
            }

            return v
        }

    val angle: Float
        get() {
            return if (!active) 0f else (-(atan2(
                joystickPosition.x - centerPosition.x.toDouble(),
                joystickPosition.y - centerPosition.y.toDouble()
            ) + Math.PI)).toFloat()
        }

    fun draw(canvas: Canvas) {
        var strokeWidth = radius * 0.03f
        if (strokeWidth < 3) strokeWidth = 3f

        if (touchDown) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = Color.WHITE
            canvas.drawCircle(centerPosition.x, centerPosition.y, radius, paint)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(joystickPosition.x, joystickPosition.y, radius * 0.25f, paint)
            paint.color = Color.argb(64, 255, 255, 255)
            canvas.drawCircle(centerPosition.x, centerPosition.y, radius, paint)
        } else {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            paint.color = Color.argb(32, 255, 255, 255)
            canvas.drawCircle(origin.x, origin.y, radius, paint)
        }
    }

    fun onTouchEvent(event: MotionEvent) {
        val currentPointer = event.getPointerId(event.actionIndex)
        var x: Float = event.getX(event.findPointerIndex(currentPointer))
        var y: Float = event.getY(event.findPointerIndex(currentPointer))

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->
                if (!touchDown && sqrt((origin.x - x) * (origin.x - x) + (origin.y - y) * (origin.y - y) * 0.4) <= radius * 1.5f) {
                    touchDown = true

                    joystickPosition.x = x
                    centerPosition.x = x
                    joystickPosition.y = y
                    centerPosition.y = y

                    pointerId = currentPointer
                }

            MotionEvent.ACTION_MOVE -> if (touchDown) {
                val pointerIndex = event.findPointerIndex(pointerId)
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)

                val tMove = sqrt(radius * radius / ((x - centerPosition.x.toDouble()).pow(2.0) + (y - centerPosition.y.toDouble()).pow(2.0))).toFloat()

                if (tMove < 1) {
                    joystickPosition.x = centerPosition.x + (x - centerPosition.x) * tMove
                    joystickPosition.y = centerPosition.y + (y - centerPosition.y) * tMove
                } else {
                    joystickPosition.x = x
                    joystickPosition.y = y
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP ->
                if (pointerId == currentPointer) {
                    reset()
                }
        }
    }

    fun reset() {
        touchDown = false
        pointerId = -1
        joystickPosition.x = centerPosition.x
        joystickPosition.y = centerPosition.y
    }

    fun init(r: Float, cx: Float, cy: Float) {
        radius = r
        origin.x = cx
        origin.y = cy
    }
}
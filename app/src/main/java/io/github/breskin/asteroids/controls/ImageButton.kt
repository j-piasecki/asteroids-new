package io.github.breskin.asteroids.controls

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import io.github.breskin.asteroids.GameView

class ImageButton(private val callback: () -> Unit) {
    private val paint = Paint()
    private var touchDown = false
    private var pointerId = -1

    var image: Bitmap? = null
    var position = PointF(0f, 0f)
    var size = 0f

    init {
        paint.isAntiAlias = true
    }

    fun update() {
        size = GameView.size * 0.1f
    }

    fun draw(canvas: Canvas) {
        paint.color = Color.argb(if (touchDown) 64 else 24, 255, 255, 255)
        canvas.drawRoundRect(position.x, position.y, position.x + size, position.y + size, size * 0.1f, size * 0.1f, paint)

        paint.color = Color.rgb(200, 200, 200)

        image?.let {
            canvas.drawBitmap(it, null, RectF(position.x + size * 0.2f, position.y + size * 0.2f, position.x + size * 0.8f, position.y + size * 0.8f), paint)
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val currentPointer = event.getPointerId(event.actionIndex)
        var x: Float = event.getX(event.findPointerIndex(currentPointer))
        var y: Float = event.getY(event.findPointerIndex(currentPointer))

        val size = GameView.size * 0.09f

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (!touchDown && x > position.x && y > position.y && x < position.x + size && y < position.y + size) {
                    touchDown = true
                    pointerId = currentPointer

                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchDown) {
                    val pointerIndex = event.findPointerIndex(pointerId)
                    x = event.getX(pointerIndex)
                    y = event.getY(pointerIndex)

                    if (x < position.x || y < position.y || x > position.x + size || y > position.y + size) {
                        touchDown = false
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (pointerId == currentPointer) {
                    if (touchDown) {
                        callback.invoke()
                    }

                    touchDown = false
                    pointerId = -1
                }
            }
        }

        return false
    }
}
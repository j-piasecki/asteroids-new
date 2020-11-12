package io.github.breskin.asteroids.controls

import android.graphics.*
import android.view.MotionEvent
import io.github.breskin.asteroids.GameView


class ToggleButton(private val callback: ((toggled: Boolean) -> Unit)? = null) {
    private val paint: Paint = Paint()
    private var touchDown = false

    var imageOn: Bitmap? = null
    var imageOff: Bitmap? = null

    var position: PointF = PointF(0f, 0f)
    var size = Float.NaN
    var toggled = true

    init {
        paint.isAntiAlias = true
    }

    fun update() {
        if (size.isNaN())
            size = GameView.size * 0.15f

        paint.strokeWidth = if (size * 0.03f < 3) 3f else size * 0.03f
    }

    fun draw(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE

        canvas.drawRoundRect(
            RectF(position.x, position.y, position.x + size, position.y + size),
            size * 0.1f,
            size * 0.1f,
            paint
        )

        paint.style = Paint.Style.FILL

        if (toggled || imageOff != null) {
            paint.color = Color.argb(if (touchDown) 64 else 32, 255, 255, 255)
            canvas.drawRoundRect(
                RectF(position.x, position.y, position.x + size, position.y + size),
                size * 0.1f,
                size * 0.1f,
                paint
            )
        } else if (touchDown) {
            paint.color = Color.argb(24, 255, 255, 255)
            canvas.drawRoundRect(
                RectF(position.x, position.y, position.x + size, position.y + size),
                size * 0.1f,
                size * 0.1f,
                paint
            )
        }

        paint.color = Color.WHITE
        if (toggled || imageOff == null) {
            canvas.drawBitmap(
                imageOn!!,
                null,
                RectF(
                    position.x + size * 0.1f,
                    position.y + size * 0.1f,
                    position.x + size * 0.9f,
                    position.y + size * 0.9f
                ),
                paint
            )
            if (!toggled) {
                canvas.drawLine(
                    position.x + paint.strokeWidth,
                    position.y + size - paint.strokeWidth,
                    position.x + size - paint.strokeWidth,
                    position.y + paint.strokeWidth,
                    paint
                )
            }
        } else {
            canvas.drawBitmap(
                imageOff!!,
                null,
                RectF(
                    position.x + size * 0.1f,
                    position.y + size * 0.1f,
                    position.x + size * 0.9f,
                    position.y + size * 0.9f
                ),
                paint
            )
        }
    }

    fun onTouchEvent(event: MotionEvent) {
        val x: Float = event.x
        val y: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                if (x >= position.x && y >= position.y && x <= position.x + size && y <= position.y + size) {
                    touchDown = true
                }

            MotionEvent.ACTION_MOVE ->
                if (touchDown && !(x >= position.x && y >= position.y && x <= position.x + size && y <= position.y + size)) {
                    touchDown = false
                }

            MotionEvent.ACTION_UP ->
                if (touchDown && x >= position.x && y >= position.y && x <= position.x + size && y <= position.y + size) {
                    touchDown = false
                    toggled = !toggled

                    callback?.invoke(toggled)
                }
        }
    }
}
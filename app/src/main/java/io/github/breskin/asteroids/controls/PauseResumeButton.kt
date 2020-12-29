package io.github.breskin.asteroids.controls

import android.graphics.*
import android.view.MotionEvent
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic

class PauseResumeButton {

    private val paint = Paint()
    private var touchDown = false
    private var pointerId = -1
    private lateinit var playShape: Path

    var paused = false
    var translation = 0f

    init {
        paint.isAntiAlias = true
    }

    fun update(logic: GameLogic) {
        logic.gamePaused = paused
    }

    fun draw(canvas: Canvas) {
        val size = GameView.size * 0.09f
        val position = PointF(GameView.viewWidth * 0.98f - size, GameView.viewWidth * 0.02f - translation)

        if (!this::playShape.isInitialized) {
            playShape = Path()
            playShape.moveTo(size * 0.05f, 0f)
            playShape.lineTo(size * 0.05f, size * 0.6f)
            playShape.lineTo(size * 0.5f, size * 0.3f)
        }

        paint.color = Color.argb(if (touchDown) 64 else 24, 255, 255, 255)
        canvas.drawRoundRect(position.x, position.y, position.x + size, position.y + size, size * 0.1f, size * 0.1f, paint)

        paint.color = Color.rgb(200, 200, 200)

        if (!paused) {
            canvas.drawRoundRect(position.x + size * 0.25f, position.y + size * 0.2f, position.x + size * 0.41667f, position.y + size * 0.8f, size * 0.05f, size * 0.05f, paint)
            canvas.drawRoundRect(position.x + size * 0.58333f, position.y + size * 0.2f, position.x + size * 0.75f, position.y + size * 0.8f, size * 0.05f, size * 0.05f, paint)
        } else {
            canvas.save()
            canvas.translate(position.x + size * 0.25f, position.y + size * 0.2f)
            canvas.drawPath(playShape, paint)
            canvas.restore()
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val currentPointer = event.getPointerId(event.actionIndex)
        var x: Float = event.getX(event.findPointerIndex(currentPointer))
        var y: Float = event.getY(event.findPointerIndex(currentPointer))

        val size = GameView.size * 0.09f
        val position = PointF(GameView.viewWidth * 0.98f - size, GameView.viewWidth * 0.02f - translation)

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
                        paused = !paused
                    }

                    touchDown = false
                    pointerId = -1
                }
            }
        }

        return false
    }
}
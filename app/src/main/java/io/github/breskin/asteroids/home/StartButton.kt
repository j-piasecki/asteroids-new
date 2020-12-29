package io.github.breskin.asteroids.home

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.objects.Ship
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class StartButton(private val callback: () -> Unit) {

    private val paint = Paint()
    private val ship = Ship()

    var text = ""
    private var radius = 0f
    private var position = PointF(0f, 0f)
    private var touchStart = PointF(0f, 0f)
    private var touchDown = false

    init {
        paint.isAntiAlias = true
    }

    fun update() {
        paint.textSize = GameView.size * 0.16f
    }

    fun draw(canvas: Canvas) {
        if (touchDown)
            paint.color = Color.argb(255, 200, 200, 200)
        else
            paint.color = Color.WHITE

        canvas.drawCircle(position.x, position.y, radius, paint)

        paint.color = Color.BLACK
        canvas.drawText(text, (GameView.viewWidth - paint.measureText(text)) * 0.5f, GameView.viewHeight * 0.5f + paint.textSize + ship.size * ship.scale * 0.5f, paint)

        ship.draw(canvas)
    }

    fun onTouchEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                if (sqrt((position.x - x).pow(2) + (position.y - y).pow(2)) <= radius)
                    touchDown = true

            MotionEvent.ACTION_MOVE ->
                if (sqrt((position.x - x).pow(2) + (position.y - y).pow(2)) > radius)
                    touchDown = false

            MotionEvent.ACTION_UP ->
                if (touchDown) {
                    touchDown = false

                    callback.invoke()
                }
        }
    }

    fun startOpeningAnimation() {
        ship.position.x = GameView.viewWidth * 0.5f
        ship.position.y = GameView.viewHeight + ship.size

        radius = 0f
        position.x = GameView.viewWidth * 0.5f
        position.y = GameView.viewHeight * 0.5f
    }

    fun updateOpeningAnimation() {
        ship.position.y -= (ship.position.y - GameView.viewHeight * 0.5f) * 0.05f
        ship.scale = ship.scale + (1.5f - ship.scale) * 0.1f

        radius += (GameView.size * 0.32f - radius) * 0.07f
        position.y += (GameView.viewHeight * 0.5f + ship.size * ship.scale * 0.75f - position.y) * 0.1f
    }

    fun updateClosingAnimation() {
        ship.position.y -= (ship.position.y + ship.size * ship.scale * 4) * 0.04f
        ship.scale = ship.scale - (ship.scale - 0.75f) * 0.1f

        radius -= radius * 0.15f
        position.y -= (ship.position.y + ship.size * ship.scale * 5) * 0.04f
    }

    fun resize(width: Int, height: Int) {
        ship.size = min(width, height) * 0.15f

        position.x = GameView.viewWidth * 0.5f
        ship.position.x = GameView.viewWidth * 0.5f
    }
}
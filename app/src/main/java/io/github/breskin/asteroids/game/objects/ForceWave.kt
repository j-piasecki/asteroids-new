package io.github.breskin.asteroids.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic

class ForceWave(private val position: PointF) {
    private val paint = Paint()

    private var radius = 0f
    private var stroke = 0f

    private var toDelete = false
    val exists: Boolean
        get() = !toDelete

    fun update(logic: GameLogic) {
        stroke = (radius * 0.07f).coerceIn(3f, GameView.size * 0.075f)

        radius += (GameView.size + radius) * 0.015f * GameView.frameTime / 16f * logic.speed
        if (radius > GameView.size * 2.5f)
            toDelete = true

        for (asteroid in logic.space.asteroids) {
            if (radius * radius >= (position.x - asteroid.position.x) * (position.x - asteroid.position.x) + (position.y - asteroid.position.y) * (position.y - asteroid.position.y)) {
                asteroid.explode(logic, split = false)
                asteroid.destroy()
            }
        }
    }

    fun draw(canvas: Canvas, logic: GameLogic) {
        paint.color = Color.argb(192, 0, 192, 255)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = stroke

        val position = logic.camera.translatePosition(logic, this.position)

        canvas.drawCircle(position.x, position.y, radius, paint)
    }
}
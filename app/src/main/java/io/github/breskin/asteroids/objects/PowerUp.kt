package io.github.breskin.asteroids.objects

import android.graphics.*
import android.os.Build
import android.util.Log
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic
import io.github.breskin.asteroids.game.PowerState


class PowerUp(val position: PointF, val type: PowerState.Power) {

    companion object {
        const val lifespan = 5000
        private val paint = Paint()

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                paint.isAntiAlias = true
        }
    }

    private var age = 0f
    var radius = 0f
    var expired = false
    var picked = false

    fun update(logic: GameLogic) {
        age += GameView.frameTime * logic.speed

        if (picked) {
            radius -= radius * 0.15f

            if (radius < 2.5) picked = false
        } else if (!expired) {
            radius += (GameView.size * 0.04f - radius) * 0.1f
        }

        if (age >= lifespan)
            expired = true
    }

    fun draw(canvas: Canvas, logic: GameLogic) {
        val pos = logic.camera.translatePosition(logic, position)
        val stroke = if (radius * 0.05f < 2) 2f else radius * 0.05f
        val alpha = if (!picked && age > lifespan * 0.935f) ((lifespan - age) / (lifespan * 0.065f) * 255).toInt().coerceIn(0, 255) else (radius / (GameView.size * 0.04f) * 255).toInt()

        paint.color = type.getColor(alpha)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(pos.x, pos.y, radius, paint)

        if (!picked) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = stroke
            canvas.drawArc(
                RectF(
                    pos.x - radius - stroke * 3,
                    pos.y - radius - stroke * 3,
                    pos.x + radius + stroke * 3,
                    pos.y + radius + stroke * 3
                ), 270f, -360f * (1f - age.toFloat() / lifespan), false, paint
            )
        }

        paint.color = Color.argb(alpha, 255, 255, 255)
        canvas.drawBitmap(type.getBitmap(), null, RectF(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius), paint)
    }
}
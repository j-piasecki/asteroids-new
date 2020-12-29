package io.github.breskin.asteroids.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.GameLogic

class PiercingBullet(position: PointF, direction: Vector, size: Float, speed: Float) : Bullet(position, direction, size, speed) {

    companion object {
        private val paint = Paint()

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                paint.isAntiAlias = true
        }
    }

    private var secondHit = false

    override fun draw(canvas: Canvas, logic: GameLogic) {
        if (secondHit) {
            super.draw(canvas, logic)
            return
        }

        paint.strokeWidth = size
        paint.color = Color.CYAN

        val position = logic.camera.translatePosition(logic, position)
        val head = getHead(position)

        canvas.drawLine(position.x, position.y, head.x, head.y, paint)
    }

    override fun destroy() {
        if (!secondHit)
            secondHit = true
        else
            super.destroy()
    }
}
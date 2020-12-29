package io.github.breskin.asteroids.game.objects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.GameLogic

class Bullet(position: PointF, direction: Vector, protected val size: Float, speed: Float) : Projectile(position, direction, speed) {

    companion object {
        private val paint = Paint()

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                paint.isAntiAlias = true
        }
    }

    override fun update(logic: GameLogic) {
        super.update(logic)

        if (position.x < -logic.space.width * 0.5f - 100 || position.x > logic.space.width * 0.5f + 100 ||
                position.y < -logic.space.height * 0.5f - 100 || position.y > logic.space.height * 0.5f + 100)
            destroy()
    }

    override fun draw(canvas: Canvas, logic: GameLogic) {
        paint.strokeWidth = size
        paint.color = Color.RED

        val position = logic.camera.translatePosition(logic, position)
        val head = getHead(position)

        canvas.drawLine(position.x, position.y, head.x, head.y, paint)
    }

    fun getHead(position: PointF = this.position) = PointF(position.x + direction.x * size * 6, position.y + direction.y * size * 6)
}
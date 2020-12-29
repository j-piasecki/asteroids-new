package io.github.breskin.asteroids.objects

import android.graphics.*
import android.os.Build
import android.util.Log
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.GameLogic
import kotlin.math.*
import kotlin.random.Random

class Asteroid(position: PointF, direction: Vector, speed: Float, val radius: Float) : Projectile(position, direction, speed), Shape {

    private val rotationSpeed = 1f / radius
    override val points: MutableList<PointF> = mutableListOf()
    private val path = Path()

    var rotation = 0f

    init {
        generateShape()
    }

    companion object {
        private val paint = Paint()

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                paint.isAntiAlias = true
        }
    }

    override fun update(logic: GameLogic) {
        super.update(logic)

        rotation += rotationSpeed * GameView.frameTime / 16f * logic.speed

        for (bullet in logic.space.bullets) {
            val head = bullet.getHead()
            if (bullet.exists && radius * radius * 1.2f >= (position.x - head.x) * (position.x - head.x) + (position.y - head.y) * (position.y - head.y)) {
                if (containsPoint(head, rotation, position) || containsPoint(bullet.position, rotation, position)) {
                    destroy()
                    bullet.destroy()

                    explode(logic)
                    break
                }
            }
        }

        if (position.x < -logic.space.width * 0.5f - radius * 2 || position.x > logic.space.width * 0.5f + radius * 2 ||
                position.y < -logic.space.height * 0.5f - radius * 2 || position.y > logic.space.height * 0.5f + radius * 2)
            destroy()
    }

    override fun draw(canvas: Canvas, logic: GameLogic) {
        val translation = logic.camera.translatePosition(logic, position)

        canvas.save()
        canvas.translate(translation.x, translation.y)
        canvas.rotate(rotation * 180f / PI.toFloat())

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawPath(path, paint)

        val strokeWidth = if (GameView.size * 0.003f < 2) 2f else GameView.size * 0.003f
        paint.style = Paint.Style.STROKE
        paint.color = Color.DKGRAY
        paint.strokeWidth = strokeWidth
        canvas.drawPath(path, paint)

        canvas.restore()
    }

    private fun generateShape() {
        var currentAngle = 0f

        while (currentAngle < 6f) {
            val angle = currentAngle + sqrt(GameView.size / radius * 0.01f) * (Random.nextFloat() * 0.3f + 0.7f)
            val distance = Random.nextFloat() * 0.5f + 0.5f
            val point = PointF(radius * cos(angle) * distance, radius * sin(angle) * distance)

            if (points.isEmpty())
                path.moveTo(point.x, point.y)
            else
                path.lineTo(point.x, point.y)

            points.add(point)

            currentAngle = angle
        }

        path.close()
    }

    fun explode(logic: GameLogic, drop: Boolean = true, split: Boolean = true, sound: Boolean = true) {
        val timeMultiplier = 0.95f.pow(logic.gameTime / 20000 + 1)

        if (drop) {
            logic.score++
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            logic.particleSystem.createInPoint(position, GameView.size * 0.015f + radius * 0.1f, (radius / (GameView.size * 0.3f) * 20 * timeMultiplier).roundToInt(), 330, 330, 330)
        else
            logic.particleSystem.createInPoint(position, GameView.size * 0.015f + radius * 0.1f, (radius / (GameView.size * 0.3f) * 8 * timeMultiplier).roundToInt(), 330, 330, 330)
    }
}
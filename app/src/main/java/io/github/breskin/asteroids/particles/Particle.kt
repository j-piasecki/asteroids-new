package io.github.breskin.asteroids.particles

import android.graphics.*
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.game.GameLogic
import kotlin.math.roundToInt
import kotlin.random.Random

class Particle(private val position: PointF, private var size: Float, cr: Int, cg: Int, cb: Int) {

    companion object {
        private val paint = Paint()
    }

    private var rotation = Random.nextFloat() * 360
    private var rotationSpeed = Random.nextFloat() * 20 - 10f

    private var a = 130 + Random.nextInt(65)
    private var r = (cr - Random.nextInt(90)).coerceIn(1, 255)
    private var g = (cg - Random.nextInt(90)).coerceIn(1, 255)
    private var b = (cb - Random.nextInt(90)).coerceIn(1, 255)

    private var cornerRadius = size * 0.25f

    private val velocity = PointF(GameView.viewWidth / 90 * (Random.nextFloat() - 0.5f), GameView.viewHeight / 90 * (Random.nextFloat() - 0.5f))

    var toDelete = false

    fun update(logic: GameLogic) {
        position.x += velocity.x * GameView.frameTime / 16f * logic.speed
        position.y += velocity.y * GameView.frameTime / 16f * logic.speed

        rotation += rotationSpeed * GameView.frameTime / 16f * logic.speed

        if (rotation > 360) rotation -= 360
        if (rotation < 0) rotation += 360

        if (a >= 2)
            a -= (2 * logic.speed).roundToInt()
        else
            toDelete = true

        if (velocity.y < GameView.viewHeight / 100)
            velocity.y += 0.5f * GameView.frameTime / 16f * logic.speed

        if (size > GameView.viewWidth / 200)
            size -= 0.0255f * GameView.frameTime / 16f * logic.speed
        else
            toDelete = true
    }

    fun draw(canvas: Canvas, logic: GameLogic) {
        paint.color = Color.argb(a, r, g, b)
        canvas.save()

        val point = logic.camera.translatePosition(logic, position)

        canvas.translate(point.x, point.y)
        canvas.rotate(rotation)
        canvas.drawRoundRect(RectF(-size / 2, -size / 2, size / 2, size / 2), cornerRadius, cornerRadius, paint)

        canvas.restore()
    }
}
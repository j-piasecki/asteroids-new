package io.github.breskin.asteroids.game.objects

import android.graphics.*
import android.os.Build
import android.util.Log
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.SoundManager
import io.github.breskin.asteroids.Utils
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.GameLogic
import io.github.breskin.asteroids.game.PowerState
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
        private var dropChance = 0f

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

        dropChance += 0.001f
        logic.score++

        if (drop && Random.nextFloat() < 1 / log2(logic.gameTime * 0.25f)) {
            var power = Random.nextInt(PowerState.Power.AMOUNT)
            while (!logic.player.powerState.canUse(PowerState.Power.get(power)))
                power = (power + 1) % PowerState.Power.AMOUNT

            logic.space.addPowerUp(PowerUp(PointF(position.x, position.y), PowerState.Power.get(power)))
        }

        if (split && radius * 0.65f > GameView.size * 0.06f) {
            spawn(logic)
        }

        if (sound) {
            logic.soundManager?.playSound(SoundManager.SoundEffect.Crash, 0.2f)
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            logic.particleSystem.createInPoint(position, GameView.size * 0.015f + radius * 0.1f, (radius / (GameView.size * 0.3f) * 20 * timeMultiplier).roundToInt(), 330, 330, 330)
        else
            logic.particleSystem.createInPoint(position, GameView.size * 0.015f + radius * 0.1f, (radius / (GameView.size * 0.3f) * 8 * timeMultiplier).roundToInt(), 330, 330, 330)
    }

    private fun spawn(logic: GameLogic) {
        val angle = Math.PI.toFloat() / 6 * (1 + Random.nextFloat())
        var newDirection = Utils.rotateVector(direction, angle)
        var newRadius = radius * (Random.nextFloat() * 0.25f + 0.5f)

        logic.space.addAsteroid(Asteroid(
                PointF(position.x, position.y),
                Vector(newDirection.x, newDirection.y),
                GameView.size / newRadius * GameView.size * 0.0005f * (Random.nextFloat() * 0.25f + 0.75f),
                newRadius
        ))

        newRadius = radius * (Random.nextFloat() * 0.25f + 0.5f)
        newDirection = Utils.rotateVector(direction, -angle)
        logic.space.addAsteroid(Asteroid(
                PointF(position.x, position.y),
                Vector(newDirection.x, newDirection.y),
                GameView.size / newRadius * GameView.size * 0.0005f * (Random.nextFloat() * 0.25f + 0.75f),
                newRadius
        ))
    }
}
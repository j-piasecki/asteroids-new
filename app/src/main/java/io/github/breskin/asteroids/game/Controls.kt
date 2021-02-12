package io.github.breskin.asteroids.game

import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import io.github.breskin.asteroids.Config
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.controls.Joystick
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.objects.Asteroid
import kotlin.math.abs
import kotlin.math.sqrt

class Controls {
    private val moveJoystick = Joystick()
    private val shootJoystick = Joystick()
    private val doubleJoystick = Joystick()

    private var origin = 0f
    private var translation = 0f

    val isMoving: Boolean
        get() = moveJoystick.active || doubleJoystick.active

    init {
        shootJoystick.flat = true
    }

    fun update() {
        moveJoystick.origin = PointF(origin - translation, moveJoystick.origin.y)
        shootJoystick.origin = PointF(GameView.viewWidth - origin + translation, moveJoystick.origin.y)

        doubleJoystick.origin = PointF(doubleJoystick.origin.x, GameView.viewHeight * 0.825f + translation)
    }

    fun draw(canvas: Canvas) {
        if (Config.oneHandedControls) {
            doubleJoystick.draw(canvas)
        } else {
            moveJoystick.draw(canvas)
            shootJoystick.draw(canvas)
        }
    }

    fun apply(logic: GameLogic) {
        val player = logic.player

        if (Config.oneHandedControls) {
            if (doubleJoystick.active) {
                player.velocity.x = doubleJoystick.vector.x
                player.velocity.y = doubleJoystick.vector.y

                val vector = calculateShootingDirection(logic)

                if (vector == null) {
                    player.ship.rotation = doubleJoystick.angle
                    player.shooting = false
                } else {
                    player.ship.rotation = vector.angle
                    player.shootingDirection = vector
                    player.shooting = true
                }
            } else {
                player.shooting = false
            }
        } else {
            if (moveJoystick.active) {
                player.velocity.x = moveJoystick.vector.x
                player.velocity.y = moveJoystick.vector.y

                player.ship.rotation = moveJoystick.angle
            }

            if (shootJoystick.active) {
                player.shootingDirection = shootJoystick.vector
                player.shooting = true
                player.ship.rotation = shootJoystick.angle
            } else {
                player.shooting = false
            }
        }
    }

    private fun calculateShootingDirection(logic: GameLogic): Vector? {
        var closestAsteroid: Asteroid? = null
        var distance = Float.MAX_VALUE

        for (asteroid in logic.space.asteroids) {
            val dst = (logic.player.position.x - asteroid.position.x) * (logic.player.position.x - asteroid.position.x) +
                    (logic.player.position.y - asteroid.position.y) * (logic.player.position.y - asteroid.position.y)

            if (dst < distance) {
                distance = dst
                closestAsteroid = asteroid
            }
        }

        closestAsteroid?.let {
            val position = logic.camera.translatePosition(logic, closestAsteroid.position)

            if (sqrt(distance) > GameView.size * 0.35f && (position.x < 0 || position.y < 0 || position.x > GameView.viewWidth || position.y > GameView.viewHeight))
                return null

            val time = sqrt(distance) / logic.player.bulletSpeed

            return Vector(it.position.x + it.direction.x * it.speed * time - logic.player.position.x,
                it.position.y + it.direction.y * it.speed * time - logic.player.position.y).normalized
        }

        return null
    }

    fun onTouchEvent(event: MotionEvent) {
        if (Config.oneHandedControls) {
            doubleJoystick.onTouchEvent(event)
        } else {
            moveJoystick.onTouchEvent(event)
            shootJoystick.onTouchEvent(event)
        }
    }

    fun reset() {
        moveJoystick.reset()
        shootJoystick.reset()
        doubleJoystick.reset()
    }

    fun startOpeningAnimation() {
        translation = GameView.size * 0.5f

        update()
    }

    fun resize(width: Int, height: Int) {
        doubleJoystick.init(height * 0.1f, width * 0.5f, height * 0.825f)

        if (width > height) {
            moveJoystick.init(height * 0.15f, width * 0.05f + height * 0.15f, height * 0.75f)
            shootJoystick.init(height * 0.15f, width * 0.95f - height * 0.15f, height * 0.75f)

            origin = height * 0.25f
        } else {
            moveJoystick.init(width * 0.15f, width * 0.25f, height - width * 0.23f)
            shootJoystick.init(width * 0.15f, width * 0.75f, height - width * 0.23f)

            origin = width * 0.25f
        }
    }

    fun updateOpeningAnimation() {
        translation -= translation * 0.1f
    }

    fun updateClosingAnimation() {
        translation += (GameView.size * 0.65f - translation) * 0.1f
    }

    fun isClosed() = abs(GameView.size * 0.65f - translation) <= GameView.size * 0.02
}
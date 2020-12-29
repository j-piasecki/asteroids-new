package io.github.breskin.asteroids.game

import android.graphics.Canvas
import android.graphics.PointF
import android.view.MotionEvent
import io.github.breskin.asteroids.Config
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.controls.Joystick
import kotlin.math.abs

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

    fun apply(player: Player) {
        if (Config.oneHandedControls) {
            if (doubleJoystick.active) {
                player.velocity.x = doubleJoystick.vector.x
                player.velocity.y = doubleJoystick.vector.y

                player.ship.rotation = doubleJoystick.angle

                player.shooting = true
                player.shootingDirection = doubleJoystick.vector.normalized
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
        doubleJoystick.init(height * 0.13f, width * 0.5f, height * 0.825f)

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
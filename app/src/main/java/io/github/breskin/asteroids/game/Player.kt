package io.github.breskin.asteroids.game

import android.graphics.Canvas
import android.graphics.PointF
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.Utils
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.objects.Asteroid
import io.github.breskin.asteroids.game.objects.Bullet
import io.github.breskin.asteroids.game.objects.Ship
import kotlin.math.sqrt

class Player {
    private var speed = 10f

    val ship = Ship()
    val powerState = PowerState()

    val position = PointF(0f, 0f)
    val velocity = PointF(0f, 0f)

    var shootingDirection = Vector(0f, 0f)
    var shooting = false
    private var shootTime = 0L

    fun draw(canvas: Canvas) {
        ship.draw(canvas)
    }

    fun update(logic: GameLogic) {
        if (logic.gameFinished)
            return

        position.x += velocity.x * speed * powerState.speedMultiplier * logic.speed
        position.y += velocity.y * speed * powerState.speedMultiplier * logic.speed

        velocity.x *= 0.8f
        velocity.y *= 0.8f

        ship.scale += (powerState.scale - ship.scale) * 0.1f

        if (position.x < -logic.space.width * 0.5f)
            position.x = -logic.space.width * 0.5f

        if (position.y < -logic.space.height * 0.5f)
            position.y = -logic.space.height * 0.5f

        if (position.x > logic.space.width * 0.5f)
            position.x = logic.space.width * 0.5f

        if (position.y > logic.space.height * 0.5f)
            position.y = logic.space.height * 0.5f

        ship.position = logic.camera.getShipPosition(logic)

        tryPickUpPower(logic)
        checkAsteroidsCollision(logic)

        if (shooting)
            shoot(logic)
    }

    private fun shoot(logic: GameLogic): Boolean {
        if (System.currentTimeMillis() - shootTime > powerState.bulletDelay && (shootingDirection.x != 0f || shootingDirection.y != 0f)) {
            val origin = PointF(position.x, position.y - ship.size * 0.5f * ship.scale)
            Utils.rotatePoint(position.x, position.y, ship.rotation, origin)

            val anglePerBullet = powerState.anglePerBullet
            val startAngle = (powerState.bulletSpray - (powerState.bullets - 1) * anglePerBullet) / 2 - powerState.bulletSpray / 2

            for (i in 0 until powerState.bullets) {
                logic.space.addBullet(
                    Bullet(
                        PointF(origin.x, origin.y),
                        Utils.rotateVector(shootingDirection, -(anglePerBullet * i + startAngle) * Math.PI.toFloat() / 180f),
                        ship.size * 0.1f,
                        speed * 3 * powerState.bulletSpeedMultiplier
                    )
                )
            }

            shootTime = System.currentTimeMillis()
            return true
        }

        return false
    }

    fun reset() {
        position.x = 0f
        position.y = 0f

        velocity.x = 0f
        velocity.y = 0f

        speed = ship.size / 11f

        shooting = false
        shootingDirection = Vector(0f, 0f)

        ship.position.x = 0f
        ship.position.y = 0f

        ship.rotation = 0f
        ship.scale = 1f
        ship.alpha = 255

        powerState.reset()
    }

    private fun tryPickUpPower(logic: GameLogic) {
        for (powerUp in logic.space.powerUps) {
            if (!powerUp.expired && sqrt((position.x - powerUp.position.x) * (position.x - powerUp.position.x) + (position.y - powerUp.position.y) * (position.y - powerUp.position.y)) < powerUp.radius + ship.size * ship.scale * 0.45f) {
                powerUp.picked = true
                powerUp.expired = true

                powerState.apply(powerUp.type)
            }
        }
    }

    private fun checkAsteroidsCollision(logic: GameLogic): Boolean {
        for (asteroid in logic.space.asteroids) {
            if (!asteroid.exists || (asteroid.position.x - position.x) * (asteroid.position.x - position.x) + (asteroid.position.y - position.y) * (asteroid.position.y - position.y) > asteroid.radius * asteroid.radius + ship.size * ship.size * 1.25f)
                continue

            for (index in ship.points.indices) {
                if (asteroid.containsPoint(ship.getPoint(index, ship.rotation, position, scale = ship.scale), asteroid.rotation, asteroid.position))
                    if (onCollision(logic, asteroid))
                        return true
            }

            for (index in asteroid.points.indices) {
                if (ship.containsPoint(asteroid.getPoint(index, asteroid.rotation, asteroid.position), ship.rotation, position))
                    if (onCollision(logic, asteroid))
                        return true
            }
        }

        return false
    }

    private fun onCollision(logic: GameLogic, asteroid: Asteroid): Boolean {
        logic.finishGame()

        return true
    }

    fun explode(logic: GameLogic) {
        logic.particleSystem.createInPoint(position, GameView.size * 0.03f, 20, 300, 0, 0)
    }
}
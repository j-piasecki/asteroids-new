package io.github.breskin.asteroids.game

import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.objects.Asteroid
import io.github.breskin.asteroids.objects.Ship

class Player {
    val ship = Ship()

    val position = PointF(0f, 0f)
    val velocity = PointF(0f, 0f)

    fun draw(canvas: Canvas) {
        ship.draw(canvas)
    }

    fun update(logic: GameLogic) {
        if (logic.gameFinished)
            return

        position.x += velocity.x * 10 * logic.speed
        position.y += velocity.y * 10 * logic.speed

        velocity.x *= 0.8f
        velocity.y *= 0.8f

        if (position.x < -logic.space.width * 0.5f)
            position.x = -logic.space.width * 0.5f

        if (position.y < -logic.space.height * 0.5f)
            position.y = -logic.space.height * 0.5f

        if (position.x > logic.space.width * 0.5f)
            position.x = logic.space.width * 0.5f

        if (position.y > logic.space.height * 0.5f)
            position.y = logic.space.height * 0.5f

        ship.position = logic.camera.getShipPosition(logic)
        checkAsteroidsCollision(logic)
    }

    fun reset() {
        position.x = 0f
        position.y = 0f

        velocity.x = 0f
        velocity.y = 0f

        ship.position.x = 0f
        ship.position.y = 0f

        ship.rotation = 0f
        ship.scale = 1f
        ship.alpha = 255
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
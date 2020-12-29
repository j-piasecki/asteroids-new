package io.github.breskin.asteroids.game

import android.graphics.Canvas
import android.graphics.PointF
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.objects.Asteroid
import io.github.breskin.asteroids.objects.Bullet
import io.github.breskin.asteroids.objects.PowerUp
import kotlin.random.Random

class Space(val width: Int, val height: Int) {

    private var counter = 0
    private val asteroidsToAdd = mutableListOf<Asteroid>()
    val asteroids = mutableListOf<Asteroid>()
    val bullets = mutableListOf<Bullet>()
    val powerUps = mutableListOf<PowerUp>()

    fun update(logic: GameLogic) {
        if (counter == 0) {
            counter = 60

            spawnAsteroid()
        }

        counter--

        for (asteroid in asteroids)
            asteroid.update(logic)

        asteroids.addAll(asteroidsToAdd)
        asteroidsToAdd.clear()

        asteroids.removeAll { !it.exists }

        for (bullet in bullets)
            bullet.update(logic)

        bullets.removeAll { !it.exists }

        for (powerUp in powerUps)
            powerUp.update(logic)

        powerUps.removeAll { it.expired && !it.picked }
    }

    fun clear(logic: GameLogic, explode: Boolean = true) {
        if (explode)
            asteroids.forEach { it.explode(logic, drop = false, split = false, sound = false) }

        asteroids.clear()
        bullets.clear()
        powerUps.clear()
    }

    fun addAsteroid(asteroid: Asteroid) {
        asteroidsToAdd.add(asteroid)
    }

    fun addBullet(bullet: Bullet) {
        bullets.add(bullet)
    }

    fun addPowerUp(powerUp: PowerUp) {
        powerUps.add(powerUp)
    }

    private fun spawnAsteroid() {
        val position = PointF()
        val direction = Vector(0f, 0f)
        val radius = GameView.size * 0.05f + GameView.size * Random.nextFloat() * 0.15f
        val speed = GameView.size / radius * GameView.size * 0.0005f * (Random.nextFloat() * 0.25f + 0.75f)

        var fromLeft = false
        var fromTop = false

        when (Random.nextInt(4)) {
            0 -> {
                fromLeft = Random.nextBoolean()
                position.y = -height * 0.5f - radius
                position.x = (if (fromLeft) -width * 0.5f else width * 0.5f) * Random.nextFloat()

                direction.y = Random.nextFloat() * 6 + 1
                direction.x = Random.nextFloat() + 1
                if (!fromLeft)
                    direction.x *= -1
                direction.normalize()
            }

            1 -> {
                fromTop = Random.nextBoolean()
                position.x = width * 0.5f + radius
                position.y = (if (fromTop) -height * 0.5f else height * 0.5f) * Random.nextFloat()

                direction.x = -Random.nextFloat() * 6 + 1
                direction.y = Random.nextFloat() * 2 + 1
                if (!fromTop)
                    direction.y *= -1
                direction.normalize()
            }

            2 -> {
                fromLeft = Random.nextBoolean()
                position.y = height * 0.5f + radius
                position.x = (if (fromLeft) -width * 0.5f else width * 0.5f) * Random.nextFloat()

                direction.y = Random.nextFloat() * 6 + 1
                direction.x = Random.nextFloat() + 1
                if (!fromLeft)
                    direction.x *= -1
                direction.normalize()
            }

            3 -> {
                fromTop = Random.nextBoolean()
                position.x = -width * 0.5f - radius
                position.y = (if (fromTop) -height * 0.5f else height * 0.5f) * Random.nextFloat()

                direction.x = Random.nextFloat() * 6 + 1
                direction.y = Random.nextFloat() * 2 + 1
                if (!fromTop)
                    direction.y *= -1
                direction.normalize()
            }
        }

        asteroids.add(Asteroid(position, direction, speed, radius))
    }

    fun draw(canvas: Canvas, logic: GameLogic) {
        for (powerUp in powerUps) {
            powerUp.draw(canvas, logic)
        }

        for (bullet in bullets) {
            bullet.draw(canvas, logic)
        }

        for (asteroid in asteroids) {
            asteroid.draw(canvas, logic)
        }
    }
}
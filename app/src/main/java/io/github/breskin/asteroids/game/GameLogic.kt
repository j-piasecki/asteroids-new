package io.github.breskin.asteroids.game

import android.graphics.Canvas
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.particles.ParticleSystem

class GameLogic(val particleSystem: ParticleSystem) {

    var space = Space(0, 0)
    val camera = Camera()
    val player = Player()

    var speed = 1f
    var gameTime = 0f
    var score = 0

    var gameFinished = false

    fun update() {
        player.update(this)
        space.update(this)

        gameTime += GameView.frameTime * speed
    }

    fun draw(canvas: Canvas) {
        if (!gameFinished) {
            space.draw(canvas, this)
            player.draw(canvas)
        }
    }

    fun reset() {
        player.reset()

        gameTime = 0f
        gameFinished = false
        score = 0
    }

    fun finishGame() {
        if (!gameFinished) {
            gameFinished = true

            player.explode(this)
            space.clear(this)
        }
    }
}
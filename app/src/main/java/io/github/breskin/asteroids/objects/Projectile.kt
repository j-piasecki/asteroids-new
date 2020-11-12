package io.github.breskin.asteroids.objects

import android.graphics.Canvas
import android.graphics.PointF
import io.github.breskin.asteroids.GameView
import io.github.breskin.asteroids.controls.Vector
import io.github.breskin.asteroids.game.GameLogic

abstract class Projectile(val position: PointF, protected val direction: Vector, protected val speed: Float) {

    private var toDelete = false

    val exists: Boolean
        get() = !toDelete

    open fun update(logic: GameLogic) {
        position.x += direction.x * speed * GameView.frameTime / 16f * logic.speed
        position.y += direction.y * speed * GameView.frameTime / 16f * logic.speed
    }

    abstract fun draw(canvas: Canvas, logic: GameLogic)

    fun destroy() {
        toDelete = true
    }
}